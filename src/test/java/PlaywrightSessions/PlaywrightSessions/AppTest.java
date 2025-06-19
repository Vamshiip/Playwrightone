package PlaywrightSessions.PlaywrightSessions;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;

public class AppTest {
	
	Playwright playwright = Playwright.create();
	Browser browser = playwright.chromium().launch();
	
	
	
}
