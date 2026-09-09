package com.javainstitute.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

/**
 * BaseTest holds everything that every test class needs in common:
 * - the WebDriver instance (controls the Chrome browser)
 * - a WebDriverWait instance (used for explicit waits)
 * - the base URL of the site under test (reusable constant)
 * - setup() opens the browser before each test
 * - tearDown() closes the browser after each test, even if the test fails
 */
public class BaseTest {

    // Reusable class-level constants (avoids repeating the same value everywhere)
    protected static final String BASE_URL = "https://www.demoblaze.com/";
    protected static final int EXPLICIT_WAIT_SECONDS = 10;

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        // WebDriverManager automatically downloads/matches the correct chromedriver version
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        // Uncomment the next line to run without opening a visible browser window
        // options.addArguments("--headless=new");
        options.addArguments("--disable-notifications");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        // Explicit wait: WebDriver will wait UP TO this many seconds for a condition,
        // instead of using a fixed Thread.sleep() delay
        wait = new WebDriverWait(driver, Duration.ofSeconds(EXPLICIT_WAIT_SECONDS));

        driver.get(BASE_URL);
    }

    /**
     * Takes a screenshot and saves it into the screenshots/ folder with a
     * timestamped, test-specific filename. Used as execution evidence.
     */
    protected void takeScreenshot(String testCaseName) {
        try {
            org.openqa.selenium.TakesScreenshot ts = (org.openqa.selenium.TakesScreenshot) driver;
            java.io.File source = ts.getScreenshotAs(org.openqa.selenium.OutputType.FILE);
            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
            java.io.File destination = new java.io.File("screenshots/" + testCaseName + "_" + timestamp + ".png");
            org.apache.commons.io.FileUtils.copyFile(source, destination);
            System.out.println("Screenshot saved: " + destination.getPath());
        } catch (Exception e) {
            System.out.println("Screenshot failed for " + testCaseName + ": " + e.getMessage());
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        // alwaysRun = true guarantees the browser closes even if the test above fails
        if (driver != null) {
            driver.quit();
        }
    }
}
