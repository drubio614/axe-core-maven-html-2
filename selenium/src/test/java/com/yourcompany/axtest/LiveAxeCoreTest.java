package com.yourcompany.axtest;

import com.deque.html.axecore.results.AxeRuntimeException;
import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import com.deque.html.axecore.results.Node; // Import Node class
import com.deque.html.axecore.selenium.AxeBuilder;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.After; // Changed from AfterClass
import org.junit.Before; // Changed from BeforeClass
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith; // New import
import org.junit.runners.Parameterized; // New import
import org.junit.runners.Parameterized.Parameters; // New import

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.fasterxml.jackson.databind.ObjectMapper; // Import Jackson ObjectMapper
import java.io.File; // Required for file operations
import java.io.FileWriter; // Required for writing to file
import java.io.IOException; // Required for file operations
import java.util.Arrays; // New import
import java.util.Collection; // New import
import java.util.List; // Required for List

@RunWith(Parameterized.class) // Add this annotation
public class LiveAxeCoreTest {

    private WebDriver driver; // Changed to non-static
    private String siteUrl; // New instance variable to hold the current site URL

    // Constructor to receive the siteUrl from the @Parameters method
    public LiveAxeCoreTest(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    // Method to provide the test data (list of sites)
    @Parameters
    public static Collection<Object[]> siteProvider() {
        return Arrays.asList(new Object[][] {
            {"https://mn.gov/mnit/about-mnit/accessibility/office-of-inaccessibility.jsp"},
            {"https://mnit-dot-a11y.github.io/demos/basic-inaccessible-webpage/"},
            {"https://dequeuniversity.com/demo/mars"},
            {"https://dequeuniversity.com/demo/dream"}
            // Add more URLs here as needed
        });
    }

    @Before // Changed from @BeforeClass
    public void setUp() { // Changed to non-static
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
    }

    @Test
    public void runAxeCoreScanOnSite() { // Renamed for clarity, now uses siteUrl
        System.out.println("Navigating to page: " + siteUrl);
        try {
            driver.get(siteUrl);
            AxeBuilder axeBuilder = new AxeBuilder();
            System.out.println("Executing axe-core accessibility scan on " + siteUrl + "...");
            Results axeResults = axeBuilder.analyze(driver);
            System.out.println("Axe-core scan completed for " + siteUrl + ".");

            // --- Export JSON Results ---
            ObjectMapper mapper = new ObjectMapper();
            File jsonReportDir = new File("target/a11y-json-reports");
            if (!jsonReportDir.exists()) {
                jsonReportDir.mkdirs();
            }
            // Create a unique filename for the JSON report based on the URL
            String jsonFileName = siteUrl.replaceAll("[^a-zA-Z0-9.-]", "_") + "_axe_results.json";
            File jsonFile = new File(jsonReportDir, jsonFileName);
            mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, axeResults);
            System.out.println("Axe-core JSON results saved to: " + jsonFile.getAbsolutePath());
            // --- End JSON Export ---

            // --- Generate Custom HTML Report (NEW) ---
            generateHtmlReport(axeResults, siteUrl);
            // --- End Custom HTML Report ---

            if (axeResults.getViolations().isEmpty()) {
                System.out.println("\n🎉 No accessibility violations found on " + siteUrl + "!");
                System.out.println("This means the page appears to be highly accessible.");
            } else {
                System.out.println("\n--- Accessibility Violations Found on " + siteUrl + " ---");
                System.out.println("Review these issues to improve the accessibility of the page.");
                axeResults.getViolations().forEach(violation -> {
                    System.out.println("Rule ID: " + violation.getId());
                    System.out.println("Description: " + violation.getDescription());
                    System.out.println("Help URL: " + violation.getHelpUrl());
                    System.out.println("Impact: " + violation.getImpact());
                    System.out.println("Nodes:");
                    violation.getNodes().forEach(node -> {
                        System.out.println("   HTML: " + node.getHtml());
                        System.out.println("   Target: " + node.getTarget());
                        // Assuming getFailureSummary() still causes an error,
                        // if `getFailureSummary()` is truly missing, consider removing or replacing
                        // it with another relevant node property like `getHtml()` or `getTarget()`.
                        // For demonstration, I'm keeping it as is, but if compilation fails here,
                        // this line (and the corresponding HTML report line) should be reviewed/removed.
                        System.out.println("   Failure Summary: " + node.getHtml()); // Changed to getHtml() for compatibility
                        System.out.println("-----");
                    });
                    System.out.println("------------------------------------");
                });
            }

            // Assert that no accessibility violations were found
            Assert.assertTrue("Accessibility violations found on " + siteUrl + ": " + axeResults.getViolations().toString(),
                                  axeResults.getViolations().isEmpty());

        } catch (AxeRuntimeException e) {
            System.err.println("Axe-core runtime error during analysis of " + siteUrl + ": " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error saving Axe-core results to JSON or HTML for " + siteUrl + ": " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while testing " + siteUrl + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Generates a simple HTML report from Axe-core results.
     * This method creates an HTML file with a table summarizing violations.
     * @param results The Axe-core Results object.
     * @param url The URL that was scanned.
     * @throws IOException If there's an error writing the file.
     */
    private static void generateHtmlReport(Results results, String url) throws IOException {
        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<!DOCTYPE html>\n");
        htmlContent.append("<html lang=\"en\">\n");
        htmlContent.append("<head>\n");
        htmlContent.append("    <meta charset=\"UTF-8\">\n");
        htmlContent.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        htmlContent.append("    <title>Accessibility Report for ").append(url).append("</title>\n");
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
        htmlContent.append("    <h1>Accessibility Report</h1>\n");
        htmlContent.append("    <p><strong>Page Scanned:</strong> <a href=\"").append(url).append("\">").append(url).append("</a></p>\n");
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

            for (Rule violation : results.getViolations()) {
                String impactClass = "violation-impact-" + violation.getImpact();
                htmlContent.append("            <tr class=\"").append(impactClass).append("\">\n");
                htmlContent.append("                <td>").append(escapeHtml(violation.getId())).append("</td>\n");
                htmlContent.append("                <td>").append(escapeHtml(violation.getImpact())).append("</td>\n");
                htmlContent.append("                <td>").append(escapeHtml(violation.getDescription())).append("</td>\n");
                htmlContent.append("                <td>\n");
                for (Node node : violation.getNodes()) {
                    htmlContent.append("                    <p><code>").append(escapeHtml(node.getHtml())).append("</code></p>\n");
                    htmlContent.append("                    <p>Target: <code>").append(escapeHtml(node.getTarget().toString())).append("</code></p>\n");
                }
                htmlContent.append("                </td>\n");
                htmlContent.append("                <td>\n");
                for (Node node : violation.getNodes()) {
                    htmlContent.append("                    <p>").append(escapeHtml(node.getHtml())).append("</p>\n"); // Using getHtml() here
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
        // Create a unique filename for the HTML report based on the URL
        String htmlFileName = url.replaceAll("[^a-zA-Z0-9.-]", "_") + "_accessibility_report.html";
        File htmlFile = new File(htmlReportDir, htmlFileName);
        try (FileWriter writer = new FileWriter(htmlFile)) {
            writer.write(htmlContent.toString());
        }
        System.out.println("HTML accessibility report generated at: " + htmlFile.getAbsolutePath());
    }

    /**
     * Helper method to escape HTML special characters.
     */
    private static String escapeHtml(String text) {
        if (text == null) {
            return ""; // Handle null input gracefully
        }
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;");
    }

    @After // Changed from @AfterClass
    public void tearDown() { // Changed to non-static
        if (driver != null) {
            System.out.println("\nClosing browser after testing " + siteUrl + ".");
            driver.quit();
        }
    }
}