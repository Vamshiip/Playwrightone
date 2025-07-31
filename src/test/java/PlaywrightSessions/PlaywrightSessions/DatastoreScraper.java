package PlaywrightSessions.PlaywrightSessions;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DatastoreScraper {
    private static final String BASE_URL = "https://console.cloud.google.com/datastore/databases/";
    private static final String KIND = "DomainUser";
    private static final int MAX_RECORDS = 1_000_000;
    private static final int SCROLL_THRESHOLD = 20; // Number of scrolls before checking for repeated rows
    private static final int BATCH_SIZE = 10000;

    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(50));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            try {
                loginToGoogleConsole(page);
                navigateAndRunQuery(page);
                extractDataWithScroll(page);
            } catch (Exception e) {
                System.err.println("❌ Error during execution: " + e.getMessage());
                e.printStackTrace();
            } finally {
                browser.close();
            }
        }
    }

    private static void loginToGoogleConsole(Page page) {
        page.navigate(BASE_URL);
        page.fill("#identifierId", "your-email@gmail.com");
        page.click("#identifierNext");
        page.waitForTimeout(3000);

        page.fill("input[type='password']", "your-password");
        page.click("#passwordNext");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        System.out.println("✅ Logged in successfully.");
    }

    private static void navigateAndRunQuery(Page page) {
        page.waitForSelector("text=Datastore Studio");
        page.click("text=Datastore Studio");
        page.waitForTimeout(3000);

        page.fill("input[aria-label='Kind']", KIND);
        page.click("text=RUN");
        page.waitForSelector("text=Query results");
        System.out.println("🔍 Query executed successfully.");
    }

    private static void extractDataWithScroll(Page page) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Set<String> uniqueRowHashes = new HashSet<>();
        int totalExtracted = 0;
        int scrollCount = 0;
        int fileCounter = 1;
        List<Map<String, String>> currentBatch = new ArrayList<>();

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        while (totalExtracted < MAX_RECORDS) {
            List<ElementHandle> rows = page.querySelectorAll("table tbody tr");
            System.out.println("📄 Page shows " + rows.size() + " rows.");

            boolean newRowFound = false;
            for (ElementHandle row : rows) {
                List<ElementHandle> cells = row.querySelectorAll("td");
                if (cells.size() == 0) continue;

                Map<String, String> record = new HashMap<>();
                for (int i = 0; i < cells.size(); i++) {
                    String key = "header" + (i + 1);
                    try {
                        String value = cells.get(i).innerText().trim();
                        record.put(key, value);
                    } catch (Exception e) {
                        record.put(key, "");
                    }
                }

                // Create a unique hash to avoid duplicates
                String rowHash = record.toString();
                if (!uniqueRowHashes.contains(rowHash)) {
                    currentBatch.add(record);
                    uniqueRowHashes.add(rowHash);
                    totalExtracted++;
                    newRowFound = true;
                }

                if (totalExtracted % BATCH_SIZE == 0) {
                    saveToJson(currentBatch, timestamp, fileCounter++, gson);
                    currentBatch.clear();
                }

                if (totalExtracted >= MAX_RECORDS) break;
            }

            if (!newRowFound && scrollCount > SCROLL_THRESHOLD) {
                System.out.println("✅ No more new rows. Extraction complete.");
                break;
            }

            // Scroll to load more
            page.mouse().wheel(0, 1500);
            page.waitForTimeout(3000);
            scrollCount++;
        }

        // Save final batch
        if (!currentBatch.isEmpty()) {
            saveToJson(currentBatch, timestamp, fileCounter, gson);
        }

        System.out.println("✅ Extraction complete. Total rows: " + totalExtracted);
    }

    private static void saveToJson(List<Map<String, String>> data, String timestamp, int part, Gson gson) {
        String fileName = "output_data_" + timestamp + "_part" + part + ".json";
        try (FileWriter writer = new FileWriter(fileName)) {
            gson.toJson(data, writer);
            System.out.println("💾 Saved batch to " + fileName);
        } catch (IOException e) {
            System.err.println("❌ Failed to save batch to JSON: " + e.getMessage());
        }
    }
}
