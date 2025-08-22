package com.yourcompany.axtest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.JavascriptExecutor; // Import for scrolling
import org.openqa.selenium.interactions.Actions; // Import for hover actions
import com.example.helpers.WebDriverInteractionHelper; // Assuming this helper exists and compiles
import java.time.Duration;

public class DestinationDreamPage {
    private WebDriver driver;
    private WebDriverInteractionHelper helper;

    // Locators for "Book Your Trip" form elements based on the provided HTML
    private By oneWayRadio = By.id("oneway");
    private By returnRadio = By.id("return");

    private By fromField = By.id("search_start_station");
    private By toField = By.id("search_finish_station");
    private By departureDateInput = By.name("search[departure_date]"); // Hidden input for date

    // Passenger category locators
    private By showAllPassengerTypesButton = By.cssSelector(".more-passenger-types button[data-testid='toggle-all-passenger-categories-button']");

    private By addAdultPassengerButton = By.xpath("//div[@data-testid='passenger-category-adults']//button[@data-testid='add-passenger']");
    private By removeAdultPassengerButton = By.xpath("//div[@data-testid='passenger-category-adults']//button[@data-testid='remove-passenger']");
    private By adultPassengerCountInput = By.id("search_passengers_attributes_0_number");

    private By addYouthPassengerButton = By.xpath("//div[@data-testid='passenger-category-youths']//button[@data-testid='add-passenger']");
    private By removeYouthPassengerButton = By.xpath("//div[@data-testid='passenger-category-youths']//button[@data-testid='remove-passenger']");
    private By youthPassengerCountInput = By.id("search_passengers_attributes_2_number"); // Corrected index for youths

    private By addSeniorPassengerButton = By.xpath("//div[@data-testid='passenger-category-seniors']//button[@data-testid='add-passenger']");
    private By removeSeniorPassengerButton = By.xpath("//div[@data-testid='passenger-category-seniors']//button[@data-testid='remove-passenger']");
    private By seniorPassengerCountInput = By.id("search_passengers_attributes_1_number"); // Corrected index for seniors

    // Youth age inputs (assuming up to 8 youth passengers based on HTML)
    private By youthAgeInput(int index) {
        return By.id("passenger_2_age_" + index);
    }

    private By submitSearchButton = By.cssSelector("button[data-testid='submit-search-button']");

    // Locator for the cookie acceptance button
    private By cookieAcceptButton = By.cssSelector(".cookie-consent-banner-action .button-tertiary");

    // Locators for language/country selection
    private By localeAndCurrencyMenu = By.cssSelector(".menu-item-wrapper.locale-and-currency .dropdown-toggle");
    private By countryDropdown = By.cssSelector("select[name='country']");
    private By languageRadioEs = By.cssSelector("input[name='locale'][value='es']");
    private By applyButton = By.cssSelector(".dropdown-menu .action .button-primary[type='submit']");


    public DestinationDreamPage(WebDriver driver) {
        this.driver = driver;
        this.helper = new WebDriverInteractionHelper(driver, Duration.ofSeconds(10));
    }

    /**
     * Clicks the "Accept" button on the cookie consent banner.
     * This method will wait for the button to be clickable before attempting to click it.
     */
    public void acceptCookies() {
        System.out.println("Attempting to accept cookies...");
        try {
            helper.clickElement(cookieAcceptButton);
            System.out.println("Cookies accepted.");
        } catch (Exception e) {
            System.out.println("Cookie banner not found or already dismissed: " + e.getMessage());
            // This catch block allows the test to continue if the cookie banner isn't present,
            // which might happen if it's already accepted or not displayed under certain conditions.
        }
    }

    /**
     * Hovers over the locale and currency menu item to reveal the dropdown.
     */
    public void hoverOverLanguageCurrencyMenu() {
        System.out.println("Hovering over language/currency menu...");
        WebElement menuElement = helper.waitForElementVisibility(localeAndCurrencyMenu);
        Actions actions = new Actions(driver);
        actions.moveToElement(menuElement).perform();
        try {
            Thread.sleep(1000); // Give time for the dropdown to appear
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Hover action performed.");
    }

    /**
     * Selects a country from the country dropdown in the language/currency menu.
     * @param countryName The visible text of the country option to select (e.g., "Spain").
     */
    public void selectCountry(String countryName) {
        System.out.println("Selecting country: " + countryName);
        Select countrySelect = new Select(helper.waitForElementVisibility(countryDropdown));
        countrySelect.selectByVisibleText(countryName);
        System.out.println("Country selected.");
    }

    /**
     * Selects a language from the language radio buttons in the language/currency menu.
     * This method specifically targets "Español".
     */
    public void selectLanguageEspañol() {
        System.out.println("Selecting language: Español");
        helper.clickElement(languageRadioEs);
        System.out.println("Language selected.");
    }

    /**
     * Clicks the "Apply" button within the language/currency dropdown to save changes.
     */
    public void clickApplyLanguageChanges() {
        System.out.println("Clicking Apply button for language/country changes.");
        helper.clickElement(applyButton);
        // It might be necessary to wait for the page to reload or for changes to apply
        try {
            Thread.sleep(2000); // Give time for the page to update after applying
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Apply button clicked.");
    }

    public void selectTripType(String type) {
        System.out.println("Selecting trip type: " + type);
        switch (type.toLowerCase()) {
            case "one-way":
                helper.clickElement(oneWayRadio);
                break;
            case "return": // Renamed from "round-trip" in new HTML
                helper.clickElement(returnRadio);
                break;
            default:
                throw new IllegalArgumentException("Invalid trip type: " + type + ". Supported types are 'one-way' and 'return'.");
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
        // The actual date input is hidden. Sending keys to it directly.
        // If this doesn't work (e.g., if a JS date picker needs interaction),
        // more complex date picker UI interaction would be needed.
        helper.waitForElementVisibility(departureDateInput).sendKeys(date);
    }

    public void clickShowAllPassengerTypes() {
        System.out.println("Clicking 'Show all passenger types' button.");
        helper.clickElement(showAllPassengerTypesButton);
    }

    public void setAdultPassengers(int count) {
        System.out.println("Setting adult passengers to: " + count);
        // Get current count from the input's value attribute
        int currentCount = Integer.parseInt(helper.waitForElementVisibility(adultPassengerCountInput).getAttribute("value"));
        if (currentCount < count) {
            for (int i = currentCount; i < count; i++) {
                helper.clickElement(addAdultPassengerButton);
            }
        } else if (currentCount > count) {
            for (int i = currentCount; i > count; i--) {
                helper.clickElement(removeAdultPassengerButton);
            }
        }
    }

    public void setYouthPassengers(int count) {
        System.out.println("Setting youth passengers to: " + count);
        // Get current count from the input's value attribute
        int currentCount = Integer.parseInt(helper.waitForElementVisibility(youthPassengerCountInput).getAttribute("value"));
        if (currentCount < count) {
            for (int i = currentCount; i < count; i++) {
                helper.clickElement(addYouthPassengerButton);
            }
        } else if (currentCount > count) {
            for (int i = currentCount; i > count; i--) {
                helper.clickElement(removeYouthPassengerButton);
            }
        }
    }

    public void setYouthAge(int passengerIndex, String age) {
        System.out.println("Setting youth passenger " + (passengerIndex + 1) + " age to: " + age);
        helper.waitForElementClickable(youthAgeInput(passengerIndex)).sendKeys(age);
    }

    public void setSeniorPassengers(int count) {
        System.out.println("Setting senior passengers to: " + count);
        // Get current count from the input's value attribute
        int currentCount = Integer.parseInt(helper.waitForElementVisibility(seniorPassengerCountInput).getAttribute("value"));
        if (currentCount < count) {
            for (int i = currentCount; i < count; i++) {
                helper.clickElement(addSeniorPassengerButton);
            }
        } else if (currentCount > count) {
            for (int i = currentCount; i > count; i--) {
                helper.clickElement(removeSeniorPassengerButton);
            }
        }
    }

    public void clickFindFaresAndSchedules() {
        System.out.println("Clicking 'Search' button.");
        helper.clickElement(submitSearchButton);
    }

    /**
     * Scrolls the page down to the bottom.
     */
    public void scrollPageDown() {
        System.out.println("Scrolling page down.");
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        try {
            Thread.sleep(500); // Small pause to observe scroll
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Scrolls the page up to the top.
     */
    public void scrollPageUp() {
        System.out.println("Scrolling page up.");
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0)");
        try {
            Thread.sleep(500); // Small pause to observe scroll
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
