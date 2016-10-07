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
import org.testng.annotations.*;

import java.util.List;
import java.util.Random;

/**
 * Created by admin on 10/7/16.
 */
public class LessonFourChromeClickRandomRelatedResults {
    WebDriver driver;
    String relatedSearchesPattern = "html body div#b_content ol#b_context li.b_ans ul.b_vList li a strong";

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
    public void checkRelatedSearches(String url, final String searchQuery) throws Exception{


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
            public Boolean apply(WebDriver webDriver){
                return webDriver.getTitle().contains(searchQuery);
            }
        });
        wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.cssSelector(relatedSearchesPattern))));
        List<WebElement> relatedSearchResults;
        relatedSearchResults = (driver.findElements(By.cssSelector(relatedSearchesPattern)));
        int count = 0;
        for (WebElement s: relatedSearchResults)
        {
            count++;
            log("Count of links in related search results " + count);
            Assert.assertTrue(s.getText().length() > 0);
        }
        Random randomizer = new Random();
        int random = randomizer.nextInt(5);
            String relatedLinkText = relatedSearchResults.get(random).getText();
            System.out.println(relatedLinkText);
            relatedSearchResults.get(random).click();
            WebElement searchField = driver.findElement(By.name("q"));
            System.out.println(searchField.getAttribute("value"));
            Assert.assertTrue(relatedLinkText.equals(searchField.getAttribute("value")));

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
