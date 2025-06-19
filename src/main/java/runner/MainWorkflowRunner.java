package runner;

import PlaywrightSessions.PlaywrightSessions.ProfNewRequest;
import PlaywrightSessions.PlaywrightSessions.FacilityWorkflow;
import PlaywrightSessions.PlaywrightSessions.FinalPayment;
import utils.PlaywrightManager;
import utils.AllureLogger;
import utils.ReportMailer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.microsoft.playwright.Page;

public class MainWorkflowRunner {

    private static final Logger logger = LoggerFactory.getLogger(MainWorkflowRunner.class);

    public static void main(String[] args) {
        logger.info("Starting Main Workflow...");

        try {
            PlaywrightManager.initialize();
            Page sharedPage = PlaywrightManager.getPage();

            AllureLogger.logStep("Initialized Playwright and opened browser.");

            ProfNewRequest profNewRequest = new ProfNewRequest(sharedPage);
            AllureLogger.logStep("Running ProfNewRequest flow.");
            profNewRequest.runFlow();

            FacilityWorkflow facilityWorkflow = new FacilityWorkflow(sharedPage);
            AllureLogger.logStep("Running FacilityWorkflow flow.");
            facilityWorkflow.runFlow();

            FinalPayment finalPayment = new FinalPayment(sharedPage);
            AllureLogger.logStep("Running FinalPayment flow.");
            finalPayment.runFlow();

            AllureLogger.logStep("All steps completed successfully.");
            logger.info("Main Workflow Completed.");

        } catch (Exception e) {
            AllureLogger.attachText("Error", e.getMessage());
            e.printStackTrace();
        } finally {
            PlaywrightManager.close();

            // Auto-generate Allure Report
            AllureLogger.logStep("Generating Allure Report...");
            utils.AllureReportGenerator.generateReport();

            // Send Report via Email
            ReportMailer.sendReport();
        }
    }
}


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
