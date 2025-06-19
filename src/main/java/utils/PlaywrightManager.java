package utils;

import com.microsoft.playwright.*;

import java.nio.file.Paths;

public class PlaywrightManager {
    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext context;
    private static Page page;

    public static void initialize() {
        if (playwright == null) {
            playwright = Playwright.create();
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            context = browser.newContext();

            // ✅ Start tracing
            context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));

            page = context.newPage();
        }
    }

    public static Page getPage() {
        return page;
    }

    public static BrowserContext getContext() {
        return context;
    }

    public static void close() {
        try {
            if (context != null) {
                // ✅ Stop tracing and export zip
                String traceName = "trace-" + System.currentTimeMillis() + ".zip";
                context.tracing().stop(new Tracing.StopOptions()
                    .setPath(Paths.get(traceName)));
                context.close();
            }
            if (browser != null) browser.close();
            if (playwright != null) playwright.close();
        } catch (Exception e) {
            System.err.println("Error during Playwright cleanup: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
