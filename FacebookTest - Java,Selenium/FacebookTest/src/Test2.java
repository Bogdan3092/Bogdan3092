import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Test2 {

	public static void main(String[] args) {
		
		System.getProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		
		driver.get("http://google.com"); // Hit URL on Browser
		System.out.println(driver.getTitle()); //validate if Page title is correct
		System.out.println(driver.getCurrentUrl()); //validate if you are landed on correct url
		System.out.println(driver.getPageSource()); // print page source
		driver.get("http://yahoo.com");
		driver.navigate().back(); // back on the first page
		//driver.navigate().forward();
		//driver.close(); // closes current browser
		//driver.quit(); // closes all the browser by selenium
		
	}

}
