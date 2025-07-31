//=======================================================================================
package utils;

import com.microsoft.playwright.*;

public class PlaywrightManager {

    private static final ThreadLocal<Playwright> playwrightThread = new ThreadLocal<>();
    private static final ThreadLocal<Browser> browserThread = new ThreadLocal<>();
    private static final ThreadLocal<Page> pageThread = new ThreadLocal<>();

    public static void init(String browserName) {
        try {
            Playwright playwright = Playwright.create();
            Browser browser;

            switch (browserName.toLowerCase()) {
                case "firefox":
                    browser = playwright.firefox().launch(
                        new BrowserType.LaunchOptions().setHeadless(false));
                    break;
                case "edge":
                    browser = playwright.chromium().launch(
                        new BrowserType.LaunchOptions()
                            .setChannel("msedge")
                            .setHeadless(false)
                    );
                    break;
                default: // chromium
                    browser = playwright.chromium().launch(
                        new BrowserType.LaunchOptions().setHeadless(false));
                    break;
            }

            playwrightThread.set(playwright);
            browserThread.set(browser);
            pageThread.set(browser.newPage());

        } catch (Exception e) {
            System.err.println("❌ Failed to initialize Playwright for browser: " + browserName);
            e.printStackTrace();
            close(); // Clean up if init fails mid-way
            throw new RuntimeException("Playwright initialization failed: " + e.getMessage());
        }
    }


    public static Page getPage() {
        return pageThread.get();
    }

    public static void close() {
        try {
            if (pageThread.get() != null) pageThread.get().close();
            if (browserThread.get() != null) browserThread.get().close();
            if (playwrightThread.get() != null) playwrightThread.get().close();
        } finally {
            playwrightThread.remove();
            browserThread.remove();
            pageThread.remove();
        }
    }
}


//=======================================================================================
//package utils;
//
//import com.microsoft.playwright.*;
//
//import java.nio.file.Paths;
//
//public class PlaywrightManager {
//    private static Playwright playwright;
//    private static Browser browser;
//    private static BrowserContext context;
//    private static Page page;
//
//    public static void initialize() {
//        if (playwright == null) {
//            playwright = Playwright.create();
//            browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
//            context = browser.newContext();
//
//            // ✅ Start tracing
//            context.tracing().start(new Tracing.StartOptions()
//                .setScreenshots(true)
//                .setSnapshots(true)
//                .setSources(true));
//
//            page = context.newPage();
//        }
//    }
//
//    public static Page getPage() {
//        return page;
//    }
//
//    public static BrowserContext getContext() {
//        return context;
//    }
//
//    public static void close() {
//        try {
//            if (context != null) {
//                // ✅ Stop tracing and export zip
//                String traceName = "trace-" + System.currentTimeMillis() + ".zip";
//                context.tracing().stop(new Tracing.StopOptions()
//                    .setPath(Paths.get(traceName)));
//                context.close();
//            }
//            if (browser != null) browser.close();
//            if (playwright != null) playwright.close();
//        } catch (Exception e) {
//            System.err.println("Error during Playwright cleanup: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}

/////=================================================================Multiple browser code===========================
/////=================================================================Multiple browser code===========================
//public class PlaywrightManager {
//    private static ThreadLocal<Playwright> playwright = new ThreadLocal<>();
//    private static ThreadLocal<Browser> browser = new ThreadLocal<>();
//    private static ThreadLocal<BrowserContext> context = new ThreadLocal<>();
//    private static ThreadLocal<Page> page = new ThreadLocal<>();
//    private static ThreadLocal<String> currentBrowser = new ThreadLocal<>();
//
//    public static void init(String browserType) {
//        playwright.set(Playwright.create());
//        currentBrowser.set(browserType.toLowerCase());
//
//        switch (browserType.toLowerCase()) {
//            case "chromium":
//                browser.set(playwright.get().chromium().launch(new BrowserType.LaunchOptions().setHeadless(false)));
//                break;
//            case "firefox":
//                browser.set(playwright.get().firefox().launch(new BrowserType.LaunchOptions().setHeadless(false)));
//                break;
//            case "edge":
//                browser.set(playwright.get().chromium().launch(new BrowserType.LaunchOptions()
//                    .setChannel("msedge")
//                    .setHeadless(false)));
//                break;
//            default:
//                throw new IllegalArgumentException("Unsupported browser: " + browserType);
//        }
//
//        context.set(browser.get().newContext());
//        page.set(context.get().newPage());
//
//        System.out.println("[Browser Started]: " + currentBrowser.get() + " | Thread: " + Thread.currentThread().getName());
//    }
//
//    public static Page getPage() {
//        return page.get();
//    }
//
//    public static String getBrowserName() {
//        return currentBrowser.get();
//    }
//
//    public static void close() {
//        if (context.get() != null) context.get().close();
//        if (browser.get() != null) browser.get().close();
//        if (playwright.get() != null) playwright.get().close();
//    }
//}

/////=================================================================Multiple browser code===========================
/////=================================================================Multiple browser code===========================