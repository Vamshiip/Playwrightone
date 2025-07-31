// File: src/main/java/runner/WorkflowExecutor.java
//====================Single /parallel browser code
package runner;

import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.AllureLogger;
import utils.PlaywrightManager;
import PlaywrightSessions.PlaywrightSessions.ProfNewRequest;
import PlaywrightSessions.PlaywrightSessions.FacilityWorkflow;
import PlaywrightSessions.PlaywrightSessions.FinalPayment;

import java.util.UUID;

public class WorkflowExecutor {
    private static final Logger logger = LoggerFactory.getLogger(WorkflowExecutor.class);

    public static void runWorkflow(String browser) {
        String threadName = Thread.currentThread().getName();
        logger.info("🚀 Running workflow in browser: {} [Thread: {}]", browser, threadName);

        // ✅ Step 1: Set unique results dir for this thread/browser
        String resultsDir = "allure-results-" + browser + "-" + threadName;
        System.setProperty("allure.results.directory", resultsDir);

        // ✅ Step 2: Create new AllureLifecycle (optional if not using custom one)
        AllureLifecycle lifecycle = Allure.getLifecycle();
        String uuid = UUID.randomUUID().toString();

        TestResult result = new TestResult()
                .setUuid(uuid)
                .setName("Full Workflow - " + browser)
                .setFullName("runner.WorkflowExecutor.runWorkflow")
                .setStatus(Status.PASSED);

        lifecycle.scheduleTestCase(result);
        lifecycle.startTestCase(uuid);

        try {
            AllureLogger.safeStep("🧪 Starting test flow in browser: " + browser);
            PlaywrightManager.init(browser);
            Page page = PlaywrightManager.getPage();
            AllureLogger.safeStep("✅ Playwright launched for: " + browser);

            new ProfNewRequest(page).runFlow();
            new FacilityWorkflow(page).runFlow();
            new FinalPayment(page).runFlow();

            AllureLogger.safeStep("✅ Workflow completed successfully in: " + browser);

        } catch (Exception e) {
            result.setStatus(Status.FAILED);
            result.setStatusDetails(new StatusDetails().setMessage(e.getMessage()));
            AllureLogger.attachText("Exception Stack", e.toString());
            logger.error("❌ Exception in workflow [{}]: ", browser, e);
        } finally {
            lifecycle.stopTestCase(uuid);
            lifecycle.writeTestCase(uuid);
            PlaywrightManager.close();
        }
    }
}



//===============================================================================
//package runner;
//
//import PlaywrightSessions.PlaywrightSessions.ProfNewRequest;
//import PlaywrightSessions.PlaywrightSessions.FacilityWorkflow;
//import PlaywrightSessions.PlaywrightSessions.FinalPayment;
//import com.microsoft.playwright.Page;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import utils.AllureLogger;
//import utils.PlaywrightManager;
//import utils.ReportMailer;
//
//public class WorkflowExecutor {
//
//    private static final Logger logger = LoggerFactory.getLogger(WorkflowExecutor.class);
//
//    public static void runWorkflow() {
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
//            logger.error("Workflow encountered an error", e);
//        } finally {
//            PlaywrightManager.close();
//
//            AllureLogger.logStep("Generating Allure Report...");
//            utils.AllureReportGenerator.generateReport();
//
//            ReportMailer.sendSummaryReport();
//        }
//    }
//}
//======================================================================
//======================================================================
/////=================================================================Multiple browser code===========================
/////=================================================================Multiple browser code===========================
//package runner;
//
//import PlaywrightSessions.PlaywrightSessions.ProfNewRequest;
//import PlaywrightSessions.PlaywrightSessions.FacilityWorkflow;
//import PlaywrightSessions.PlaywrightSessions.FinalPayment;
//import com.microsoft.playwright.Page;
//import utils.AllureLogger;
//import utils.PlaywrightManager;
//
//public class WorkflowExecutor {
//
//    public static void runWorkflow(String browser) {
//        AllureLogger.logStep("[" + browser + "] Starting workflow...");
//
//        try {
//            PlaywrightManager.init(browser);
//            Page page = PlaywrightManager.getPage();
//
//            AllureLogger.logStep("[" + browser + "] Initialized Playwright and opened browser.");
//
//            ProfNewRequest profNewRequest = new ProfNewRequest(page);
//            AllureLogger.logStep("[" + browser + "] Running ProfNewRequest flow.");
//            profNewRequest.runFlow();
//
//            FacilityWorkflow facilityWorkflow = new FacilityWorkflow(page);
//            AllureLogger.logStep("[" + browser + "] Running FacilityWorkflow flow.");
//            facilityWorkflow.runFlow();
//
//            FinalPayment finalPayment = new FinalPayment(page);
//            AllureLogger.logStep("[" + browser + "] Running FinalPayment flow.");
//            finalPayment.runFlow();
//
//            AllureLogger.logStep("[" + browser + "] All steps completed successfully.");
//            System.out.println("✅ Finished: " + browser + " at " + java.time.LocalTime.now());
//
//
//        } catch (Exception e) {
//            AllureLogger.attachText("[" + browser + "] Error", e.getMessage());
//            throw new RuntimeException("Workflow failed for browser: " + browser, e);
//        }
//    }
//}
/////=================================================================Multiple browser code===========================
/////=================================================================Multiple browser code===========================
