package PlaywrightSessions.PlaywrightSessions;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.OrderIDHandler;

import java.nio.file.Paths;
import java.util.UUID;
import java.util.regex.Pattern;

public class FacilityWorkflow {

    private static final Logger logger = LoggerFactory.getLogger(FacilityWorkflow.class);
    private Page page;

    public FacilityWorkflow(Page page) {
        this.page = page;
    }

    public void runFlow() {
        try {
            safeStep("Facility Login", () -> {
                page.navigate("https://yellow-dune-0dc4f8f1e.5.azurestaticapps.net/sign-in");
                page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email")).fill("vamshi.bo@yopmail.com");
                page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).fill("Test@123");
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Skip")).click();
            });

            safeStep("Search and Open Order ID", () -> {
                String orderID = OrderIDHandler.getFirstOrderID();
                Locator searchBox = page.getByRole(AriaRole.BANNER).getByRole(AriaRole.TEXTBOX, new Locator.GetByRoleOptions().setName("Search..."));
                searchBox.click();
                searchBox.fill(orderID);
                System.out.println(orderID);
                searchBox.press("Enter");
                page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("QIX-")).click();
            });

            safeStep("Approve Order", () -> {
                page.locator("div")
                    .filter(new Locator.FilterOptions().setHasText(Pattern.compile("^Internal Status\\*Select the Current Status$")))
                    .getByRole(AriaRole.IMG)
                    .click();
                page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Approved")).click();
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Update Order Details")).click();
                page.waitForTimeout(3000);
                System.out.println("Status Approved");
            });

            safeStep("Upload Medical File", () -> {
                page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName("Files")).click();
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Upload")).click();
                Locator input = page.locator("input[name='files[]']:not([webkitdirectory])");
                input.setInputFiles(Paths.get("TestFileupload.pdf"));
                page.locator("text=TestFileupload.pdf").waitFor(new Locator.WaitForOptions().setTimeout(10000));
                page.locator("button:has-text('Upload 1 file')").click();
                page.locator("text=File uploaded successfully").waitFor(new Locator.WaitForOptions().setTimeout(10000));
                page.locator("button:has-text('Done')").click();
                System.out.println("File Upload success");
            });

            safeStep("Generate Final PDF", () -> {
                Locator generatePdfButton = page.locator("text=Generate PDF");
                generatePdfButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
                generatePdfButton.click();
            	
            	//==============
            	// Retry click if not enabled yet
            	boolean pdfClicked = false;
            	for (int i = 0; i < 5; i++) {
            	    if (generatePdfButton.isEnabled()) {
            	        try {
            	            generatePdfButton.click(new Locator.ClickOptions().setForce(true));
            	            pdfClicked = true;
            	            System.out.println("Clicked on Generate PDF.");
            	            break;
            	        } catch (PlaywrightException e) {
            	            System.out.println("Generate PDF click failed, retrying... " + e.getMessage());
            	        }
            	    }
            	    page.waitForTimeout(1000); // Wait 1 sec before retry
            	}

            	if (!pdfClicked) {
            	    System.out.println("Generate PDF button was not enabled or clickable after retries. Skipping click.");
            	}

            	// Now wait for a brief moment before refreshing to ensure server starts generating
            	page.waitForTimeout(2000);       	
            	
            	//==============
                

                Locator finalRecordsText = page.locator("text=Final Records");
                Locator refreshButton = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Refresh"));

                int maxAttempts = 10;
                int attempt = 0;
                while (attempt < maxAttempts) {
                    if (finalRecordsText.isVisible()) {
                        System.out.println("✅ Final Records found!");
                        break;
                    }
                    System.out.println("🔄 Refreshing... Attempt " + (attempt + 1));
                    refreshButton.click();
                    page.waitForTimeout(4000);
                    attempt++;
                   
                }

                if (!finalRecordsText.isVisible()) {
                    System.out.println("❌ Final Records not found after " + maxAttempts + " attempts.");
                }

            });

            safeStep("Send Invoice Email", () -> {
                page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName("Invoice")).click();
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Default Invoice Data")).click();
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Email Invoice")).click();
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Send Email")).click();
                System.out.println("Email Invoice sent");
                page.waitForTimeout(2000);
            });

            safeStep("Sign Out", () -> {
                page.locator("div.dropdown-toggle >> svg").click();
                page.locator("li:has-text('Sign Out')").click();
            });

        } catch (Exception e) {
            safeStep("❌ Exception in FacilityWorkflow", () -> {
                Allure.addAttachment("Error", e.getMessage());
                logger.error("Error in FacilityWorkflow", e);
                throw e;
            });
        }
    }
    private void safeStep(String name, Runnable stepLogic) {
        try {
            Allure.getLifecycle().startStep(UUID.randomUUID().toString(), new io.qameta.allure.model.StepResult().setName(name));
            stepLogic.run();
            Allure.getLifecycle().stopStep();
        } catch (Exception e) {
            System.out.println("❌ Error in step: " + name + " - " + e.getMessage());
            Allure.getLifecycle().stopStep();
            throw e;
        }
    }

}









//=============================================================================
//package PlaywrightSessions.PlaywrightSessions;
//
//
//import com.microsoft.playwright.*;
//import com.microsoft.playwright.options.*;
//
//import utils.OrderIDHandler;
//import utils.PlaywrightManager;
//
//import java.nio.file.Paths;
//import java.util.regex.Pattern;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class FacilityWorkflow {
//	private Page page;
//
//    public FacilityWorkflow(Page page) {
//        this.page = page;
//    }
//	private static final Logger logger = LoggerFactory.getLogger(FacilityWorkflow.class);
////	public static void main(String[] args) {
//	public static void runFlow() {
////		try (Playwright playwright = Playwright.create()) {
////			Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
////					.setHeadless(false));
////			BrowserContext context = browser.newContext();
////			Page page = context.newPage();
//			  Page page = PlaywrightManager.page;
//			page.navigate("https://yellow-dune-0dc4f8f1e.5.azurestaticapps.net/sign-in");
//			page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email")).click();
//			page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email")).fill("vamshi.bo@yopmail.com");
//			page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).click();
//			page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).fill("Test@123");
//			//page.locator("iframe[name=\"a-k3qq1d1l8awn\"]").contentFrame().getByRole(AriaRole.CHECKBOX, new FrameLocator.GetByRoleOptions().setName("I'm not a robot")).click();
//			page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();
//			page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Skip")).click();
//			
//			Locator searchBox = page.getByRole(AriaRole.BANNER).getByRole(AriaRole.TEXTBOX, new Locator.GetByRoleOptions().setName("Search..."));
//			String orderID = OrderIDHandler.getFirstOrderID();
//			searchBox.click();
////			String orderID = OrderIDHandler.getLatestOrderID();
//			searchBox.fill(orderID);
////			searchBox.fill("QIX-726");
//			searchBox.press("Enter");
//			page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("QIX-")).click();
//		
//			page.locator("div").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^Internal Status\\*Select the Current Status$"))).getByRole(AriaRole.IMG).click();
//			page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Approved")).click();
//			page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Update Order Details")).click();
//			page.waitForTimeout(3000);
//			System.out.println("Status Approved");
//
//
//			page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName("Files")).click();
//			page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Upload")).click();
////			page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("browse files")).click();
//
//			// Set the file in the correct input
//			Locator input = page.locator("input[name='files[]']:not([webkitdirectory])");
//			input.setInputFiles(Paths.get("TestFileupload.pdf"));
//
//			// Wait for file name to be visible (optional)
//			page.locator("text=TestFileupload.pdf").waitFor(new Locator.WaitForOptions().setTimeout(10000));
//
//			// ✅ Click Upload or Done to close the popup
//			page.locator("button:has-text('Upload 1 file')").click();  // Or "Done", "Continue", based on UI
//
//			// Wait for upload success
//			page.locator("text=File uploaded successfully").waitFor(new Locator.WaitForOptions().setTimeout(10000));
//			page.locator("button:has-text('Done')").click(); 
//			System.out.println("File Upload success");
//			
//			Locator generatePdfButton = page.locator("text=Generate PDF");
//			
//			// Wait until visible and enabled
//			generatePdfButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
//
//			// Click with force to ensure success even if something overlaps it
//			//generatePdfButton.click(new Locator.ClickOptions().setForce(true));
//			generatePdfButton.click();
//
//			Locator finalRecordsText = page.locator("text=Final Records");
//            Locator refreshButton = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Refresh"));
//
//            int maxAttempts = 10;
//            int attempt = 0;
//
//            while (attempt < maxAttempts) {
//                if (finalRecordsText.isVisible()) {
//                    System.out.println("✅ Final Records found!");
//                    break;
//                }
//
//                System.out.println("🔄 Refreshing... Attempt " + (attempt + 1));
//                refreshButton.click();
//                page.waitForTimeout(4000); // wait 2 seconds for DOM to update
//                attempt++;
//            }
//
//            if (!finalRecordsText.isVisible()) {
//                System.out.println("❌ Final Records not found after " + maxAttempts + " attempts.");
//            }
//            System.out.println("Final PDF generated");
//
//			page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName("Invoice")).click();
//			page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Default Invoice Data")).click();
//			page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Email Invoice")).click();
//			page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Send Email")).click();
//			System.out.println("Email Invoice sent");
//			page.waitForTimeout(2000);
//			// Click the profile/user menu
//			page.locator("div.dropdown-toggle >> svg").click();
//
//			// Then click on Sign Out
//			page.locator("li:has-text('Sign Out')").click();
//
//		}
//	}
////}
//
//
