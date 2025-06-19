package PlaywrightSessions.PlaywrightSessions;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.OrderIDHandler;

public class FinalPayment {

    private static final Logger logger = LoggerFactory.getLogger(FinalPayment.class);
    private Page page;

    public FinalPayment(Page page) {
        this.page = page;
    }

    public void runFlow() {
        try {
            Allure.step("Login to portal", () -> {
                page.navigate("https://yellow-dune-0dc4f8f1e.5.azurestaticapps.net/sign-in");
                page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email")).fill("varun.prof@yopmail.com");
                page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).fill("Test@123");
                page.locator("#root svg").click();
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Close")).click();
            });

            Allure.step("Search for Order ID", () -> {
                String orderID = OrderIDHandler.getFirstOrderID();
                Locator searchBox = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Search..."));
                searchBox.click();
                searchBox.fill(orderID);
                searchBox.press("Enter");
                page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("QIX-")).click();
            });

            Allure.step("Open payment screen", () -> {
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Make Payment")).click();
            });

            Allure.step("Fill payment form", () -> {
                FrameLocator emailFrame = page.frameLocator("iframe[title='Secure email input frame']");
                emailFrame.getByRole(AriaRole.TEXTBOX, new FrameLocator.GetByRoleOptions().setName("Email")).fill("varun.prof@yopmail.com");

                FrameLocator cardFrame = page.frameLocator("iframe[title='Secure payment input frame']");
                cardFrame.getByRole(AriaRole.TEXTBOX, new FrameLocator.GetByRoleOptions().setName("Card number")).fill("4242 4242 4242 4242");
                cardFrame.getByRole(AriaRole.TEXTBOX, new FrameLocator.GetByRoleOptions().setName("Expiration date MM / YY")).fill("06 / 29");
                cardFrame.getByRole(AriaRole.TEXTBOX, new FrameLocator.GetByRoleOptions().setName("Security code")).fill("456");

                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Pay Now")).click();
                page.waitForTimeout(3000);
            });

            Allure.step("Wait for payment success", () -> {
                Locator successPopup = page.locator("text=Payment Processed Successfully.");
                successPopup.waitFor(new Locator.WaitForOptions().setTimeout(5000));
            });

            Allure.step("Download final document", () -> {
                Download download = page.waitForDownload(() -> {
                    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Download")).click();
                });
                System.out.println("Downloaded file path: " + download.path());
            });

        } catch (Exception e) {
            Allure.step("❌ Exception in FinalPayment flow", () -> {
                Allure.addAttachment("Error", e.getMessage());
                logger.error("Error in FinalPayment", e);
                throw e;
            });
        }
    }
}




//=====================================================================
//package PlaywrightSessions.PlaywrightSessions;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.microsoft.playwright.Browser;
//import com.microsoft.playwright.BrowserContext;
//import com.microsoft.playwright.BrowserType;
//import com.microsoft.playwright.Download;
//import com.microsoft.playwright.FrameLocator;
//import com.microsoft.playwright.Locator;
//import com.microsoft.playwright.Page;
//import com.microsoft.playwright.Playwright;
//import com.microsoft.playwright.options.AriaRole;
//
//import utils.OrderIDHandler;
//import utils.PlaywrightManager;
//
//public class FinalPayment {
//	private Page page;
//
//    public FinalPayment(Page page) {
//        this.page = page;
//    }
//	private static final Logger logger = LoggerFactory.getLogger(ProfNewRequest.class);
//	//public static void main(String[] args) {
//	public static void runFlow() {
////	    try (Playwright playwright = Playwright.create()) {
////	      Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
////	        .setHeadless(false));
////	      BrowserContext context = browser.newContext();
////	      Page page = context.newPage();
//		  Page page = PlaywrightManager.page;
//	      page.navigate("https://yellow-dune-0dc4f8f1e.5.azurestaticapps.net/sign-in");
//	      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email")).click();
//	      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email")).fill("varun.prof@yopmail.com");
//	      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).click();
//	      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).fill("Test@123");
//	      page.locator("#root svg").click();
//	      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();
//	      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Close")).click();
//	      Locator searchBox = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Search..."));
//	      String orderID = OrderIDHandler.getFirstOrderID();
//			searchBox.click();
////			String orderID = OrderIDHandler.getLatestOrderID();
//			searchBox.fill(orderID);
////			searchBox.fill("QIX-726");
//	      
//	      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Search...")).fill(orderID);
//	      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Search...")).press("Enter");
//	      page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("QIX-")).click();
//	      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Make Payment")).click();
//	   
//	   // Fill email inside the email frame
//	      FrameLocator emailFrame = page.frameLocator("iframe[title='Secure email input frame']");
//	      emailFrame.getByRole(AriaRole.TEXTBOX, new FrameLocator.GetByRoleOptions().setName("Email")).click();
//	      emailFrame.getByRole(AriaRole.TEXTBOX, new FrameLocator.GetByRoleOptions().setName("Email")).fill("varun.prof@yopmail.com");
//
//	      // Fill card details inside the payment frame
//	      FrameLocator cardFrame = page.frameLocator("iframe[title='Secure payment input frame']");
//
//	      cardFrame.getByRole(AriaRole.TEXTBOX, new FrameLocator.GetByRoleOptions().setName("Card number")).click();
//	      cardFrame.getByRole(AriaRole.TEXTBOX, new FrameLocator.GetByRoleOptions().setName("Card number")).fill("4242 4242 4242 4242");
//
//	      cardFrame.getByRole(AriaRole.TEXTBOX, new FrameLocator.GetByRoleOptions().setName("Expiration date MM / YY")).click();
//	      cardFrame.getByRole(AriaRole.TEXTBOX, new FrameLocator.GetByRoleOptions().setName("Expiration date MM / YY")).fill("06 / 29");
//
//	      cardFrame.getByRole(AriaRole.TEXTBOX, new FrameLocator.GetByRoleOptions().setName("Security code")).click();
//	      cardFrame.getByRole(AriaRole.TEXTBOX, new FrameLocator.GetByRoleOptions().setName("Security code")).fill("456");
//
//	      // Click the Pay Now button on the main page
//	      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Pay Now")).click();
//	      page.waitForTimeout(3000);
//	      Locator successPopup = page.locator("text=Payment Processed Successfully.");
//	      successPopup.waitFor(new Locator.WaitForOptions().setTimeout(5000));
//	      
//	      Download download = page.waitForDownload(() -> {
//	        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Download")).click();
//	      });
//	    }
//	  }
//
////}
