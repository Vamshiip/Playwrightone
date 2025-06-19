package tests;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Page;

import PlaywrightSessions.PlaywrightSessions.ProfNewRequest;
import PlaywrightSessions.PlaywrightSessions.FacilityWorkflow;
import PlaywrightSessions.PlaywrightSessions.FinalPayment;
import utils.PlaywrightManager;
import utils.AllureReportGenerator;
import utils.ReportMailer;

public class WorkflowTest {

    private static Page sharedPage;

    @BeforeAll
    public static void setup() {
        // Use console or SLF4J logging here, not Allure
        System.out.println("[SETUP] Initializing Playwright...");
        PlaywrightManager.initialize();
        sharedPage = PlaywrightManager.getPage();
        System.out.println("[SETUP] Browser is ready.");
    }

    @Test
    @Description("End-to-End Test: ProfNewRequest → FacilityWorkflow → FinalPayment")
    public void testEndToEndWorkflow() {
        Allure.step("Run ProfNewRequest flow", () -> {
            ProfNewRequest prof = new ProfNewRequest(sharedPage);
            prof.runFlow();
        });

        Allure.step("Run FacilityWorkflow flow", () -> {
            FacilityWorkflow facility = new FacilityWorkflow(sharedPage);
            facility.runFlow();
        });

        Allure.step("Run FinalPayment flow", () -> {
            FinalPayment payment = new FinalPayment(sharedPage);
            payment.runFlow();
        });

        Allure.step("✅ All workflow steps completed", () -> {
            // You can add optional assertions here if needed
        });
    }

    @AfterAll
    public static void tearDown() {
        System.out.println("[TEARDOWN] Closing Playwright...");
        PlaywrightManager.close();

        System.out.println("[TEARDOWN] Generating Allure report...");
        AllureReportGenerator.generateReport();

        System.out.println("[TEARDOWN] Sending report via email...");
        ReportMailer.sendReport();
    }
}
