package utils;

	import com.microsoft.playwright.*;
	import utils.OrderIDHandler; // Your reusable utility class

	public class ExtractAndStoreOrderIDs {
	    public static void main(String[] args) {
	        try (Playwright playwright = Playwright.create()) {
	            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
	            Page page = browser.newPage();

	            // Navigate to your payment screen
	            page.navigate("https://yellow-dune-0dc4f8f1e.5.azurestaticapps.net/paymentScreen/...");
	            page.waitForLoadState();

	         // Step 1: Extract the full payment order text
	            Locator paymentOrderLocator = page.locator("text=Payment for Order:");
	            String orderIdText = paymentOrderLocator.textContent().trim();
	            System.out.println("Raw Text Found: " + orderIdText);

	            // Step 2: Extract and store Order IDs
	            OrderIDHandler.captureAndStoreOrderIDsFromText(orderIdText);

	            // Step 3: Print confirmation
	            System.out.println("Saved Order IDs: " + OrderIDHandler.retrieveOrderIDs());

	            browser.close();
	        }
	    }
	}


