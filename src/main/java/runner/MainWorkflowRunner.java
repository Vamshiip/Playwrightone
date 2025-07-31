//===================================================================================
package runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.PlaywrightManager;
import utils.ReportMailer;
import utils.AllureReportGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class MainWorkflowRunner {
    private static final Logger logger = LoggerFactory.getLogger(MainWorkflowRunner.class);

    public static void main(String[] args) {
        cleanAllureResults();

        String mode = System.getProperty("mode", "single");
        String browserList = System.getProperty("browser", "chromium");

        if (mode.equalsIgnoreCase("parallel")) {
            runInParallel(browserList);
        } else {
            runSingle(browserList);
        }

        logger.info("✅ All workflows completed. Generating Allure report...");
        System.out.println("[INFO] Generating Allure Report...");
        AllureReportGenerator.generateReport();

        System.out.println("[INFO] Attempting to send summary email...");
        ReportMailer.sendSummaryReport();
    }

    private static void runInParallel(String browserCsv) {
        List<String> browsers = Arrays.asList(browserCsv.split(","));
        CountDownLatch latch = new CountDownLatch(browsers.size());
        AtomicInteger threadCounter = new AtomicInteger(1);

        for (String browser : browsers) {
            String browserTrimmed = browser.trim().toLowerCase();

            new Thread(() -> {
                int threadNum = threadCounter.getAndIncrement();
                String threadName = browserTrimmed + "-thread-" + threadNum;
                Thread.currentThread().setName(threadName); // ✅ thread name used in WorkflowExecutor

                try {
                    WorkflowExecutor.runWorkflow(browserTrimmed); // ✅ result dir set in that class
                } catch (Exception e) {
                    logger.error("❌ Exception in thread " + threadName, e);
                } finally {
                    PlaywrightManager.close();
                    latch.countDown();
                }
            }).start();
        }

        try {
            latch.await();
            PlaywrightManager.close();
        } catch (InterruptedException e) {
            logger.error("❌ Main thread interrupted while waiting.", e);
        }
    }

    private static void runSingle(String browser) {
        String browserTrimmed = browser.trim().toLowerCase();
        String threadName = browserTrimmed + "-thread-1";
        Thread.currentThread().setName(threadName); // ✅ so result dir becomes: allure-results-browser-thread-1

        try {
            WorkflowExecutor.runWorkflow(browserTrimmed);
        } catch (Exception e) {
            logger.error("❌ Exception during single browser run", e);
        } finally {
            PlaywrightManager.close();
        }
    }

    public static void cleanAllureResults() {
        try {
            // ✅ Delete ALL folders that start with allure-results- (not just one fixed folder)
            Files.list(Paths.get("."))
                .filter(path -> Files.isDirectory(path) && path.getFileName().toString().startsWith("allure-results-"))
                .forEach(path -> {
                    try {
                        Files.walk(path)
                                .sorted(Comparator.reverseOrder())
                                .map(Path::toFile)
                                .forEach(File::delete);
                        System.out.println("🧹 Deleted folder: " + path);
                    } catch (IOException e) {
                        System.out.println("⚠️ Failed to clean: " + path + " → " + e.getMessage());
                    }
                });

            // Optional: Clean merged + report folder too
            Files.deleteIfExists(Paths.get("allure-results-merged"));
            Files.deleteIfExists(Paths.get("allure-report"));
        } catch (IOException e) {
            System.out.println("⚠️ Error cleaning allure result folders: " + e.getMessage());
        }
    }
}



//===================================================================================
//package runner;
//
//import PlaywrightSessions.PlaywrightSessions.ProfNewRequest;
//import PlaywrightSessions.PlaywrightSessions.FacilityWorkflow;
//import PlaywrightSessions.PlaywrightSessions.FinalPayment;
//import utils.PlaywrightManager;
//import utils.AllureLogger;
//import utils.ReportMailer;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.Comparator;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import com.microsoft.playwright.Page;
//
//public class MainWorkflowRunner {
//
//    private static final Logger logger = LoggerFactory.getLogger(MainWorkflowRunner.class);
//
//    public static void main(String[] args) {
//    	
//    	logger.info("🔁 Cleaning allure-results folder...");
//        cleanAllureResults(); // ✅ Clean allure-results before execution
//        
//        
//        logger.info("Starting Main Workflow...");
//
//        try {
//            PlaywrightManager.initialize();
//            Page sharedPage = PlaywrightManager.getPage();
//
//            AllureLogger.logStep("Initialized Playwright and opened browser.");
//
//            ProfNewRequest profNewRequest = new ProfNewRequest(sharedPage);
//            AllureLogger.logStep("Running ProfNewRequest flow.");
//            profNewRequest.runFlow();
//
//            FacilityWorkflow facilityWorkflow = new FacilityWorkflow(sharedPage);
//            AllureLogger.logStep("Running FacilityWorkflow flow.");
//            facilityWorkflow.runFlow();
//
//            FinalPayment finalPayment = new FinalPayment(sharedPage);
//            AllureLogger.logStep("Running FinalPayment flow.");
//            finalPayment.runFlow();
//
//            AllureLogger.logStep("All steps completed successfully.");
//            logger.info("Main Workflow Completed.");
//
//        } catch (Exception e) {
//            AllureLogger.attachText("Error", e.getMessage());
//            e.printStackTrace();
//        } finally {
//            PlaywrightManager.close();
//
//            // Auto-generate Allure Report
//            AllureLogger.logStep("Generating Allure Report...");
//            utils.AllureReportGenerator.generateReport();
//
//            // Send Report via Email
//            ReportMailer.sendSummaryReport();
//        }
//        }
//        
//     // ✅ Allure results cleanup method
//        public static void cleanAllureResults() {
//            try {
//                Path resultsDir = Paths.get("allure-results");
//                if (Files.exists(resultsDir)) {
//                    Files.walk(resultsDir)
//                         .sorted(Comparator.reverseOrder())
//                         .map(Path::toFile)
//                         .forEach(File::delete);
//                    System.out.println("✅ Cleaned existing allure-results folder.");
//                } else {
//                    System.out.println("ℹ️ allure-results folder not found, skipping cleanup.");
//                }
//            } catch (IOException e) {
//                System.out.println("⚠️ Failed to clean allure-results: " + e.getMessage());
//            }
//    }
//}
/////=================================================================Multiple browser code===========================
/////=================================================================Multiple browser code===========================
//package runner;
//
//import utils.AllureLogger;
//import utils.PlaywrightManager;
//import utils.ReportMailer;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//
//public class MainWorkflowRunner {
//
//    private static final Logger logger = LoggerFactory.getLogger(MainWorkflowRunner.class);
//
//    public static void main(String[] args) {
//        logger.info("Starting Parallel Cross-Browser Workflow...");
//
//     // For single-browser run:
// //       List<String> browsers = Arrays.asList("chromium");  // or "firefox", "edge"
//        List<String> browsers = Arrays.asList(System.getProperty("browser", "chromium"));
//
////        List<String> browsers = Arrays.asList("chromium", "firefox", "edge");
//        CountDownLatch latch = new CountDownLatch(browsers.size());
//        
//        
//        
//
//        for (String browser : browsers) {
//            new Thread(() -> {
//                try {
//                	System.out.println("✅ Started: " + browser + " at " + java.time.LocalTime.now());
//
//                    // 🔁 Now using centralized workflow method
//                    WorkflowExecutor.runWorkflow(browser);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    PlaywrightManager.close();
//                    latch.countDown();
//                }
//            }, "Thread-" + browser.toUpperCase()).start();
//        }
//
//        try {
//            latch.await(); // Wait for all threads to complete
//        } catch (InterruptedException e) {
//            logger.error("Main thread interrupted while waiting for browser threads.");
//            e.printStackTrace();
//        }
//
//        logger.info("All browser workflows completed. Generating Allure report...");
//        AllureLogger.logStep("Generating Allure Report...");
//        utils.AllureReportGenerator.generateReport();
//
//        ReportMailer.sendSummaryReport();
//    }
//}

/////=================================================================Multiple browser code===========================
/////=================================================================Multiple browser code===========================
////=======================================================================
/////////////////////////////////////////////////////////////////
//package runner;
//
//import PlaywrightSessions.PlaywrightSessions.ProfNewRequest;
//import PlaywrightSessions.PlaywrightSessions.FacilityWorkflow;
//import PlaywrightSessions.PlaywrightSessions.FinalPayment;
//import utils.PlaywrightManager;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import com.microsoft.playwright.Page;
//
//public class MainWorkflowRunner {
//	
//    private static final Logger logger = LoggerFactory.getLogger(MainWorkflowRunner.class);
//
//    public static void main(String[] args) {
//        logger.info("Starting Main Workflow...");
//
//        try {
//            PlaywrightManager.initialize();
//            Page sharedPage = PlaywrightManager.getPage();
//
//            ProfNewRequest profNewRequest = new ProfNewRequest(sharedPage);
//            profNewRequest.runFlow();
//
//            FacilityWorkflow facilityWorkflow = new FacilityWorkflow(sharedPage);
//            facilityWorkflow.runFlow();
//
//            FinalPayment finalPayment = new FinalPayment(sharedPage);
//            finalPayment.runFlow();
//
//            logger.info("Main Workflow Completed.");
//        } finally {
//            PlaywrightManager.close();
//        }
//    }
//}
