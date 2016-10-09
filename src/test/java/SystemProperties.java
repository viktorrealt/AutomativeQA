import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.SkipException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class SystemProperties {
    public WebDriver driverInitialization(String browser, String huburl, String outputdir, String platform)
    {
        WebDriver driver;
        if (outputdir != null) {
            System.setProperty("outputDirectory", outputdir);
        }

        if (huburl == null && browser == null && platform == null) {
            driver = new FirefoxDriver();
            return driver;
        } else if (huburl == null && platform == null && !browser.contentEquals("null")) {
            if (System.getProperty("browser").equals("chrome")) {
                String driverPath = System.getProperty("chrome.executable");
                if (driverPath == null)
                    throw new SkipException("Path to chrome doesn't found");
                System.setProperty("webdriver.chrome.driver", driverPath);
                driver = new ChromeDriver();
                return driver;
            } else if (System.getProperty("browser").equals("opera")) {
                String driverPath = System.getProperty("opera.executable");
                if (driverPath == null)
                    throw new SkipException("Path to chrome doesn't found");
                System.setProperty("webdriver.opera.driver", driverPath);
                driver = new OperaDriver();
                return driver;
            } else if (System.getProperty("browser").equals("edge")) {
                String driverPath = System.getProperty("edge.executable");
                if (driverPath == null)
                    throw new SkipException("Path to chrome doesn't found");
                System.setProperty("webdriver.edge.driver", driverPath);
                driver = new EdgeDriver();
                return driver;
            }
            else if (System.getProperty("browser").equals("chrome-mobile")) {
                String driverPath = System.getProperty("chrome.executable");
                if (driverPath == null)
                    throw new SkipException("Path to chrome doesn't found");
                System.setProperty("webdriver.chrome.driver", driverPath);
                Map<String, String> mobileEmulation = new HashMap<String, String>();
                mobileEmulation.put("deviceName", "Google Nexus 5");

                Map<String, Object> chromeOptions = new HashMap<String, Object>();
                chromeOptions.put("mobileEmulation", mobileEmulation);
                DesiredCapabilities capabilities = DesiredCapabilities.chrome();
                capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
                driver = new ChromeDriver(capabilities);
                return driver;
            }
        } else if (huburl != null && browser == null && platform == null) {
            try {
                System.out.println(huburl);
                DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
                capabilities.setCapability("phantomjs.binary.path", "phantomjs");
                driver = new RemoteWebDriver(new URL(huburl), capabilities);
                return driver;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else if (huburl != null && browser != null) {
            if (browser.equals("chrome")) {
                try {
                    DesiredCapabilities capabilities = DesiredCapabilities.chrome();
                    driver = new RemoteWebDriver(new URL(huburl), capabilities);
                    return driver;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } else if (browser.equals("opera")) {
                try {
                    DesiredCapabilities capabilities = DesiredCapabilities.operaBlink();
                    driver = new RemoteWebDriver(new URL(huburl), capabilities);
                    return driver;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } else if (browser.equals("phantomjs")) {
                try {
                    DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
                    capabilities.setCapability("phantomjs.binary.path", "phantomjs");
                    driver = new RemoteWebDriver(new URL(huburl), capabilities);
                    return driver;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } else if (browser.equals("edge")) {
                try {
                    DesiredCapabilities capabilities = DesiredCapabilities.edge();
                    driver = new RemoteWebDriver(new URL(huburl), capabilities);
                    return driver;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            else if (System.getProperty("browser").equals("chrome-mobile")) {
                String driverPath = System.getProperty("chrome.executable");
                if (driverPath == null)
                    throw new SkipException("Path to chrome doesn't found");
                System.setProperty("webdriver.chrome.driver", driverPath);
                Map<String, String> mobileEmulation = new HashMap<String, String>();
                mobileEmulation.put("deviceName", "Google Nexus 5");

                Map<String, Object> chromeOptions = new HashMap<String, Object>();
                chromeOptions.put("mobileEmulation", mobileEmulation);
                DesiredCapabilities capabilities = DesiredCapabilities.chrome();
                capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
                driver = new ChromeDriver(capabilities);
                return driver;
            }
        } else if (platform != null && platform.equals("android"))
        {
            try {
                DesiredCapabilities capabilities = DesiredCapabilities.android();
                driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capabilities);
                return driver;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                throw new SkipException("Unable to create RemoteWebdriver instance");
            }
        }
        return null;
    }
}
