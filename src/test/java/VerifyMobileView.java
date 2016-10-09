import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;


public class VerifyMobileView {

    void isMobile(WebDriver driver)
    {
        WebElement checkMobile = driver.findElement(By.cssSelector("#mHamburger"));
        Assert.assertTrue(checkMobile.isDisplayed());
    }
}
