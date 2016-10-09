import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by Ириша on 06.10.2016.
 */
public class CountOfLinks {
    WebDriver driver;
    String linkCssPattern = "html body div#b_content ol#b_results li.b_algo h2";

    @DataProvider
    public Object[][] getData()
    {
        log("Collect Data to DataProvider");
        FileParsing fileParsing = new FileParsing();
        return fileParsing.getParams();
    }
    @BeforeTest
    public void setUp() throws Exception {
        String browser = System.getProperty("browser");
        String huburl = System.getProperty("huburl");
        String outputdir = System.getProperty("outputdir");
        String platform = System.getProperty("platform");
        if (outputdir != null)
        {
            System.setProperty("outputDirectory", outputdir);
        }
        if (huburl == null && browser == null) {
            driver = new FirefoxDriver();
        } else if (huburl == null && !browser.contentEquals("null")) {
            if (System.getProperty("browser").equals("chrome")) {
                String driverPath = System.getProperty("chrome.executable");
                if (driverPath == null)
                    throw new SkipException("Path to chrome doesn't found");
                System.setProperty("webdriver.chrome.driver", driverPath);
                driver = new ChromeDriver();
            } else if (System.getProperty("browser").equals("opera")) {
                String driverPath = System.getProperty("opera.executable");
                if (driverPath == null)
                    throw new SkipException("Path to chrome doesn't found");
                System.setProperty("webdriver.opera.driver", driverPath);
                driver = new OperaDriver();
            } else if (System.getProperty("browser").equals("edge")) {
                String driverPath = System.getProperty("edge.executable");
                if (driverPath == null)
                    throw new SkipException("Path to chrome doesn't found");
                System.setProperty("webdriver.edge.driver", driverPath);
                driver = new EdgeDriver();
            }
        }
        else if (huburl != null && browser == null)
        {
            try {
                System.out.println(huburl);
                DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
                capabilities.setCapability("phantomjs.binary.path", "test-classes/phantomjs");
                driver = new RemoteWebDriver(new URL(huburl), capabilities);
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
        }
        else if (huburl != null && browser != null)
        {
            if (browser.equals("chrome")) {
                try {
                    DesiredCapabilities capabilities = DesiredCapabilities.chrome();
                    driver = new RemoteWebDriver(new URL(huburl), capabilities);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            else if (browser.equals("opera"))
            {
                try {
                    DesiredCapabilities capabilities = DesiredCapabilities.operaBlink();
                    driver = new RemoteWebDriver(new URL(huburl), capabilities);
                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }
            }
            else if (browser.equals("phantomjs"))
            {
                try {
                    DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
                    capabilities.setCapability("phantomjs.binary.path", "phantomjs");
                    driver = new RemoteWebDriver(new URL(huburl), capabilities);
                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }
            }
            else if (browser.equals("edge"))
            {
                try {
                    DesiredCapabilities capabilities = DesiredCapabilities.edge();
                    driver = new RemoteWebDriver(new URL(huburl), capabilities);
                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }
            }
        }else if (platform != null && platform.equals("android"))
        {
            log("Initialize RemoteWebDriver");
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

    }

    @Test(dataProvider="getData")
    public void checkResultsCount (String url, final String searchQuery)
    {
        int resultsPerPage = 10;
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
        int counter = 0;
        for (WebElement s: searchResults)
        {
            counter++;
            log("Count of results");
        }
        log("Check count of search query in results title");
        Assert.assertEquals(resultsPerPage, counter); //проверяем наличие 10 ключевых слов в заголовках поисковой выдачи

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
