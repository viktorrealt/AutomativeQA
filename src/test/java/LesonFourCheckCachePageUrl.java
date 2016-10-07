import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ириша on 08.10.2016.
 */
public class LesonFourCheckCachePageUrl {

    WebDriver driver;
    String searchResultsPattern = "li.b_algo div.b_title h2";
    CleanUrl clean = new CleanUrl();
    @DataProvider
    public Object[][] getData()
    {
        Reporter.log("Collect Data to DataProvider");
        FileParsing fileParsing = new FileParsing();
        return fileParsing.getParams();
    }

    @BeforeTest
    public void setUp()
    {
        //get param chrome.executable from pom.xml
        String driverPath = System.getProperty("chrome.executable");
        if (driverPath == null)
            throw new SkipException("Path to chrome doesn't found");
        System.setProperty("webdriver.chrome.driver", driverPath);

        driver = new ChromeDriver();
    }

    @org.testng.annotations.Test(dataProvider="getData")
    public void checkResultUrls(String url, final String searchQuery) throws Exception {


        log("Open main page");
        driver.navigate().to(url);
        log("Find input field");
        By inputLocator = By.name("q"); //Создаем локатор поиска по тэгу name
        WebElement input = driver.findElement(inputLocator); //Создаем WebElement и передаем ему inputlocator в качестве параметра
        log("Clear input field");
        input.clear(); // очищаем поле ввода
        log("Send search query");
        input.sendKeys(searchQuery);
        log("Find search button");
        WebElement searchButton = driver.findElement(By.name("go"));
        log("Click on button");
        searchButton.click();
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver webDriver) {
                return webDriver.getTitle().contains(searchQuery);
            }
        });
        wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.id("b_results"))));
        List<WebElement> searchResults;
        searchResults = driver.findElements(By.cssSelector(searchResultsPattern));
        int count = 0;
        for (WebElement s : searchResults) {
            count++;
            log("Count of links in search results " + count);
            Assert.assertTrue(s.getText().length() > 0);
        }
        List<WebElement> searchResultsUrl;
        searchResultsUrl = driver.findElements(By.cssSelector(".b_caption div.b_attribution cite"));
        ArrayList<String> cleanUrlResult = new ArrayList<String>();
        for (WebElement s : searchResultsUrl) {
            cleanUrlResult.add(clean.GetCleanUrl(s.getText()));
        }
        for (String s1 : cleanUrlResult)
        {
            if (s1.length() > 0 && !s1.equals("null")) {
                System.out.println(s1);
                driver.navigate().to(s1);
                log("Navigate to " + s1);
                wait.until(ExpectedConditions.urlContains(s1));
                log("Check " + s1 + " url");
                Assert.assertTrue(s1.contains(driver.getCurrentUrl().substring(7)));
            }
            else
                log("Not an url" + s1);
        }

    }

    @AfterSuite
    public void closeAll()
    {
        driver.quit();
    }

    private void log(String s)
    {
        Reporter.log(s + "<br>");
    }

}
