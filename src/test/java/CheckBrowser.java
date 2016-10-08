import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

/**
 * Created by admin on 10/7/16.
 */

public class CheckBrowser {
    @Test
    public void checkBrowser() {
        System.out.println(System.getProperty("browser"));
        System.out.println(System.getProperty("huburl"));
    }

}
