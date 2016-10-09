import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.SystemClock;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


public class LessonFourCheckCachePageUrl {

    private String searchResultsPattern = "li.b_algo div.b_title h2";
    private String browser = System.getProperty("browser");
    private String huburl = System.getProperty("huburl");
    private String outputdir = System.getProperty("outputdir");
    private String platform = System.getProperty("platform");
    private VerifyMobileView checkMobile = new VerifyMobileView();
    private SystemProperties sysprop = new SystemProperties();
    private WebDriver driver = sysprop.driverInitialization(browser, huburl, outputdir, platform);

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
    public void checkCachePagesUrls(String url, final String searchQuery) throws Exception {
        log("Open main page");
        driver.navigate().to(url);
        if (platform != null && platform.equals("android") || browser != null && browser.equals("chrome-mobile")) {
            log("Find mobile search field");
            checkMobile.isMobile(driver);
            WebElement searchFieldMobile = driver.findElement(By.id("sb_form_q"));
            searchFieldMobile.clear();
            searchFieldMobile.sendKeys(searchQuery);
            searchFieldMobile.submit();
        } else {
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
        if (platform != null && platform.equals("android") || browser != null && browser.equals("chrome-mobile")) {
            log("Check for toolbox with link to cached version");
            Assert.assertTrue(driver.findElement(By.className("c_tlbxTrg")).isDisplayed());
            checkCachedUrls(driver);
        } else {
            checkCachedUrls(driver);
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

    private void checkCachedUrls(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        List<WebElement> popup = driver.findElements(By.className("c_tlbxTrg")); //Находим список pop-up со ссылкой на кэш
        List<String> cachedUrls = new ArrayList<String>(); //ссылки на URL в кэше
        List<WebElement> haspopup = driver.findElements(By.cssSelector(".b_caption div.b_attribution[u] cite"));
        for (WebElement s : popup) {
            s.click();
            wait.until(ExpectedConditions.elementToBeClickable(By.className("c_tlbx")));
            WebElement temp = driver.findElement(By.cssSelector("div.c_tlbx div a"));
            cachedUrls.add(temp.getAttribute("href")); //Ссылки на кэшированные страницы
        }
        HashMap<String, String> finalList = new HashMap<String, String>(); //K - CachedUrls V - haspopup.getText();

        Iterator<String> i1 = cachedUrls.iterator(); //Итератор для кешированных URL
        Iterator<WebElement> i2 = haspopup.iterator(); //Итератор для ссылок
        while (i1.hasNext() && i2.hasNext()) { //Помещаем в MAP
            finalList.put(i1.next(), i2.next().getText());
        }

        for (Map.Entry<String, String> entry : finalList.entrySet()) {
            String cacheurl = entry.getKey();
            String urlname = entry.getValue();
            driver.navigate().to(cacheurl);
            WebElement onCachePaheUrl = driver.findElement(By.cssSelector("div.b_vPanel div strong a"));
            Assert.assertTrue(onCachePaheUrl.getText().contains(urlname));
        }
    }

}
