import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.*;

import java.net.URL;

/**
 * Created by Ириша on 09.10.2016.
 */
public class AndroidVerifyPageIsMobile {
    WebDriver driver;
    String linkCssPattern = "";

    @DataProvider
    public Object[][] getData()
    {
        log("Collect Data to DataProvider");
        FileParsing fileParsing = new FileParsing();
        return fileParsing.getParams();
    }

    @BeforeTest
    public void setUp()
    {
        log("Initialize Driver");
        try {
            DesiredCapabilities capabilities = DesiredCapabilities.android();
            driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capabilities);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new SkipException("Unable to create RemoteWebdriver instance");
        }
    }

    @Test(dataProvider="getData")
    public void androidVerifyPageIsMobile(String url, final String searchQuery)
    {
        driver.navigate().to(url);
        By inputLocator = By.name("q"); //Создаем локатор поиска по тэгу name
        WebElement input = driver.findElement(inputLocator); //Создаем WebElement и передаем ему inputlocator в качестве параметра
        input.clear(); // очищаем поле ввода
        input.sendKeys(searchQuery);
        input.submit();
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
        WebElement checkMobile = driver.findElement(By.cssSelector("#mHamburger"));
        Assert.assertTrue(checkMobile.isDisplayed());
    }

    @AfterTest
    public void closeAll()
    {
        driver.quit();
    }



    private void log(String s)
    {
        Reporter.log(s + "<br>");
    }
}
