import org.apache.commons.collections.list.LazyList;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by admin on 10/10/16.
 */
public class Lesson3Test {
    private String relatedSearchesPattern = "#b_context li.b_ans ul.b_vList li a";
    private String browser = System.getProperty("browser");
    private String huburl = System.getProperty("huburl");
    private String outputdir = System.getProperty("outputdir");
    private String platform = System.getProperty("platform");
    private SystemProperties sysprop = new SystemProperties();
    private WebDriver driver = sysprop.driverInitialization(browser, huburl, outputdir, platform);
    private VerifyMobileView checkMobile = new VerifyMobileView();
    @DataProvider
    public Object[][] getData()
    {
        Reporter.log("Collect Data to DataProvider");
        FileParsing fileParsing = new FileParsing();
        return fileParsing.getParams();
    }

    @BeforeTest
    public void setUp() throws Exception {

    }

    @Test(dataProvider="getData")
    public void checkRelatedSearches(String url, final String searchQuery) throws Exception{


        log("Open main page");
        driver.navigate().to(url);
        if (platform != null && platform.equals("android") || browser != null && browser.equals("chrome-mobile")) {
            checkMobile.isMobile(driver);
            log("Find mobile search field");
            WebElement searchFieldMobile = driver.findElement(By.id("sb_form_q"));
            searchFieldMobile.clear();
            log("Send search query");
            searchFieldMobile.sendKeys(searchQuery);
            log("Submit search query");
            searchFieldMobile.submit();
        }else {
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
        }
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver webDriver){
                return webDriver.getTitle().contains(searchQuery);
            }
        });
        wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.id("b_results"))));
        List<WebElement> relatedSearchResults;
        relatedSearchResults = (driver.findElements(By.cssSelector(relatedSearchesPattern)));
        if (platform != null && platform.equals("android") || browser != null && browser.equals("chrome-mobile")) {
            try
            {
                getCountOfRelatedSearchResults(relatedSearchResults); //Get count of related results links
                clickOnRandomLink(relatedSearchResults);
            }
            catch (Exception e)
            {
                log("Related search results doesn't find in mobile version");
                throw new SkipException("Related search results doesn't find in mobile version");
            }

            }
        else {
            getCountOfRelatedSearchResults(relatedSearchResults); //Get count of related results links
            clickOnRandomLink(relatedSearchResults); //Click on random result
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

    private void getCountOfRelatedSearchResults(List<WebElement> relatedSearchResults) //get count of links in related search results
    {

        int count = 0;
        for (WebElement s : relatedSearchResults) {
            count++;
            log("Count of links in related search results " + count);
            Assert.assertTrue(s.getText().length() > 0);
        }
    }

    private void clickOnRandomLink(List<WebElement> relatedSearchResults)
    {
        Random randomizer = new Random();
        int random = randomizer.nextInt(relatedSearchResults.size()); //Генерируем рандомное число, не превышающее кол-во ссылок
        if (random > 0) {
            String relatedLinkText = relatedSearchResults.get(random).getText();
            System.out.println(relatedLinkText);
            relatedSearchResults.get(random).click();
            WebElement searchField = driver.findElement(By.name("q"));
            System.out.println(searchField.getAttribute("value"));
            log("Compare related link text with text in search field");
            Assert.assertTrue(relatedLinkText.toLowerCase().equals(searchField.getAttribute("value").toLowerCase()));
        } else {
            log("Related links doesn't found");
            throw new SkipException("Related links doesn't found");
        }
    }
}
