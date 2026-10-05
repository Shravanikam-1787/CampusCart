package com.campuscart;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.HttpURLConnection;
import java.net.URI;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end browser automation tests for CampusCart using Selenium WebDriver.
 * Tests cover the core student user journey: homepage loading, adding products to cart,
 * inspecting cart drawer details, and submitting the campus checkout form.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CampusCartSeleniumTest {

    private static final String BASE_URL = "http://localhost:8080/";
    private static ConfigurableApplicationContext springContext;
    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeAll
    public static void setUpAll() {
        // Ensure CampusCart application is running on port 8080
        if (!isServerResponding(BASE_URL)) {
            springContext = SpringApplication.run(CampusCartApplication.class, "--server.port=8080");
        }

        // Configure headless Chrome options suitable for automation
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");

        // Selenium Manager automatically manages matching chromedriver
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    public static void tearDownAll() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception ignored) {
            }
        }
        if (springContext != null) {
            try {
                springContext.close();
            } catch (Exception ignored) {
            }
        }
    }

    @BeforeEach
    public void setUpEach() {
        driver.get(BASE_URL);

        // Reset browser state / localStorage for complete test isolation
        ((JavascriptExecutor) driver).executeScript("localStorage.clear();");
        driver.navigate().refresh();

        // Wait for dynamic product catalog to load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("product-card-1")));
    }

    /**
     * Test 1 — Homepage:
     * Navigate to http://localhost:8080/, verify the page loads, and verify "CampusCart" is visible.
     */
    @Test
    @Order(1)
    @DisplayName("Test 1: Homepage loads successfully with CampusCart branding")
    void testHomepageLoadsAndBrandingVisible() {
        // Verify page title
        assertTrue(driver.getTitle().contains("CampusCart"),
                "Page title should contain 'CampusCart'");

        // Verify navbar brand element is displayed
        WebElement brandTitle = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.className("brand-title")));
        assertEquals("CampusCart", brandTitle.getText().trim(),
                "Navbar brand title should display 'CampusCart'");

        // Verify hero section is visible
        WebElement heroHeading = driver.findElement(By.className("hero-heading"));
        assertTrue(heroHeading.isDisplayed(), "Hero heading should be visible");

        // Verify catalog has rendered product cards
        WebElement firstProduct = driver.findElement(By.id("product-card-1"));
        assertTrue(firstProduct.isDisplayed(), "Stationery catalog product card should be visible");
    }

    /**
     * Test 2 — Product and Add to Cart:
     * Find a visible product, click Add to Cart, and verify the cart badge updates.
     */
    @Test
    @Order(2)
    @DisplayName("Test 2: Product can be added to cart and updates cart badge count")
    void testProductAddToCartUpdatesBadge() {
        // Verify initial cart badge count is 0
        WebElement badge = driver.findElement(By.id("cartCountBadge"));
        assertEquals("0", badge.getText().trim(), "Initial cart badge should be 0");

        // Find product 1 (Spiral Notebook) and its Add to Cart button
        WebElement addToCartBtn = driver.findElement(By.id("add-to-cart-1"));
        assertTrue(addToCartBtn.isDisplayed(), "Add to Cart button should be visible");

        // Click Add to Cart
        addToCartBtn.click();

        // Verify badge updates to 1
        wait.until(ExpectedConditions.textToBe(By.id("cartCountBadge"), "1"));
        assertEquals("1", badge.getText().trim(), "Cart badge count should update to 1 after adding item");
    }

    /**
     * Test 3 — Cart:
     * Add a product, open the cart drawer, and verify item details, quantity, and total amount.
     */
    @Test
    @Order(3)
    @DisplayName("Test 3: Cart drawer displays selected product, quantity, and total amount")
    void testCartDrawerDisplaysItemDetailsAndTotal() {
        // Add product 1 (Spiral Notebook - Rs.60.00) to cart
        driver.findElement(By.id("add-to-cart-1")).click();
        wait.until(ExpectedConditions.textToBe(By.id("cartCountBadge"), "1"));

        // Open cart drawer
        driver.findElement(By.id("openCartBtn")).click();

        // Wait for cart drawer to become active
        wait.until(ExpectedConditions.attributeContains(By.id("cartDrawer"), "class", "active"));

        // Verify cart item is present
        WebElement cartItem = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("cart-item-1")));
        assertTrue(cartItem.getText().contains("Spiral Notebook"),
                "Cart item should display product name 'Spiral Notebook'");

        // Verify quantity is 1
        wait.until(ExpectedConditions.textToBe(By.cssSelector("#cart-item-1 .qty-count"), "1"));
        WebElement qtyEl = cartItem.findElement(By.className("qty-count"));
        assertEquals("1", qtyEl.getText().trim(), "Cart item quantity should be 1");

        // Verify subtotal and grand total
        wait.until(ExpectedConditions.textToBe(By.id("cartSubtotal"), "₹60.00"));
        wait.until(ExpectedConditions.textToBe(By.id("cartGrandTotal"), "₹60.00"));
        WebElement subtotalEl = driver.findElement(By.id("cartSubtotal"));
        WebElement grandTotalEl = driver.findElement(By.id("cartGrandTotal"));
        assertEquals("₹60.00", subtotalEl.getText().trim(), "Cart subtotal should match product price");
        assertEquals("₹60.00", grandTotalEl.getText().trim(), "Cart grand total should match product price");
    }

    /**
     * Test 4 — Checkout and Order Confirmation:
     * Add product, open cart, proceed to checkout modal, fill student details, submit order,
     * and verify order confirmation modal displays generated Order ID and student info.
     */
    @Test
    @Order(4)
    @DisplayName("Test 4: Full checkout flow places order and displays confirmation with Order ID")
    void testCheckoutAndOrderConfirmationFlow() {
        // Add product 1 (Spiral Notebook) to cart
        driver.findElement(By.id("add-to-cart-1")).click();
        wait.until(ExpectedConditions.textToBe(By.id("cartCountBadge"), "1"));

        // Open cart drawer
        driver.findElement(By.id("openCartBtn")).click();
        wait.until(ExpectedConditions.attributeContains(By.id("cartDrawer"), "class", "active"));

        // Proceed to checkout modal
        WebElement checkoutBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("proceedToCheckoutBtn")));
        checkoutBtn.click();

        // Wait for checkout modal to become active
        wait.until(ExpectedConditions.attributeContains(By.id("checkoutModal"), "class", "active"));

        // Fill out checkout fields with student information
        driver.findElement(By.id("studentName")).sendKeys("Ananya Sen");
        driver.findElement(By.id("studentId")).sendKeys("22BCE2001");
        driver.findElement(By.id("studentEmail")).sendKeys("ananya.sen@college.edu");
        driver.findElement(By.id("hostelRoom")).sendKeys("Girls Hostel 1, Room 302");

        Select paymentSelect = new Select(driver.findElement(By.id("paymentMethod")));
        paymentSelect.selectByVisibleText("Cash on Delivery (Hostel Drop)");

        // Submit order
        WebElement placeOrderBtn = driver.findElement(By.id("confirmOrderBtn"));
        placeOrderBtn.click();

        // Wait for order confirmation modal to appear
        wait.until(ExpectedConditions.attributeContains(By.id("orderConfirmModal"), "class", "active"));

        // Verify confirmation details
        WebElement orderIdEl = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("confirmedOrderId")));
        String orderId = orderIdEl.getText().trim();
        assertTrue(orderId.startsWith("CC-"), "Confirmed Order ID should start with 'CC-', got: " + orderId);

        WebElement customerNameEl = driver.findElement(By.id("confirmedCustomerName"));
        assertTrue(customerNameEl.getText().contains("Ananya Sen"),
                "Confirmation should display student name 'Ananya Sen'");

        WebElement totalEl = driver.findElement(By.id("confirmedTotal"));
        assertEquals("₹60.00", totalEl.getText().trim(), "Confirmation total should be '₹60.00'");
    }

    /**
     * Helper to verify if an HTTP endpoint is reachable.
     */
    private static boolean isServerResponding(String url) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URI(url).toURL().openConnection();
            conn.setConnectTimeout(1500);
            conn.setReadTimeout(1500);
            conn.setRequestMethod("GET");
            return conn.getResponseCode() > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
