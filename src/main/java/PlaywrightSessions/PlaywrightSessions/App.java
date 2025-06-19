package PlaywrightSessions.PlaywrightSessions;


import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class App {
	
	public static void main(String[] args) { 
	
	Playwright playwright = Playwright.create();
	Browser browser = playwright.chromium().launch();
	Page page = browser.newPage();
    page.navigate("https://yellow-dune-0dc4f8f1e.5.azurestaticapps.net/sign-in");
	String title = page.title();
	
	System.out.println(title);
	}
	
}
/*======
package PlaywrightSessions;

import com.microsoft.playwright.*;

import java.io.IOException;
import java.nio.file.*;

public class FacilityWorkflow {

    private static final String TRACKER_PATH = "step_tracker.txt";
    private static final String FILE_TO_UPLOAD = "C:/Users/admin/eclipse-workspace/PlaywrightSessions/TestFileupload.pdf";

    public static void main(String[] args) throws IOException {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            String lastStep = getLastStep();

            if (lastStep.isEmpty() || lastStep.equals("LOGIN")) {
                performLogin(page);
                saveStep("UPLOAD");
            }

            if (lastStep.equals("UPLOAD")) {
                performUpload(page);
                saveStep("PAYMENT");
            }

            if (lastStep.equals("PAYMENT")) {
                performPayment(page);
                saveStep("DONE");
            }
        }
    }

    private static void performLogin(Page page) {
        page.navigate("https://yellow-dune-0dc4f81e.5.azurestaticapps.net");
        page.getByPlaceholder("Enter email").fill("vinay.kumar@ezroi.com");
        page.getByPlaceholder("Enter password").fill("Ezr@1234");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login")).click();
        System.out.println("Login successful");
    }

    private static void performUpload(Page page) throws IOException {
        page.waitForTimeout(3000);
        page.navigate("https://yellow-dune-0dc4f81e.5.azurestaticapps.net/admin/document-request/13894");
        page.waitForSelector("text=Add Files");

        Locator fileInput = page.locator("input[type='file'][name='files[]']");
        if (!Files.exists(Paths.get(FILE_TO_UPLOAD))) {
            System.err.println("File not found: " + FILE_TO_UPLOAD);
            return;
        }

        fileInput.setInputFiles(Paths.get(FILE_TO_UPLOAD));
        System.out.println("File uploaded");
    }

    private static void performPayment(Page page) {
        // Simulate payment logic here
        System.out.println("Payment complete (dummy step)");
    }

    private static void saveStep(String step) throws IOException {
        Files.write(Paths.get(TRACKER_PATH), step.getBytes());
    }

    private static String getLastStep() throws IOException {
        Path path = Paths.get(TRACKER_PATH);
        return Files.exists(path) ? Files.readString(path).trim() : "LOGIN";
    }
} */
