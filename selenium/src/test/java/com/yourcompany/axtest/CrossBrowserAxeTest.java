package com.yourcompany.axtest;

import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.selenium.AxeBuilder;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class CrossBrowserAxeTest {

    private WebDriver driver;
    private final String browser;
    private final String url;

    public CrossBrowserAxeTest(String browser, String url) {
        this.browser = browser;
        this.url = url;
    }

    @Parameterized.Parameters(name = "{index}: {1} on {0}")
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][] {
            {"chrome", "https://dequeuniversity.com/demo/mars"},
            {"firefox", "https://dequeuniversity.com/demo/mars"},
            {"edge", "https://dequeuniversity.com/demo/mars"},
            {"chrome", "https://dequeuniversity.com/demo/dream"},
            {"firefox", "https://dequeuniversity.com/demo/dream"},
            {"edge", "https://dequeuniversity.com/demo/dream"}
        });
    }

    @Before
    public void setUp() {
        if ("chrome".equalsIgnoreCase(browser)) {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
        } else if ("firefox".equalsIgnoreCase(browser)) {
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver();
        } else if ("edge".equalsIgnoreCase(browser)) {
            WebDriverManager.edgedriver().setup();
            driver = new EdgeDriver();
        }
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @Test
    public void testPageAccessibility() {
        System.out.println("Running test for " + browser + " on URL: " + url);
        driver.get(url);

        try {
            AxeBuilder axeBuilder = new AxeBuilder();
            Results axeResults = axeBuilder.analyze(driver);

            assertTrue("Accessibility violations found on " + url, axeResults.getViolations().isEmpty());
            System.out.println("No accessibility violations found for " + url + " on " + browser + ".");

        } catch (Exception e) {
            System.err.println("An error occurred during the test: " + e.getMessage());
            e.printStackTrace();
            org.junit.Assert.fail("Test failed due to an exception.");
        }
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}