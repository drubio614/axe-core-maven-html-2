package com.yourcompany.axtest;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import com.deque.html.axecore.selenium.AxeBuilder;
import com.deque.html.axecore.results.Results;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;

// New imports for screenshot functionality
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.io.FileHandler;


public class KeyboardNavigationTest {

    private WebDriver driver;
    private KeyboardNavigationPage keyboardNavigationPage;
    // IMPORTANT: Update this path to the location where you save KeyboardNavigationPage.html
    private String testPageUrl = "file:///C:/Users/rubi1dou/Documents/KeyboardNavigationPage.html"; // Example path, adjust as needed

    // --- Reporter Setup Variables (Comment out if you don't have the reporter binary) ---
    // IMPORTANT: Update this path to your reporter binary (e.g., "src/test/resources/reporter.exe" for Windows)
    // private static String reporterBinaryPath = new File("src/test/resources/reporter").getAbsolutePath(); // Adjust for your OS (e.g., reporter.exe)
    // private AxeReportingOptions _reportOptions = new AxeReportingOptions();
    // --- End Reporter Setup Variables ---

    @Before
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized"); // Maximize the browser window
        // options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080"); // Uncomment for headless

        driver = new ChromeDriver(options);
        // Set a shorter implicit wait for general element presence, explicit waits are preferred for dynamic content
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        System.out.println("WebDriver setup complete. Browser should be open.");

        keyboardNavigationPage = new KeyboardNavigationPage(driver);

        // --- Initialize AxeConfiguration for Reporting (Comment out if you don't have the reporter binary) ---
        /*
        AxeConfiguration.configure()
                .testSuiteName("KeyboardNavigationTestSuite") // Name for your test suite report
                .outputDirectory("target/axe-reports/logs"); // Directory for raw log files
        System.out.println("Axe Reporting configured.");
        */
        // --- End Initialize AxeConfiguration ---
    }

    @Test
    public void testKeyboardNavigationAndAccessibility() {
        System.out.println("Navigating to test page: " + testPageUrl);
        driver.get(testPageUrl);
        sleep(6000); // Wait after navigating

        try {
            // --- 1. Initial Scan of the Page ---
            System.out.println("\n--- Performing Initial Accessibility Scan ---");
            AxeBuilder axeBuilder = new AxeBuilder();
            // Log results for the report (Comment out .logResults if you don't have the reporter binary)
            Results initialResults = axeBuilder.analyze(driver); //.logResults(_reportOptions.uiState("InitialPageScan"));
            System.out.println("Initial scan violations: " + initialResults.getViolations().size());
            generateHtmlReport(initialResults, "initial_page_scan"); // Keep for immediate HTML output
            exportJsonResults(initialResults, "initial_page_scan");   // Keep for immediate JSON output
            sleep(6000); // Wait after initial scan

            // --- 2. Keyboard Navigation and Interaction ---
            System.out.println("\n--- Simulating Keyboard Navigation ---");

            // Tab to the first button and press Enter
            keyboardNavigationPage.pressTab(); // Focuses simpleButton
            sleep(6000);
            keyboardNavigationPage.pressEnter(); // Clicks simpleButton (no visible change, but interaction occurs)
            System.out.println("Clicked simple button via Enter.");
            sleep(6000);

            keyboardNavigationPage.pressTab(); // Focuses simpleLink
            sleep(6000);
            keyboardNavigationPage.pressSpace(); // Clicks simpleLink via Space (no navigation due to href="#")
            System.out.println("Clicked simple link via Space.");
            sleep(6000);

            keyboardNavigationPage.pressTab(); // Focuses textInput
            sleep(6000);
            keyboardNavigationPage.typeText("Hello Keyboard!"); // Types into text input
            System.out.println("Typed into text input.");
            sleep(6000);

            // --- 3. Trigger Modal via Keyboard and Scan ---
            keyboardNavigationPage.pressTab(); // Focuses modalTriggerButton
            sleep(6000);
            keyboardNavigationPage.pressEnter(); // Opens modal via Enter
            System.out.println("Opened modal via Enter.");
            keyboardNavigationPage.waitForModalToAppear();
            sleep(6000); // Wait for modal to be fully visible

            System.out.println("\n--- Performing Modal Dialog Accessibility Scan ---");
            // Scan only the modal content and log results (Comment out .logResults if you don't have the reporter binary)
            Results modalResults = axeBuilder.include("#keyboardModal").analyze(driver); //.logResults(_reportOptions.uiState("ModalDialogScan"));
            System.out.println("Modal dialog scan violations: " + modalResults.getViolations().size());
            generateHtmlReport(modalResults, "modal_dialog_scan"); // Keep for immediate HTML output
            exportJsonResults(modalResults, "modal_dialog_scan");   // Keep for immediate JSON output
            sleep(6000); // Wait after modal scan

            // --- 4. Interact within Modal via Keyboard ---
            keyboardNavigationPage.pressTab(); // Focuses modalTextInput (already focused by JS, but good to simulate)
            sleep(6000);
            keyboardNavigationPage.enterTextInModalInput("This is modal feedback.");
            sleep(6000);
            System.out.println("Entered text in modal input.");

            keyboardNavigationPage.pressTab(); // Focuses submitModalButton
            sleep(6000);
            keyboardNavigationPage.pressEnter(); // Clicks submit button (triggers alert in HTML)
            System.out.println("Clicked modal submit button via Enter.");
            // Handle the alert if it appears, for this demo we'll just let it block
            // In a real test, you'd use driver.switchTo().alert().accept();
            sleep(6000); // Wait for alert to show/be dismissed manually if running interactively

            // --- 5. Close Modal via Keyboard (Escape Key) and Scan ---
            // After alert, focus might be lost or on body. Pressing ESC should still close modal.
            // Ensure focus is on the modal or body for ESC to work as expected.
            // If running interactively, dismiss the alert manually.
            keyboardNavigationPage.pressEscape();
            System.out.println("Pressed Escape to close modal.");
            keyboardNavigationPage.waitForModalToDisappear();
            sleep(6000); // Wait after modal closes

            System.out.println("\n--- Performing Final Page Accessibility Scan ---");
            // Log results for the report (Comment out .logResults if you don't have the reporter binary)
            Results finalResults = axeBuilder.analyze(driver); //.logResults(_reportOptions.uiState("FinalPageScan"));
            System.out.println("Final page scan violations: " + finalResults.getViolations().size());
            generateHtmlReport(finalResults, "final_page_scan"); // Keep for immediate HTML output
            exportJsonResults(finalResults, "final_page_scan");   // Keep for immediate JSON output
            sleep(6000);

            // Final Assertions
            org.junit.Assert.assertTrue("Accessibility violations found in initial scan!", initialResults.getViolations().isEmpty());
            org.junit.Assert.assertTrue("Accessibility violations found in modal dialog scan!", modalResults.getViolations().isEmpty());
            org.junit.Assert.assertTrue("Accessibility violations found in final page scan!", finalResults.getViolations().isEmpty());

            System.out.println("\n🎉 All keyboard navigation and accessibility tests passed!");

        } catch (Exception e) {
            System.err.println("An error occurred during the test: " + e.getMessage());
            e.printStackTrace();
            // Capture screenshot on test failure
            takeScreenshot("test_failure_" + System.currentTimeMillis());
            org.junit.Assert.fail("Test failed due to an exception: " + e.getMessage());
        }
    }

    /**
     * Helper method for Thread.sleep() to avoid repetitive try-catch blocks.
     * @param milliseconds The number of milliseconds to sleep.
     */
    private void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Sleep interrupted: " + e.getMessage());
        }
    }

    /**
     * Helper method to generate an HTML report for Axe-core results.
     * @param results The Axe-core Results object.
     * @param reportName A descriptive name for the report file.
     * @throws IOException If there's an error writing the file.
     */
    private void generateHtmlReport(Results results, String reportName) throws IOException {
        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<!DOCTYPE html>\n");
        htmlContent.append("<html lang=\"en\">\n");
        htmlContent.append("<head>\n");
        htmlContent.append("    <meta charset=\"UTF-8\">\n");
        htmlContent.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        htmlContent.append("    <title>Accessibility Report: ").append(reportName).append("</title>\n");
        htmlContent.append("    <style>\n");
        htmlContent.append("        body { font-family: Arial, sans-serif; margin: 20px; }\n");
        htmlContent.append("        h1 { color: #333; }\n");
        htmlContent.append("        table { width: 100%; border-collapse: collapse; margin-top: 20px; }\n");
        htmlContent.append("        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n");
        htmlContent.append("        th { background-color: #f2f2f2; }\n");
        htmlContent.append("        .violation-impact-critical { background-color: #ffe0e0; }\n");
        htmlContent.append("        .violation-impact-serious { background-color: #fffacd; }\n");
        htmlContent.append("        .violation-impact-moderate { background-color: #e0f2f7; }\n");
        htmlContent.append("        .violation-impact-minor { background-color: #e6ffe6; }\n");
        htmlContent.append("        .summary-box { background-color: #f9f9f9; border: 1px solid #eee; padding: 15px; margin-bottom: 20px; }\n");
        htmlContent.append("    </style>\n");
        htmlContent.append("</head>\n");
        htmlContent.append("<body>\n");
        htmlContent.append("    <h1>Accessibility Report: ").append(reportName).append("</h1>\n");
        htmlContent.append("    <p><strong>Page Scanned URL:</strong> ").append(driver.getCurrentUrl()).append("</p>\n");
        htmlContent.append("    <div class=\"summary-box\">\n");
        htmlContent.append("        <h2>Summary</h2>\n");
        htmlContent.append("        <p>Total Violations: <strong>").append(results.getViolations().size()).append("</strong></p>\n");
        htmlContent.append("        <p>Total Inapplicable: <strong>").append(results.getInapplicable().size()).append("</strong></p>\n");
        htmlContent.append("        <p>Total Incomplete: <strong>").append(results.getIncomplete().size()).append("</strong></p>\n");
        htmlContent.append("    </div>\n");

        if (!results.getViolations().isEmpty()) {
            htmlContent.append("    <h2>Violations (").append(results.getViolations().size()).append(")</h2>\n");
            htmlContent.append("    <table>\n");
            htmlContent.append("        <thead>\n");
            htmlContent.append("            <tr>\n");
            htmlContent.append("                <th>Rule ID</th>\n");
            htmlContent.append("                <th>Impact</th>\n");
            htmlContent.append("                <th>Description</th>\n");
            htmlContent.append("                <th>HTML Affected</th>\n");
            htmlContent.append("                <th>Failure Summary</th>\n");
            htmlContent.append("                <th>Help Link</th>\n");
            htmlContent.append("            </tr>\n");
            htmlContent.append("        </thead>\n");
            htmlContent.append("        <tbody>\n");

            for (com.deque.html.axecore.results.Rule violation : results.getViolations()) {
                String impactClass = "violation-impact-" + violation.getImpact();
                htmlContent.append("            <tr class=\"").append(impactClass).append("\">\n");
                htmlContent.append("                <td>").append(escapeHtml(violation.getId())).append("</td>\n");
                htmlContent.append("                <td>").append(escapeHtml(violation.getImpact())).append("</td>\n");
                htmlContent.append("                <td>").append(escapeHtml(violation.getDescription())).append("</td>\n");
                htmlContent.append("                <td>\n");
                for (com.deque.html.axecore.results.Node node : violation.getNodes()) {
                    htmlContent.append("                    <p><code>").append(escapeHtml(node.getHtml())).append("</code></p>\n");
                    htmlContent.append("                    <p>Target: <code>").append(escapeHtml(node.getTarget().toString())).append("</code></p>\n");
                }
                htmlContent.append("                </td>\n");
                htmlContent.append("                <td>\n");
                for (com.deque.html.axecore.results.Node node : violation.getNodes()) {
                    htmlContent.append("                    <p>").append(escapeHtml(node.getHtml())).append("</p>\n");
                }
                htmlContent.append("                </td>\n");
                htmlContent.append("                <td><a href=\"").append(violation.getHelpUrl()).append("\" target=\"_blank\">Learn More</a></td>\n");
                htmlContent.append("            </tr>\n");
            }
            htmlContent.append("        </tbody>\n");
            htmlContent.append("    </table>\n");
        } else {
            htmlContent.append("    <p>No accessibility violations found.</p>\n");
        }

        htmlContent.append("</body>\n");
        htmlContent.append("</html>");

        File htmlReportDir = new File("target/a11y-html-reports");
        if (!htmlReportDir.exists()) {
            htmlReportDir.mkdirs();
        }
        File htmlFile = new File(htmlReportDir, reportName + "_accessibility_report.html");
        try (FileWriter writer = new FileWriter(htmlFile)) {
            writer.write(htmlContent.toString());
        }
        System.out.println("HTML accessibility report generated at: " + htmlFile.getAbsolutePath());
    }

    /**
     * Helper method to export Axe-core results to a JSON file.
     * @param results The Axe-core Results object.
     * @param fileName A descriptive name for the JSON file (without extension).
     * @throws IOException If there's an error writing the file.
     */
    private void exportJsonResults(Results results, String fileName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File jsonReportDir = new File("target/a11y-json-reports");
        if (!jsonReportDir.exists()) {
            jsonReportDir.mkdirs();
        }
        File jsonFile = new File(jsonReportDir, fileName + "_axe_results.json");
        mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, results);
        System.out.println("Axe-core JSON results saved to: " + jsonFile.getAbsolutePath());
    }

    /**
     * Helper method to take a screenshot and save it.
     * @param fileName The name of the screenshot file (e.g., "failure_screenshot").
     */
    private void takeScreenshot(String fileName) {
        try {
            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File screenshotsDir = new File("target/screenshots");
            if (!screenshotsDir.exists()) {
                screenshotsDir.mkdirs(); // Create directory if it doesn't exist
            }
            File destFile = new File(screenshotsDir, fileName + ".png");
            FileHandler.copy(screenshotFile, destFile);
            System.out.println("Screenshot saved to: " + destFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Helper method to escape HTML special characters.
     * @param text The text to escape.
     * @return The HTML-escaped string.
     */
    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;");
    }

    @After
    public void tearDown() {
        // --- Generate Consolidated Report (Commented out as binary is not available) ---
        /*
        try {
            Runtime rt = Runtime.getRuntime();
            String command = reporterBinaryPath + " target/axe-reports/logs target/axe-reports --format html";
            Process process = rt.exec(command);
            process.waitFor();
            System.out.println("Consolidated HTML report generated in target/axe-reports directory.");
        } catch (Exception e) {
            System.err.println("Error generating consolidated report: " + e.getMessage());
            e.printStackTrace();
        }
        */
        // --- End Generate Consolidated Report ---

        if (driver != null) {
            System.out.println("Closing browser.");
            driver.quit(); // Close the browser after the test
        }
    }
}
