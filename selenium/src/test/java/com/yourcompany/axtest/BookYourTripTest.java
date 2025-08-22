package com.yourcompany.axtest;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.time.Duration;
import com.deque.html.axecore.selenium.AxeBuilder;
import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class) // This annotation requires a single constructor matching @Parameters
public class BookYourTripTest {

    private WebDriver driver;
    private BookYourTripPage bookYourTripPage;

    // New instance variable to hold the current site URL for parameterized tests
    private String siteUrl;

    // IMPORTANT: Only one constructor is allowed for Parameterized tests,
    // and it must match the parameters provided by the @Parameters method.
    public BookYourTripTest(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    // Method to provide the test data (list of sites)
    @Parameters
    public static Collection<Object[]> siteProvider() {
        return Arrays.asList(new Object[][] {
            {"https://dequeuniversity.com/demo/mars"}, // The actual URL for the Mars Commuter page
            // Add more URLs here as needed for parameterized testing
        });
    }

    @Before
    public void setUp() {
        // Automatically set up the chromedriver executable
        WebDriverManager.chromedriver().setup();

        // Configure ChromeOptions
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized"); // Maximize the browser window
        // IMPORTANT: COMMENT OUT THE FOLLOWING LINE TO SEE THE BROWSER UI
        // options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080"); 
        // If you want to see the browser, ensure --headless is NOT present.
        // --disable-gpu and --window-size are still useful even in non-headless mode.

        driver = new ChromeDriver(options);
        // Set implicit wait for elements to appear (optional, but good practice)
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        
        System.out.println("WebDriver setup complete. Browser should be open.");

        // Initialize the Page Object with the WebDriver instance
        bookYourTripPage = new BookYourTripPage(driver);
    }

    @Test
    public void testBookingAndAccessibilityScan() {
        // Use the siteUrl provided by the parameterized runner
        String currentTestUrl = this.siteUrl; 
        driver.get(currentTestUrl); 

        System.out.println("Navigated to: " + driver.getCurrentUrl());

        try {
            // Fill out the form based on the Mars Commuter HTML
            bookYourTripPage.selectTripType("one-way");
            bookYourTripPage.enterOrigin("Earth");
            bookYourTripPage.enterDestination("Mars");
            bookYourTripPage.enterDepartureDate("08/20/2025"); // Example date, ensure format matches date picker
            bookYourTripPage.selectDepartureTime("09 am"); // Example time, ensure it's in the dropdown options
            bookYourTripPage.selectMarsElitePass(false); // Select "No" for MarsElite Pass
            // If you select true for selectMarsElitePass, then uncomment and use selectMarsPass:
            // bookYourTripPage.selectMarsPass("Britrail Pass"); 
            bookYourTripPage.selectClassOfService("first"); // Select "First" class
            bookYourTripPage.selectTravelerType("Adult (26+)"); // Select "Adult (26+)" traveler type
            // If you select "Youth (12-25)", then uncomment and use selectYouthAge:
            // bookYourTripPage.selectYouthAge("15"); 

            bookYourTripPage.clickFindFaresAndSchedules();

            System.out.println("Form filled and submitted. Now performing accessibility scan.");

            // --- Perform Accessibility Test ---
            AxeBuilder axeBuilder = new AxeBuilder();
            Results axeResults = axeBuilder.analyze(driver);
            
            // --- Export JSON Results ---
            ObjectMapper mapper = new ObjectMapper();
            File jsonReportDir = new File("target/a11y-json-reports");
            if (!jsonReportDir.exists()) {
                jsonReportDir.mkdirs();
            }
            String jsonFileName = currentTestUrl.replaceAll("[^a-zA-Z0-9.-]", "_") + "_axe_results.json";
            File jsonFile = new File(jsonReportDir, jsonFileName);
            mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, axeResults);
            System.out.println("Axe-core JSON results saved to: " + jsonFile.getAbsolutePath());
            // --- End JSON Export ---

            // --- Generate Custom HTML Report ---
            generateHtmlReport(axeResults, currentTestUrl);
            // --- End Custom HTML Report ---

            if (axeResults.getViolations().isEmpty()) {
                System.out.println("\n🎉 No accessibility violations found on " + currentTestUrl + "!");
                org.junit.Assert.assertTrue("No accessibility violations found.", true);
            } else {
                System.out.println("\n--- Accessibility Violations Found on " + currentTestUrl + " ---");
                axeResults.getViolations().forEach(violation -> {
                    System.out.println("Rule ID: " + violation.getId());
                    System.out.println("Description: " + violation.getDescription());
                    System.out.println("Help URL: " + violation.getHelpUrl());
                    System.out.println("Impact: " + violation.getImpact());
                    System.out.println("Nodes:");
                    violation.getNodes().forEach(node -> {
                        System.out.println("    HTML: " + node.getHtml());
                        System.out.println("    Target: " + node.getTarget());
                        // Using getHtml() as getFailureSummary() might not be available or cause issues
                        System.out.println("    Failure Summary (HTML): " + node.getHtml()); 
                        System.out.println("-----");
                    });
                    System.out.println("------------------------------------");
                });
                org.junit.Assert.fail("Accessibility violations found on " + currentTestUrl + ". Check reports for details.");
            }

        } catch (Exception e) {
            System.err.println("An error occurred during the test: " + e.getMessage());
            e.printStackTrace();
            org.junit.Assert.fail("Test failed due to an exception: " + e.getMessage());
        }
    }

    // Helper method for HTML report generation (copied from LiveAxeCoreTest)
    private void generateHtmlReport(Results results, String url) throws IOException {
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
                for (com.deque.html.axecore.results.Node node : violation.getNodes()) { // Use fully qualified name for Node
                    htmlContent.append("                    <p><code>").append(escapeHtml(node.getHtml())).append("</code></p>\n");
                    htmlContent.append("                    <p>Target: <code>").append(escapeHtml(node.getTarget().toString())).append("</code></p>\n");
                }
                htmlContent.append("                </td>\n");
                htmlContent.append("                <td>\n");
                for (com.deque.html.axecore.results.Node node : violation.getNodes()) { // Use fully qualified name for Node
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
        String htmlFileName = url.replaceAll("[^a-zA-Z0-9.-]", "_") + "_accessibility_report.html";
        File htmlFile = new File(htmlReportDir, htmlFileName);
        try (FileWriter writer = new FileWriter(htmlFile)) {
            writer.write(htmlContent.toString());
        }
        System.out.println("HTML accessibility report generated at: " + htmlFile.getAbsolutePath());
    }

    // Helper method to escape HTML special characters.
    private String escapeHtml(String text) {
        if (text == null) {
            return ""; // Handle null input gracefully
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
