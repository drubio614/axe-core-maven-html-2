package com.yourcompany.axtest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import com.example.helpers.WebDriverInteractionHelper; // Assuming this helper exists and compiles
import java.time.Duration; // Import the Duration class

public class BookYourTripPage {
    private WebDriver driver;
    private WebDriverInteractionHelper helper;

    // Locators for "Book Your Trip" form elements based on the provided HTML
    // IMPORTANT: These XPaths are derived from the HTML you provided.
    // Ensure they accurately target elements on your specific web page.
    private By oneWayRadio = By.id("route-type-one-way");
    private By roundTripRadio = By.id("route-type-round-trip");
    private By multiPlanetRadio = By.id("route-type-multi-city"); // HTML uses "multi-city" for "Multi-Planet"

    private By fromField = By.id("from0"); // From field has ID "from0"
    private By toField = By.id("to0");     // To field has ID "to0"
    private By departureDateField = By.id("deptDate0"); // Departure Date has ID "deptDate0"
    private By departureTimeDropdown = By.id("time0"); // Departure Time dropdown has ID "time0"

    private By marsElitePassYesRadio = By.id("pass-question-yes"); // "Yes" radio for MarsElite Pass
    private By marsElitePassNoRadio = By.id("pass-question-no");   // "No" radio for MarsElite Pass
    private By marsPassDropdown = By.id("passes-select"); // Mars Pass dropdown has ID "passes-select"

    private By classOfServiceFirstRadio = By.id("1stclass"); // "First" class of service radio
    private By classOfServiceSecondRadio = By.id("2ndclass"); // "Second" class of service radio

    // The HTML shows a single dropdown for traveler type and a separate one for youth age
    private By travelerTypeDropdown = By.id("traveler0"); // Traveler type dropdown (Adult, Youth, Child, Senior)
    private By youthAgeDropdown = By.id("age0"); // Youth Age dropdown

    private By findFaresAndSchedulesButton = By.id("fs-submit"); // Submit button has ID "fs-submit"

    public BookYourTripPage(WebDriver driver) {
        this.driver = driver;
        // Initialize WebDriverInteractionHelper with the driver and a timeout
        this.helper = new WebDriverInteractionHelper(driver, Duration.ofSeconds(10));
    }

    public void selectTripType(String type) {
        System.out.println("Selecting trip type: " + type);
        switch (type.toLowerCase()) {
            case "one-way":
                helper.clickElement(oneWayRadio); // Use helper with By locator
                break;
            case "round-trip":
                helper.clickElement(roundTripRadio); // Use helper with By locator
                break;
            case "multi-planet":
                helper.clickElement(multiPlanetRadio); // Use helper with By locator
                break;
            default:
                throw new IllegalArgumentException("Invalid trip type: " + type);
        }
    }

    public void enterOrigin(String origin) {
        System.out.println("Entering origin: " + origin);
        helper.waitForElementClickable(fromField).sendKeys(origin);
    }

    public void enterDestination(String destination) {
        System.out.println("Entering destination: " + destination);
        helper.waitForElementClickable(toField).sendKeys(destination);
    }

    public void enterDepartureDate(String date) {
        System.out.println("Entering departure date: " + date);
        helper.waitForElementClickable(departureDateField).sendKeys(date);
    }

    public void selectDepartureTime(String time) {
        System.out.println("Selecting departure time: " + time);
        Select select = new Select(helper.waitForElementVisibility(departureTimeDropdown));
        select.selectByVisibleText(time);
    }

    public void selectMarsElitePass(boolean hasPass) {
        System.out.println("Selecting MarsElite Pass: " + (hasPass ? "Yes" : "No"));
        if (hasPass) {
            helper.clickElement(marsElitePassYesRadio); // Use helper with By locator
        } else {
            helper.clickElement(marsElitePassNoRadio); // Use helper with By locator
        }
    }

    public void selectMarsPass(String passName) {
        System.out.println("Selecting Mars Pass: " + passName);
        // This dropdown is initially hidden if "No" is selected for MarsElite Pass.
        // Ensure you select "Yes" for MarsElite Pass before calling this method.
        helper.waitForElementVisibility(marsPassDropdown); // Wait for the dropdown to become visible
        Select select = new Select(driver.findElement(marsPassDropdown)); // Use driver.findElement here
        select.selectByVisibleText(passName);
    }

    public void selectClassOfService(String serviceClass) {
        System.out.println("Selecting class of service: " + serviceClass);
        switch (serviceClass.toLowerCase()) {
            case "first":
                helper.clickElement(classOfServiceFirstRadio);
                break;
            case "second":
                helper.clickElement(classOfServiceSecondRadio);
                break;
            default:
                throw new IllegalArgumentException("Invalid class of service: " + serviceClass);
        }
    }

    public void selectTravelerType(String travelerType) {
        System.out.println("Selecting traveler type: " + travelerType);
        Select select = new Select(helper.waitForElementVisibility(travelerTypeDropdown));
        select.selectByVisibleText(travelerType);
    }

    public void selectYouthAge(String age) {
        System.out.println("Selecting youth age: " + age);
        // This dropdown is initially hidden unless "Youth" traveler type is selected.
        helper.waitForElementVisibility(youthAgeDropdown); // Wait for the dropdown to become visible
        Select select = new Select(driver.findElement(youthAgeDropdown)); // Use driver.findElement here
        select.selectByVisibleText(age);
    }

    public void clickFindFaresAndSchedules() {
        System.out.println("Clicking 'Find Fares & Schedules' button.");
        helper.clickElement(findFaresAndSchedulesButton);
    }
}
