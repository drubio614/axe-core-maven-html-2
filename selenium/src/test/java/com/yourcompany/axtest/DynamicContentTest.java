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
import java.time.Duration; // Ensure Duration is imported
import com.example.helpers.WebDriverInteractionHelper; // Assuming this helpeer exists and compiles

public class DynamicContentTest {

    private WebDriver driver;
    private DynamicContentPage dynamicContentPage;
    private String testPageUrl = "file:///C:\\Users\\rubi1dou\\Documents\\DynamicContentPage.html"; // IMPORTANT: Update this path!

      @Before
    public void setUp() {
        // Automatically set up the chromedriver executable
        WebDriverManager.chromedriver().setup();

        // Configure ChromeOptions for visible browser
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized"); // Maximize the browser window
        // options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080"); // Uncomment for headless

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5)); // Implicit wait for general element presence

        System.out.println("WebDriver setup complete. Browser should be open.");

        dynamicContentPage = new DynamicContentPage(driver);
    }

    @Test
    public void testDynamicContentAndModalAccessibility() {
        System.out.println("Navigating to test page: " + testPageUrl);
        driver.get(testPageUrl);
        try {
            Thread.sleep(6000); // Wait 6 seconds after navigating
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try {
            // --- 1. Initial Scan of the Page ---
            System.out.println("\n--- Performing Initial Accessibility Scan ---");
            AxeBuilder axeBuilder = new AxeBuilder();
            Results initialResults = axeBuilder.analyze(driver);
            System.out.println("Initial scan violations: " + initialResults.getViolations().size());
            generateHtmlReport(initialResults, "initial_page_scan");
            exportJsonResults(initialResults, "initial_page_scan");
            try {
                Thread.sleep(6000); // Wait 6 seconds after initial scan
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // --- 2. Scan After Dynamic Content Loads ---
            dynamicContentPage.clickLoadDynamicContent();
            try {
                Thread.sleep(6000); // Wait 6 seconds after clicking load dynamic content
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            dynamicContentPage.waitForDynamicContent();
            try {
                Thread.sleep(6000); // Wait 6 seconds after dynamic content is visible
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("\n--- Performing Dynamic Content Area Scan ---");
            // Scan only the newly loaded dynamic content area
            Results dynamicResults = axeBuilder.include("#dynamicContentArea").analyze(driver);
            System.out.println("Dynamic content scan violations: " + dynamicResults.getViolations().size());
            generateHtmlReport(dynamicResults, "dynamic_content_scan");
            exportJsonResults(dynamicResults, "dynamic_content_scan");
            try {
                Thread.sleep(6000); // Wait 6 seconds after dynamic content scan
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // --- 3. Scan After Modal Opens ---
            dynamicContentPage.clickOpenModal();
            try {
                Thread.sleep(6000); // Wait 6 seconds after clicking open modal
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            dynamicContentPage.waitForModal();
            try {
                Thread.sleep(6000); // Wait 6 seconds after modal is visible
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Type text into the modal feedback field
            dynamicContentPage.enterModalFeedback("This is a test feedback message.");
            try {
                Thread.sleep(6000); // Wait 6 seconds after typing feedback
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            System.out.println("\n--- Performing Modal Dialog Scan ---");
            // Scan only the modal dialog
            Results modalResults = axeBuilder.include("#myModal").analyze(driver);
            System.out.println("Modal dialog scan violations: " + modalResults.getViolations().size());
            generateHtmlReport(modalResults, "modal_dialog_scan");
            exportJsonResults(modalResults, "modal_dialog_scan");
            try {
                Thread.sleep(6000); // Wait 6 seconds after modal scan
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Close the modal
            dynamicContentPage.clickCloseModal();
            try {
                Thread.sleep(6000); // Wait 6 seconds after clicking close modal
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            dynamicContentPage.waitForModalToClose();
            System.out.println("\nModal dialog closed.");
            try {
                Thread.sleep(6000); // Wait 6 seconds after modal closes
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Final check: Assertions based on collected results
            // You would typically assert that violation counts are acceptable (e.g., all 0)
            org.junit.Assert.assertTrue("Accessibility violations found in initial scan!", initialResults.getViolations().isEmpty());
            org.junit.Assert.assertTrue("Accessibility violations found in dynamic content scan!", dynamicResults.getViolations().isEmpty());
            org.junit.Assert.assertTrue("Accessibility violations found in modal dialog scan!", modalResults.getViolations().isEmpty());

            System.out.println("\n🎉 All dynamic content and modal accessibility tests passed!");

        } catch (Exception e) {
            System.err.println("An error occurred during the test: " + e.getMessage());
            e.printStackTrace();
            org.junit.Assert.fail("Test failed due to an exception: " + e.getMessage());
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
                    // getFailureSummary() might not always be available or useful, getHtml() is a fallback
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
        if (driver != null) {
            System.out.println("Closing browser.");
            driver.quit(); // Close the browser after the test
        }
    }
}
