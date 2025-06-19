package PlaywrightSessions.PlaywrightSessions;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;

import io.qameta.allure.Allure;
import utils.OrderIDHandler;
import utils.PlaywrightManager;

import java.nio.file.Paths;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfNewRequest {

    private static final Logger logger = LoggerFactory.getLogger(ProfNewRequest.class);
    private Page page;

    public ProfNewRequest(Page page) {
        this.page = page;
    }

    public void runFlow() {
        try {
            Allure.step("Navigate to Sign-In page", () -> {
                page.navigate("https://yellow-dune-0dc4f8f1e.5.azurestaticapps.net/sign-in");
            });

            Allure.step("Login with email and password", () -> {
                page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email")).fill("varun.prof@yopmail.com");
                page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).fill("Test@123");
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();
            });

            Allure.step("Close popup and go to 'New Document Request'", () -> {
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Close")).click();
                page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("New Document Request")).click();
            });

            Allure.step("Fill personal and facility info", () -> {
                page.getByRole(AriaRole.RADIO, new Page.GetByRoleOptions().setName("Medical Facility")).check();
                page.locator(".css-1yt0726").click();
                page.waitForTimeout(2000);
                page.locator("#react-select-16-input").fill("veda");
                page.keyboard().press("Enter");
                page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Veda Medical Facility Green")).click();

                page.locator(".select__input-container").click();
                page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Authorization")).click();

                page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("First Name")).fill("Play");
                page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Last Name")).fill("One");
                page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Date of Birth ex. MM-DD-YYYY")).click();
                page.getByText("2025", new Page.GetByTextOptions().setExact(true)).click();
                page.getByText("2020").click();
                page.getByText("78910111213").click();
            });

            Allure.step("Upload Medical Document", () -> {
                page.locator("div").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^Medical$"))).first().click();
                page.locator("input[type='file']").nth(0).setInputFiles(Paths.get("D:\\NewEra_Automation\\EZROIFramework\\front.png"));
            });

            Allure.step("Upload Billing Document", () -> {
                page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Enter Internal Reference")).fill("M789");
                page.getByRole(AriaRole.RADIO, new Page.GetByRoleOptions().setName("Select Date Range")).check();
                page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("From Date")).click();
                page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Choose Monday, June 2nd,")).click();
                page.getByRole(AriaRole.CHECKBOX).first().check();

                page.locator("div").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^Billing$"))).first().click();
                page.locator("input[type='file']").nth(1).setInputFiles(Paths.get("D:\\NewEra_Automation\\EZROIFramework\\front.png"));
            });

            Allure.step("Upload X-Ray Document", () -> {
                page.getByRole(AriaRole.RADIO, new Page.GetByRoleOptions().setName("Select Date Range")).nth(1).check();
                page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("From Date")).nth(1).click();
                page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Choose Sunday, June 1st,")).click();
                page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("To Date")).nth(1).click();
                page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Choose Wednesday, June 4th,")).click();

                page.locator("div").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^X-Ray$"))).first().click();
                page.locator("input[type='file']").nth(2).setInputFiles(Paths.get("D:\\NewEra_Automation\\EZROIFramework\\front.png"));
                page.locator("input[name=\"refNum_3\"]").fill("X789");
                page.locator("input[name=\"termsAndConditions\"]").check();
            });

            Allure.step("Submit and pay", () -> {
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click();
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Pay and Submit")).click();
                page.waitForTimeout(2000);
            });

            Allure.step("Capture Order ID", () -> {
                String text = page.locator("text=Payment for Order:").textContent().trim();
                OrderIDHandler.captureAndStoreOrderIDsFromText(text);
                System.out.println("📄 Found Order Text: " + text);
                System.out.println("🆔 Latest Order ID: " + OrderIDHandler.getLatestOrderID());
            });

            Allure.step("Fill payment info in iframe", () -> {
                FrameLocator emailFrame = page.frameLocator("iframe[title='Secure email input frame']");
                emailFrame.getByRole(AriaRole.TEXTBOX, new FrameLocator.GetByRoleOptions().setName("Email")).fill("varun.prof@yopmail.com");

                FrameLocator cardFrame = page.frameLocator("iframe[title='Secure payment input frame']");
                cardFrame.getByRole(AriaRole.TEXTBOX, new FrameLocator.GetByRoleOptions().setName("Card number")).fill("4242 4242 4242 4242");
                cardFrame.getByRole(AriaRole.TEXTBOX, new FrameLocator.GetByRoleOptions().setName("Expiration date MM / YY")).fill("06 / 29");
                cardFrame.getByRole(AriaRole.TEXTBOX, new FrameLocator.GetByRoleOptions().setName("Security code")).fill("456");
            //    page.frameLocator("iframe[name=\"__privateStripeFrame1155\"]").getByLabel("Country").selectOption("US");
            //    cardFrame.getByRole(AriaRole.TEXTBOX, new FrameLocator.GetByRoleOptions().setName("Country")).selectOption("India");
            

                Locator payNowButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Pay Now"));
                payNowButton.click();
            
             page.waitForTimeout(3000);

                Locator successPopup = page.locator("text=Payment Processed Successfully.");
                successPopup.waitFor(new Locator.WaitForOptions().setTimeout(5000));
            });

            Allure.step("Sign out from account", () -> {
                page.locator("div").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^Varun KumarCompanyAdmin$"))).nth(3).click();
                page.getByRole(AriaRole.LISTITEM).filter(new Locator.FilterOptions().setHasText("Sign Out")).click();
            });

        } catch (Exception e) {
            Allure.step("❌ Exception in ProfNewRequest flow", () -> {
                Allure.addAttachment("Exception Message", e.getMessage());
                logger.error("Error in ProfNewRequest", e);
                throw e; // So test will fail
            });
        }
    }
}








//========================================================
//package PlaywrightSessions.PlaywrightSessions;
//
//import com.microsoft.playwright.*;
//import com.microsoft.playwright.options.*;
//
//import utils.OrderIDHandler;
//import utils.PlaywrightManager;
//
//import java.nio.file.Paths;
//
//import java.util.regex.Pattern;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class ProfNewRequest {
//	private Page page;
//
//    public ProfNewRequest(Page page) {
//        this.page = page;
//    }
//	private static final Logger logger = LoggerFactory.getLogger(ProfNewRequest.class);
//	public static void runFlow() {
// // public static void main(String[] args) {
////    try (Playwright playwright = Playwright.create()) {
////      Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
////        .setHeadless(false));
////      BrowserContext context = browser.newContext();
////      Page page = context.newPage();
//      Page page = PlaywrightManager.page;
//      page.navigate("https://yellow-dune-0dc4f8f1e.5.azurestaticapps.net/sign-in");
//      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email")).click();
//      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email")).fill("");
//      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email")).click();
//      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email")).fill("varun.prof@yopmail.com");
//      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).click();
//      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).fill("Test@123");
//      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();
//    //  page.navigate("https://yellow-dune-0dc4f8f1e.5.azurestaticapps.net/orders/readyforpayment");
//      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Close")).click();
//      page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("New Document Request")).click();
//      page.getByRole(AriaRole.RADIO, new Page.GetByRoleOptions().setName("Medical Facility")).check();
//      page.locator(".css-1yt0726").click();
//      Locator Facility = page.locator("#react-select-16-input");
//      Facility.fill("veda");
//      Facility.press("Enter");
//      page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Veda Medical Facility Green")).click();
//      page.locator(".select__input-container").click();
//      page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Authorization")).click();
//      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("First Name")).click();
//      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("First Name")).fill("Play");
//      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Last Name")).click();
//      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Last Name")).fill("One");
//      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Date of Birth ex. MM-DD-YYYY")).click();
//      page.getByText("2025", new Page.GetByTextOptions().setExact(true)).click();
//      page.getByText("2020").click();
//      page.getByText("78910111213").click();
// 
//
//   // Upload Medical Document
//      page.locator("div").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^Medical$"))).first().click();
//      Locator uploadInputMedical = page.locator("input[type='file']").nth(0);
//      uploadInputMedical.setInputFiles(Paths.get("D:\\NewEra_Automation\\EZROIFramework\\front.png"));
//      System.out.println("✅ Uploaded Medical Document");
//
//      
//      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Enter Internal Reference")).click();
//      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Enter Internal Reference")).fill("M789");
//      page.getByRole(AriaRole.RADIO, new Page.GetByRoleOptions().setName("Select Date Range")).check();
//      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("From Date")).click();
//      page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Choose Monday, June 2nd,")).click();
//      page.getByRole(AriaRole.CHECKBOX).first().check();
//      page.waitForTimeout(1000);
//  
//      page.locator("div").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^Billing$"))).first().click();
//      Locator uploadInputBilling = page.locator("input[type='file']").nth(1);
//      uploadInputBilling.setInputFiles(Paths.get("D:\\NewEra_Automation\\EZROIFramework\\front.png"));
//      System.out.println("✅ Uploaded Billing Document");
//      
//      
//      page.getByRole(AriaRole.RADIO, new Page.GetByRoleOptions().setName("Select Date Range")).nth(1).check();
//      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("From Date")).nth(1).click();
//      page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Choose Sunday, June 1st,")).click();
//      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("To Date")).nth(1).click();
//      page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Choose Wednesday, June 4th,")).click();
//      page.waitForTimeout(1000);
//   // Upload X-Ray Document
//      page.locator("div").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^X-Ray$"))).first().click();
//      Locator uploadInputXray = page.locator("input[type='file']").nth(2);
//      uploadInputXray.setInputFiles(Paths.get("D:\\NewEra_Automation\\EZROIFramework\\front.png"));
//      System.out.println("✅ Uploaded X-Ray Document");
//      
//      page.locator("input[name=\"refNum_3\"]").click();
//      page.locator("input[name=\"refNum_3\"]").fill("X789");
//      page.locator("input[name=\"termsAndConditions\"]").check();
// 
//      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click();
////      page.navigate("https://yellow-dune-0dc4f8f1e.5.azurestaticapps.net/paymentOrderSummary/");
//      
//      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Pay and Submit")).click();
//      
//      page.waitForTimeout(2000);
//   // Step 1: Get the full text near 'Payment for Order'
//      String text = page.locator("text=Payment for Order:").textContent().trim();
//      System.out.println("📄 Found Order Text: " + text);
//
//      // Step 2: Capture & store to Excel + CSV
//      OrderIDHandler.captureAndStoreOrderIDsFromText(text);
//
//      // Optional: Retrieve later
//      String FirstOrderID = OrderIDHandler.getFirstOrderID();
//      String latestOrderID = OrderIDHandler.getLatestOrderID();
//      
//      System.out.println("🆔 Latest Order ID: " + latestOrderID);
//      
// //     page.waitForURL("**/paymentScreen**");
//      //https://yellow-dune-0dc4f8f1e.5.azurestaticapps.net/paymentScreen/277e98f1-810c-487a-abad-3c18a7c797b5?newRequest=true
//
//   // Fill email inside the email frame
//      FrameLocator emailFrame = page.frameLocator("iframe[title='Secure email input frame']");
//      emailFrame.getByRole(AriaRole.TEXTBOX, new FrameLocator.GetByRoleOptions().setName("Email")).click();
//      emailFrame.getByRole(AriaRole.TEXTBOX, new FrameLocator.GetByRoleOptions().setName("Email")).fill("varun.prof@yopmail.com");
//
//      // Fill card details inside the payment frame
//      FrameLocator cardFrame = page.frameLocator("iframe[title='Secure payment input frame']");
//
//      cardFrame.getByRole(AriaRole.TEXTBOX, new FrameLocator.GetByRoleOptions().setName("Card number")).click();
//      cardFrame.getByRole(AriaRole.TEXTBOX, new FrameLocator.GetByRoleOptions().setName("Card number")).fill("4242 4242 4242 4242");
//
//      cardFrame.getByRole(AriaRole.TEXTBOX, new FrameLocator.GetByRoleOptions().setName("Expiration date MM / YY")).click();
//      cardFrame.getByRole(AriaRole.TEXTBOX, new FrameLocator.GetByRoleOptions().setName("Expiration date MM / YY")).fill("06 / 29");
//
//      cardFrame.getByRole(AriaRole.TEXTBOX, new FrameLocator.GetByRoleOptions().setName("Security code")).click();
//      cardFrame.getByRole(AriaRole.TEXTBOX, new FrameLocator.GetByRoleOptions().setName("Security code")).fill("456");
//
//      // Click the Pay Now button on the main page
//      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Pay Now")).click();
//      page.waitForTimeout(3000);
//      Locator successPopup = page.locator("text=Payment Processed Successfully.");
//      successPopup.waitFor(new Locator.WaitForOptions().setTimeout(5000));
//      System.out.println("✅ " +successPopup);
//
//      page.locator("div").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^Varun KumarCompanyAdmin$"))).nth(3).click();
//      page.getByRole(AriaRole.LISTITEM).filter(new Locator.FilterOptions().setHasText("Sign Out")).click();
//    }
//  }
////}
