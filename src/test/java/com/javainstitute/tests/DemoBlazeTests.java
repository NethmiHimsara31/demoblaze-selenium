package com.javainstitute.tests;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

/**
 * DemoBlazeTests contains the five required automated test scenarios (TC01-TC05)
 * for the DemoBlaze web store, as described in the assessment brief.
 */
public class DemoBlazeTests extends BaseTest {

    // Reusable test data as class-level constants
    private static final String SAMSUNG_PRODUCT = "Samsung galaxy s6";
    private static final String NOKIA_PRODUCT = "Nokia lumia 1520";

    // Fictitious checkout data (no real personal/payment information)
    private static final String CHECKOUT_NAME = "Test Student";
    private static final String CHECKOUT_COUNTRY = "Sri Lanka";
    private static final String CHECKOUT_CITY = "Colombo";
    private static final String CHECKOUT_CARD = "4111111111111111";
    private static final String CHECKOUT_MONTH = "12";
    private static final String CHECKOUT_YEAR = "2027";

    // ---------------------------------------------------------------------
    // TC01 - Home Page Smoke Test
    // Verify a non-empty title and displayed PRODUCT STORE heading.
    // ---------------------------------------------------------------------
    @Test(priority = 1)
    public void tc01_homePageSmokeTest() {
        String pageTitle = driver.getTitle();
        System.out.println("TC01 - Page title: " + pageTitle);

        // Assertion 1: title must not be blank
        Assert.assertFalse(pageTitle.isEmpty(), "Page title should not be empty");

        // Locator strategy: id (non-XPath locator)
        WebElement storeHeading = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("nava")));

        // Assertion 2: the PRODUCT STORE heading text is displayed
        Assert.assertTrue(storeHeading.getText().contains("PRODUCT STORE"),
                "Home page should display PRODUCT STORE heading");
        Assert.assertTrue(storeHeading.isDisplayed());

        takeScreenshot("TC01_HomePage");
    }

    // ---------------------------------------------------------------------
    // TC02 - Product Selection
    // Open Phones, select Samsung galaxy s6, verify heading, print price.
    // ---------------------------------------------------------------------
    @Test(priority = 2)
    public void tc02_productSelection() {
        // XPath locator: Phones category link
        WebElement phonesCategory = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@id='itemc' and text()='Phones']")));
        phonesCategory.click();

        // XPath locator: product link found dynamically by its visible text
        WebElement samsungLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(),'" + SAMSUNG_PRODUCT + "')]")));
        samsungLink.click();

        // XPath locator: product name heading on the product detail page
        WebElement productHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[@class='name']")));

        // Assertion: heading matches the product we selected
        Assert.assertEquals(productHeading.getText(), SAMSUNG_PRODUCT,
                "Product heading should match selected product");

        WebElement priceElement = driver.findElement(By.xpath("//h3[@class='price-container']"));
        String price = priceElement.getText();
        System.out.println("TC02 - " + SAMSUNG_PRODUCT + " price: " + price);

        Assert.assertFalse(price.isEmpty(), "Product price should not be empty");

        takeScreenshot("TC02_ProductSelection");
    }

    // ---------------------------------------------------------------------
    // TC03 - Add to Cart
    // Add Samsung galaxy s6, wait for the JS alert, print its text, accept it.
    // ---------------------------------------------------------------------
    @Test(priority = 3)
    public void tc03_addToCart() {
        navigateToProduct(SAMSUNG_PRODUCT);

        // linkText locator (another non-XPath locator strategy)
        WebElement addToCartBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.linkText("Add to cart")));
        addToCartBtn.click();

        // Explicit wait specifically for the JavaScript alert to appear
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();

        String alertText = alert.getText();
        System.out.println("TC03 - Alert text: " + alertText);

        // Assertion: confirm the alert confirms the product was added
        Assert.assertTrue(alertText.contains("Product added"),
                "Alert should confirm product was added");


        alert.accept();

        takeScreenshot("TC03_AlertBeforeAccept");
    }

    // ---------------------------------------------------------------------
    // TC04 - Cart Management
    // Add Samsung + Nokia, count/print cart rows, remove Nokia,
    // verify Samsung remains, print the total.
    // ---------------------------------------------------------------------
    @Test(priority = 4)
    public void tc04_cartManagement() throws InterruptedException {
        addProductToCartByName(SAMSUNG_PRODUCT);
        addProductToCartByName(NOKIA_PRODUCT);

        // Navigate to cart page - id locator
        WebElement cartLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("cartur")));
        cartLink.click();

        // Wait for at least one row to be present before reading the table
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//tbody[@id='tbodyid']/tr")));
        Thread.sleep(1000); // brief pause only to allow the second row to render after navigation

        // findElements() + List<WebElement>: collection of all cart rows
        List<WebElement> cartRows = driver.findElements(By.xpath("//tbody[@id='tbodyid']/tr"));
        System.out.println("TC04 - Cart rows before removal: " + cartRows.size());
        Assert.assertEquals(cartRows.size(), 2, "Cart should contain 2 items before removal");

        // Loop over WebElements: print each product name and price
        for (WebElement row : cartRows) {
            String name = row.findElement(By.xpath("./td[2]")).getText();
            String price = row.findElement(By.xpath("./td[3]")).getText();
            System.out.println("TC04 - Cart item: " + name + " | Price: " + price);
        }

        // Remove Nokia lumia 1520: find its row, click the Delete link in that row
        WebElement nokiaRow = driver.findElement(
                By.xpath("//tbody[@id='tbodyid']/tr[td[2][text()='" + NOKIA_PRODUCT + "']]"));
        nokiaRow.findElement(By.linkText("Delete")).click();

        // Wait until only one row remains
        wait.until(driverRef -> driver.findElements(By.xpath("//tbody[@id='tbodyid']/tr")).size() == 1);

        List<WebElement> remainingRows = driver.findElements(By.xpath("//tbody[@id='tbodyid']/tr"));
        Assert.assertEquals(remainingRows.size(), 1, "One row should remain after deleting Nokia");

        String remainingProduct = remainingRows.get(0).findElement(By.xpath("./td[2]")).getText();
        Assert.assertEquals(remainingProduct, SAMSUNG_PRODUCT,
                "Samsung galaxy s6 should remain in the cart");

        String total = driver.findElement(By.id("totalp")).getText();
        System.out.println("TC04 - Cart total after removal: " + total);
        Assert.assertFalse(total.isEmpty(), "Cart total should not be empty");

        takeScreenshot("TC04_CartAfterRemoval");
    }

    // ---------------------------------------------------------------------
    // TC05 - Checkout Validation
    // First verify invalid result for missing Name/Card, then submit valid data.
    // ---------------------------------------------------------------------
    @Test(priority = 5)
    public void tc05_checkoutValidation() {
        addProductToCartByName(SAMSUNG_PRODUCT);

        WebElement cartLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("cartur")));
        cartLink.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//tbody[@id='tbodyid']/tr")));

        WebElement placeOrderBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[text()='Place Order']")));
        placeOrderBtn.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("orderModal")));

        // --- Invalid attempt: submit with mandatory Name/Card fields left blank ---
        WebElement purchaseBtn = driver.findElement(
                By.xpath("//button[text()='Purchase']"));
        purchaseBtn.click();

        // The site raises a native JavaScript alert listing which fields are missing
        wait.until(ExpectedConditions.alertIsPresent());
        Alert invalidAlert = driver.switchTo().alert();
        String invalidMessage = invalidAlert.getText();
        System.out.println("TC05 - Invalid validation message: " + invalidMessage);
        Assert.assertTrue(invalidMessage.contains("Please fill out Name and Creditcard"),
                "Invalid submission should show a missing Name/Creditcard message");

        // Close the validation alert
        invalidAlert.accept();

        takeScreenshot("TC05_PurchaseSuccess");

        // --- Valid attempt: fill in fictitious data and submit ---
        driver.findElement(By.id("name")).sendKeys(CHECKOUT_NAME);
        driver.findElement(By.id("country")).sendKeys(CHECKOUT_COUNTRY);
        driver.findElement(By.id("city")).sendKeys(CHECKOUT_CITY);
        driver.findElement(By.id("card")).sendKeys(CHECKOUT_CARD);
        driver.findElement(By.id("month")).sendKeys(CHECKOUT_MONTH);
        driver.findElement(By.id("year")).sendKeys(CHECKOUT_YEAR);

        driver.findElement(By.xpath("//button[text()='Purchase']")).click();

        // Wait for the purchase success confirmation
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h2[text()='Thank you for your purchase!']")));

        System.out.println("TC05 - Success message: " + successMessage.getText());
        Assert.assertTrue(successMessage.isDisplayed(),
                "Valid purchase should show the thank-you confirmation");

        driver.findElement(By.xpath("//button[text()='OK']")).click();
    }

    // ---------------------------------------------------------------------
    // Helper methods (reused across tests to avoid duplicated code)
    // ---------------------------------------------------------------------

    /**
     * Navigates from the home page into a given product's detail page.
     * DemoBlaze occasionally re-renders elements via JavaScript right after
     * a page loads, which can make an already-located element "stale" the
     * instant we try to click it. clickWithRetry() below re-locates the
     * element fresh and retries if that happens.
     */
    private void navigateToProduct(String productName) {
        driver.get(BASE_URL);
        clickWithRetry(By.xpath("//a[@id='itemc' and text()='Phones']"));
        clickWithRetry(By.xpath("//a[contains(text(),'" + productName + "')]"));
    }

    /**
     * Clicks an element identified by the given locator, retrying up to 5
     * times if a StaleElementReferenceException occurs. The element is
     * re-located fresh on every attempt, and a short pause is added between
     * retries to let the page finish re-rendering.
     */
    private void clickWithRetry(By locator) {
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));

        int attempts = 0;
        while (true) {
            try {
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
                element.click();
                return; // success
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                attempts++;
                if (attempts >= 5) {
                    throw e;
                }
                try {
                    Thread.sleep(400); // brief pause to let the DOM settle before retrying
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    /**
     * Navigates to a product page and adds it to the cart, accepting the resulting alert.
     */
    private void addProductToCartByName(String productName) {
        navigateToProduct(productName);

        WebElement addToCartBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.linkText("Add to cart")));
        addToCartBtn.click();

        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();
    }
}
