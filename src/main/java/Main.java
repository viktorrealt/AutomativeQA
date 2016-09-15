import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by admin on 9/14/16.
 */
public class Main {
    public static void main(String[] args) {
        String url = "http://bing.com"; //URL сайта
        final String searchQuery = "automation"; //Поисковый запрос
        int searchResultsCount = 10;
        WebDriver driver = new FirefoxDriver();
        driver.navigate().to(url);

        By inputLocator = By.name("q"); //Создаем локатор поиска по тэгу name
        WebElement input = driver.findElement(inputLocator); //Создаем WebElement и передаем ему inputlocator в качестве параметра
        input.clear(); // очищаем поле ввода
        input.sendKeys(searchQuery);
        //input.submit();
        WebElement searchButton = driver.findElement(By.name("go"));
        searchButton.click();

        WebDriverWait wait = new WebDriverWait(driver, 10);
        //wait.until(ExpectedConditions.elementToBeSelected(inputLocator));
        wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver webDriver){
                return webDriver.getTitle().contains(searchQuery);
            }
        });

        String title = driver.getTitle();
        System.out.println(title);



        for (int i = 1; i <= searchResultsCount; i++)
        {
            WebElement searchResults = wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("//li[" + i + "]/div/h2/a"))));
            System.out.println(searchResults.getText());
        }


        //driver.close() - закрывает только активную вкладку
        driver.quit();

    }
}
