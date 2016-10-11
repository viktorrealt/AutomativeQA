import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByAll;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.*;

import java.util.*;

/**
 * Created by admin on 10/10/16.
 */
public class Lesson5 {
    private String relatedSearchesPattern = "#b_context li.b_ans ul.b_vList li a";
    private String searchResultsPattern = "#b_results li.b_algo h2 a";
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
            goHome(searchQuery, url); //Go to main page and make search query
        }
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver webDriver){
                return webDriver.getTitle().contains(searchQuery);
            }
        });
        wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.id("b_results"))));

        if (platform != null && platform.equals("android") || browser != null && browser.equals("chrome-mobile")) {
            try
            {
                List<WebElement> relatedSearchResults;
                relatedSearchResults = (driver.findElements(By.cssSelector(relatedSearchesPattern)));
                getCountOfRelatedSearchResults(relatedSearchResults); //Get count of related results links
                log("Click on random result");
                clickOnRandomLink(relatedSearchResults);
                goHome(searchQuery, url); //Go to main page and make search query
                List<WebElement> searchResults;
                searchResults = driver.findElements(By.cssSelector(searchResultsPattern));
                getResultsText(searchResults);
                goHome(searchQuery, url); //Go to main page and make search query
                getResultsUrls(searchQuery, url);
                goHome(searchQuery, url); //Go to main page and make search query
                checkCachedUrls(driver);
            }
            catch (Exception e)
            {
                log("Related search results doesn't find in mobile version");
                throw new SkipException("Related search results doesn't find in mobile version");
            }

            }
        else {
            List<WebElement> relatedSearchResults;
            relatedSearchResults = (driver.findElements(By.cssSelector(relatedSearchesPattern)));
            getCountOfRelatedSearchResults(relatedSearchResults); //Get count of related results links
            log("Click on random result");
            clickOnRandomLink(relatedSearchResults);
            goHome(searchQuery, url); //Go to main page and make search query
            List<WebElement> searchResults;
            searchResults = driver.findElements(By.cssSelector(searchResultsPattern));
            getResultsText(searchResults);
            goHome(searchQuery, url); //Go to main page and make search query
            getResultsUrls(searchQuery, url);
            goHome(searchQuery, url); //Go to main page and make search query
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

    private void getCountOfRelatedSearchResults(List<WebElement> relatedSearchResults) //get count of links in related search results
    {

        int count = 0;
        for (WebElement s : relatedSearchResults) {
            count++;
            log("Count of links in related search results " + count);
            Assert.assertTrue(s.getText().length() > 0);
            System.out.println("Count of related search results: " + count);
        }
    }

    private void clickOnRandomLink(List<WebElement> relatedSearchResults)
    {
        Random randomizer = new Random();
        int random = randomizer.nextInt(relatedSearchResults.size()); //Генерируем рандомное число, не превышающее кол-во ссылок
        if (random <= 0) {
            random += 1;
        }
        if (random > 0){
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
    private void getResultsText(List<WebElement> searchResults)
    {
        System.out.println("Size of search results list: " + searchResults.size());
        int count = 0;
        for (WebElement s : searchResults) {
            count++;
            log("Count of links in search results " + count);
            System.out.println("Count of links in search results " + count);
            Assert.assertTrue(s.getText().length() > 0);
            System.out.println(s.getText());
        }
    }

    private void getResultsUrls(String searchQuery, String url)
    {
            for (int i = 1; i <= 10; i++) {
                WebElement element = driver.findElement(By.xpath("(//li[@class=\"b_algo\"]//div[@class=\"b_attribution\" or @class=\"b_snippet\"]/cite)[" + i + "]")); //Убиться тапком, хуже регулярок
                WebElement titleURL = driver.findElement(By.xpath("(//li[@class=\"b_algo\"]//h2/a)[" + i + "]"));
                WebDriverWait wait = new WebDriverWait(driver, 30);
                    String text = element.getText().substring(0, element.getText().length()-5); //Удаляем ... при длинном URL и прочие моменты
                    System.out.println("text: " + text);
                    titleURL.click();
                    wait.until(ExpectedConditions.urlContains(text));
                    String siteUrl = driver.getCurrentUrl().toLowerCase();
                    System.out.println("CurrentURL: " + siteUrl);
                    Assert.assertTrue(siteUrl.toLowerCase().contains(text.toLowerCase()));
                    if (siteUrl.toLowerCase().contains(text.toLowerCase()))
                        System.out.println("ok");
                    else
                        System.out.printf("WTF");
                    goHome(searchQuery, url);
                    wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.id("b_results"))));

            }
/*       for (WebElement s1 : searchResultsUrl)            ////Эта хрень не работает т.к. ищет данные, которые уже не существуют)))
        {
            String text = s1.getText();
            System.out.println(text);
            log("Navigate to " + text);
            driver.navigate().to(text);
            System.out.println("current url: " + driver.getCurrentUrl());
            log("Check " + text + " url");
            Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains(text.toLowerCase()));

        }*/
    }
    private void goHome(String searchQuery, String url)
    {
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

