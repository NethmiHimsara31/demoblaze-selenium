# DemoBlaze Selenium Java Automation Project

## Project Overview
This project automates 5 test scenarios (TC01-TC05) against the DemoBlaze
demo shopping site (https://www.demoblaze.com/) using Selenium WebDriver,
Java, Maven, and TestNG.

## Prerequisites
- Java JDK 11 or higher installed
- Maven installed (`mvn -v` to check)
- Google Chrome browser installed
- Internet connection (WebDriverManager downloads the matching ChromeDriver
  automatically - no manual driver setup required)

## Project Structure
```
demoblaze-selenium/
├── pom.xml                  # Maven dependencies (Selenium, TestNG, WebDriverManager)
├── testng.xml               # TestNG suite runner file
├── README.md                # This file
├── screenshots/             # Execution evidence
└── src/test/java/com/javainstitute/tests/
    ├── BaseTest.java        # Common WebDriver setup/teardown
    └── DemoBlazeTests.java  # The 5 required @Test methods
```

## How to Run

### Option 1: Command line (Maven)
1. Open a terminal in the project's root folder (where pom.xml is).
2. Run:
   ```
   mvn clean test
   ```
3. Chrome will open automatically, run all 5 tests in order, and close after
   each test. Results print to the console and to `target/surefire-reports/`.

### Option 2: IntelliJ IDEA / Eclipse
1. Open the project as a Maven project (File > Open > select the folder
   containing pom.xml).
2. Let the IDE download dependencies (may take a minute the first time).
3. Right-click `testng.xml` (or `DemoBlazeTests.java`) > Run.

## Test Scenarios Covered
| ID   | Scenario                                                        |
|------|-------------------------------------------------------------------|
| TC01 | Home page smoke test - title and PRODUCT STORE heading verification |
| TC02 | Product selection - Phones > Samsung galaxy s6, verify heading, print price |
| TC03 | Add to cart - JS alert wait, print alert text, accept it |
| TC04 | Cart management - add 2 products, count rows, remove Nokia, verify Samsung remains, print total |
| TC05 | Checkout validation - invalid (missing Name/Card) then valid purchase with fictitious data |

## Assumptions and Notes
- DemoBlaze is a public demo site; its layout/response times can change
  without notice, which may require locator updates in future.
- All checkout data used (name, card number, etc.) is fictitious test data
  as instructed - no real personal or payment information is used.
- A short `Thread.sleep(1000)` is used once in TC04 purely to allow the
  cart page's second row to finish rendering after page navigation; all
  other waits are explicit (`WebDriverWait` + `ExpectedConditions`).
- Tests are run independently; each test opens and closes its own browser
  session via `@BeforeMethod` / `@AfterMethod(alwaysRun = true)`.

## Known Limitations
- If DemoBlaze's alert wording, field IDs, or button text changes, the
  affected locators will need to be updated accordingly.
- Occasional slowness on the public demo server may require increasing
  `EXPLICIT_WAIT_SECONDS` in `BaseTest.java`.
