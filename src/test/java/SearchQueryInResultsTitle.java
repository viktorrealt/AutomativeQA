import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by admin on 10/7/16.
 */
public class SearchQueryInResultsTitle {
    WebDriver driver;
    String linkCssPattern = "html body div#b_content ol#b_results li.b_algo h2";

    @DataProvider
    public Object[][] getData()
    {
        log("Collect Data to DataProvider");
        FileParsing fileParsing = new FileParsing();
        return fileParsing.getParams();
    }
    @BeforeSuite
    public void setUp()
    {
        log("Initialize Firefox Driver");
        driver = new FirefoxDriver();
    }

    @Test(dataProvider="getData")
    public void checkResultsCount (String url, final String searchQuery)
    {
        driver.navigate().to(url);
        By inputLocator = By.name("q"); //Создаем локатор поиска по тэгу name
        WebElement input = driver.findElement(inputLocator); //Создаем WebElement и передаем ему inputlocator в качестве параметра
        input.clear(); // очищаем поле ввода
        input.sendKeys(searchQuery);
        WebElement searchButton = driver.findElement(By.name("go"));
        searchButton.click();

        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver webDriver){
                return webDriver.getTitle().contains(searchQuery);
            }
        });
        log("sout page title");
        String title = driver.getTitle();
        System.out.println(title);


        wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.id("b_results"))));

        List<WebElement> searchResults;
        searchResults = (driver.findElements(By.cssSelector(linkCssPattern)));
        for (WebElement s: searchResults)
        {
            log("Check search query in results text");
            Assert.assertTrue(s.getText().contains(searchQuery));
        }
        log("Check count of search query in results title");
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
