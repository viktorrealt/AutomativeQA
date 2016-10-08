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

import java.util.*;

/**
 * Created by Ириша on 08.10.2016.
 */
public class LessonFourCheckCachePageUrl {

    WebDriver driver;
    String searchResultsPattern = "li.b_algo div.b_title h2";
    CheckBrowser checkBrowser = new CheckBrowser();

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
        String driverPath = System.getProperty("chrome.executable");
        if (driverPath == null)
            throw new SkipException("Path to chrome doesn't found");
        System.setProperty("webdriver.chrome.driver", driverPath);

        driver = new ChromeDriver();
    }

    @org.testng.annotations.Test(dataProvider="getData")
    public void checkCachePagesUrls(String url, final String searchQuery) throws Exception {


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

        for(Map.Entry<String, String> entry: finalList.entrySet())
        {
            String cacheurl = entry.getKey();
            String urlname = entry.getValue();
            driver.navigate().to(cacheurl);
            WebElement onCachePaheUrl = driver.findElement(By.cssSelector("div.b_vPanel div strong a"));
            log("Bing URL: " + urlname);
            log("Cache URL: " + onCachePaheUrl.getText());
            Assert.assertTrue(onCachePaheUrl.getText().contains(urlname));

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
