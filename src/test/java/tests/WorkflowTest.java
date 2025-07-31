//======================================================================================
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
        System.out.println("[SETUP] Initializing Playwright...");

        // 🟡 You can pass browser from CLI: -Dbrowser=firefox
        String browser = System.getProperty("browser", "chromium");
        PlaywrightManager.init(browser);

        sharedPage = PlaywrightManager.getPage();
        System.out.println("[SETUP] Browser (" + browser + ") is ready.");
    }

    @Test
    @Description("End-to-End Test: ProfNewRequest → FacilityWorkflow → FinalPayment")
    public void testEndToEndWorkflow() {
        Allure.step("Run ProfNewRequest flow", () -> {
            new ProfNewRequest(sharedPage).runFlow();
        });

        Allure.step("Run FacilityWorkflow flow", () -> {
            new FacilityWorkflow(sharedPage).runFlow();
        });

        Allure.step("Run FinalPayment flow", () -> {
            new FinalPayment(sharedPage).runFlow();
        });

        Allure.step("✅ All workflow steps completed", () -> {});
    }

    @AfterAll
    public static void tearDown() {
        System.out.println("[TEARDOWN] Closing Playwright...");
        PlaywrightManager.close();

        System.out.println("[TEARDOWN] Generating Allure report...");
        AllureReportGenerator.generateReport();

        System.out.println("[TEARDOWN] Sending report via email...");
        ReportMailer.sendSummaryReport();
    }
}


//======================================================================================
//package tests;
//
//import io.qameta.allure.Allure;
//import io.qameta.allure.Description;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//
//import com.microsoft.playwright.Page;
//
//import PlaywrightSessions.PlaywrightSessions.ProfNewRequest;
//import PlaywrightSessions.PlaywrightSessions.FacilityWorkflow;
//import PlaywrightSessions.PlaywrightSessions.FinalPayment;
//import utils.PlaywrightManager;
//import utils.AllureReportGenerator;
//import utils.ReportMailer;
//
//public class WorkflowTest {
//
//    private static Page sharedPage;
//
//    @BeforeAll
//    public static void setup() {
//        // Use console or SLF4J logging here, not Allure
//        System.out.println("[SETUP] Initializing Playwright...");
//        PlaywrightManager.initialize();
//        sharedPage = PlaywrightManager.getPage();
//        System.out.println("[SETUP] Browser is ready.");
//    }
//
//    @Test
//    @Description("End-to-End Test: ProfNewRequest → FacilityWorkflow → FinalPayment")
//    public void testEndToEndWorkflow() {
//        Allure.step("Run ProfNewRequest flow", () -> {
//            ProfNewRequest prof = new ProfNewRequest(sharedPage);
//            prof.runFlow();
//        });
//
//        Allure.step("Run FacilityWorkflow flow", () -> {
//            FacilityWorkflow facility = new FacilityWorkflow(sharedPage);
//            facility.runFlow();
//        });
//
//        Allure.step("Run FinalPayment flow", () -> {
//            FinalPayment payment = new FinalPayment(sharedPage);
//            payment.runFlow();
//        });
//
//        Allure.step("✅ All workflow steps completed", () -> {
//            // You can add optional assertions here if needed
//        });
//    }
//
//    @AfterAll
//    public static void tearDown() {
//        System.out.println("[TEARDOWN] Closing Playwright...");
//        PlaywrightManager.close();
//
//        System.out.println("[TEARDOWN] Generating Allure report...");
//        AllureReportGenerator.generateReport();
//
//        System.out.println("[TEARDOWN] Sending report via email...");
//        ReportMailer.sendSummaryReport();
//    }
//}
//==========================================================
/////=================================================================Multiple browser code===========================
/////=================================================================Multiple browser code===========================
//package tests;
//
//import io.qameta.allure.Allure;
//import io.qameta.allure.Description;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
//import org.junit.jupiter.api.parallel.Execution;
//import org.junit.jupiter.api.parallel.ExecutionMode;
//
//import com.microsoft.playwright.Page;
//
//import PlaywrightSessions.PlaywrightSessions.ProfNewRequest;
//import PlaywrightSessions.PlaywrightSessions.FacilityWorkflow;
//import PlaywrightSessions.PlaywrightSessions.FinalPayment;
//import utils.PlaywrightManager;
//import utils.AllureReportGenerator;
//import utils.ReportMailer;
//
//@Execution(ExecutionMode.CONCURRENT)
//public class WorkflowTest {
//
//    @BeforeEach
//    public void setup() {
//        // This will be called per thread/browser in parameterized test
//        System.out.println("[SETUP] Test started...");
//    }
//
//    @ParameterizedTest
//    @ValueSource(strings = {"chromium", "firefox", "edge"})
//    @Description("Cross-Browser End-to-End Test: ProfNewRequest → FacilityWorkflow → FinalPayment")
//    public void testEndToEndWorkflow(String browser) {
//        try {
//            PlaywrightManager.init(browser);
//            Page page = PlaywrightManager.getPage();
//
//            Allure.step("[" + browser + "] Run ProfNewRequest flow", () -> {
//                ProfNewRequest prof = new ProfNewRequest(page);
//                prof.runFlow();
//            });
//
//            Allure.step("[" + browser + "] Run FacilityWorkflow flow", () -> {
//                FacilityWorkflow facility = new FacilityWorkflow(page);
//                facility.runFlow();
//            });
//
//            Allure.step("[" + browser + "] Run FinalPayment flow", () -> {
//                FinalPayment payment = new FinalPayment(page);
//                payment.runFlow();
//            });
//
//            Allure.step("[" + browser + "] ✅ All workflow steps completed", () -> {
//                // Optional assertions can go here
//            });
//
//        } catch (Exception e) {
//            Allure.step("[" + browser + "] ❌ Error occurred: " + e.getMessage());
//            e.printStackTrace();
//        } finally {
//            PlaywrightManager.close();
//        }
//    }
//
//    @AfterAll
//    public static void tearDown() {
//        System.out.println("[TEARDOWN] Generating Allure report...");
//        AllureReportGenerator.generateReport();
//
//        System.out.println("[TEARDOWN] Sending report via email...");
//        ReportMailer.sendSummaryReport();
//    }
//}


/////=================================================================Multiple browser code===========================
/////=================================================================Multiple browser code===========================