package com.yourcompany.axtest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.JavascriptExecutor; // Import for scrolling
import org.openqa.selenium.interactions.Actions; // Import for hover actions
import java.time.Duration;
import org.openqa.selenium.support.ui.ExpectedConditions; // Added for WebDriverWait
import org.openqa.selenium.support.ui.WebDriverWait; // Added for WebDriverWait
import com.example.helpers.WebDriverInteractionHelper; // Ensure this import is correct


public class DynamicContentPage {
    private WebDriver driver;
    private WebDriverInteractionHelper helper;
    private WebDriverWait wait; // Added WebDriverWait for explicit waits

    // Locators for "Book Your Trip" form elements (from Dream Destination, not used by DynamicContentTest directly)
    // Kept for completeness if this Page Object is also used for other tests.
    private By oneWayRadio = By.id("oneway");
    private By returnRadio = By.id("return");
    private By fromField = By.id("search_start_station");
    private By toField = By.id("search_finish_station");
    private By departureDateInput = By.name("search[departure_date]");
    private By showAllPassengerTypesButton = By.cssSelector(".more-passenger-types button[data-testid='toggle-all-passenger-categories-button']");
    private By addAdultPassengerButton = By.xpath("//div[@data-testid='passenger-category-adults']//button[@data-testid='add-passenger']");
    private By removeAdultPassengerButton = By.xpath("//div[@data-testid='passenger-category-adults']//button[@data-testid='remove-passenger']");
    private By adultPassengerCountInput = By.id("search_passengers_attributes_0_number");
    private By addYouthPassengerButton = By.xpath("//div[@data-testid='passenger-category-youths']//button[@data-testid='add-passenger']");
    private By removeYouthPassengerButton = By.xpath("//div[@data-testid='passenger-category-youths']//button[@data-testid='remove-passenger']");
    private By youthPassengerCountInput = By.id("search_passengers_attributes_2_number");
    private By addSeniorPassengerButton = By.xpath("//div[@data-testid='passenger-category-seniors']//button[@data-testid='add-passenger']");
    private By removeSeniorPassengerButton = By.xpath("//div[@data-testid='passenger-category-seniors']//button[@data-testid='remove-passenger']");
    private By seniorPassengerCountInput = By.id("search_passengers_attributes_1_number");
    private By submitSearchButton = By.cssSelector("button[data-testid='submit-search-button']");
    private By cookieAcceptButton = By.cssSelector(".cookie-consent-banner-action .button-tertiary");
    private By localeAndCurrencyMenu = By.cssSelector(".menu-item-wrapper.locale-and-currency .dropdown-toggle");
    private By countryDropdown = By.cssSelector("select[name='country']");
    private By languageRadioEs = By.cssSelector("input[name='locale'][value='es']");
    private By applyButton = By.cssSelector(".dropdown-menu .action .button-primary[type='submit']");
    private By youthAgeInput(int index) {
        return By.id("passenger_2_age_" + index);
    }


    // Locators specifically for DynamicContentPage.html (used by DynamicContentTest)
    private By loadDynamicContentButton = By.id("loadDynamicContentButton");
    private By dynamicContentArea = By.id("dynamicContentArea");
    private By openModalButton = By.id("openModalButton");
    private By myModal = By.id("myModal");
    private By closeModalButton = By.id("closeModalButton");
    private By modalInput = By.id("modalInput");


    public DynamicContentPage(WebDriver driver) {
        this.driver = driver;
        this.helper = new WebDriverInteractionHelper(driver, Duration.ofSeconds(10));
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Initialize WebDriverWait
    }

    // Methods specific to DynamicContentPage.html (used by DynamicContentTest)
    /**
     * Clicks the button to load dynamic content.
     */
    public void clickLoadDynamicContent() {
        System.out.println("Clicking 'Load Dynamic Content' button.");
        helper.clickElement(loadDynamicContentButton);
    }

    /**
     * Waits for the dynamic content area to become visible.
     */
    public void waitForDynamicContent() {
        System.out.println("Waiting for dynamic content to become visible...");
        wait.until(ExpectedConditions.visibilityOfElementLocated(dynamicContentArea));
        System.out.println("Dynamic content is visible.");
    }

    /**
     * Clicks the button to open the modal dialog.
     */
    public void clickOpenModal() {
        System.out.println("Clicking 'Open Modal' button.");
        helper.clickElement(openModalButton);
    }

    /**
     * Waits for the modal dialog to become visible.
     */
    public void waitForModal() {
        System.out.println("Waiting for modal to become visible...");
        wait.until(ExpectedConditions.visibilityOfElementLocated(myModal));
        System.out.println("Modal is visible.");
    }

    /**
     * Clicks the button to close the modal dialog.
     */
    public void clickCloseModal() {
        System.out.println("Clicking 'Close Modal' button.");
        helper.clickElement(closeModalButton);
    }

    /**
     * Waits for the modal dialog to become invisible (closed).
     */
    public void waitForModalToClose() {
        System.out.println("Waiting for modal to close...");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(myModal));
        System.out.println("Modal is closed.");
    }

    /**
     * Enters text into the feedback input field within the modal.
     * @param feedbackText The text to type into the feedback field.
     */
    public void enterModalFeedback(String feedbackText) {
        System.out.println("Entering feedback into modal: '" + feedbackText + "'");
        // Ensure the modal input is visible and clickable before sending keys
        helper.sendKeysToElement(modalInput, feedbackText);
        System.out.println("Feedback entered.");
    }

    // Methods from DreamDestinationPage (kept for completeness, but not used by DynamicContentTest)
    // These methods are included in this Page Object for flexibility if it were to be used for
    // the Dream Destination page tests as well.
    public void acceptCookies() {
        System.out.println("Attempting to accept cookies...");
        try {
            helper.clickElement(cookieAcceptButton);
            System.out.println("Cookies accepted.");
        } catch (Exception e) {
            System.out.println("Cookie banner not found or already dismissed: " + e.getMessage());
        }
    }

    public void hoverOverLanguageCurrencyMenu() {
        System.out.println("Hovering over language/currency menu...");
        WebElement menuElement = helper.waitForElementVisibility(localeAndCurrencyMenu);
        Actions actions = new Actions(driver);
        actions.moveToElement(menuElement).perform();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Hover action performed.");
    }

    public void selectCountry(String countryName) {
        System.out.println("Selecting country: " + countryName);
        Select countrySelect = new Select(helper.waitForElementVisibility(countryDropdown));
        countrySelect.selectByVisibleText(countryName);
        System.out.println("Country selected.");
    }

    public void selectLanguageEspañol() {
        System.out.println("Selecting language: Español");
        helper.clickElement(languageRadioEs);
        System.out.println("Language selected.");
    }

    public void clickApplyLanguageChanges() {
        System.out.println("Clicking Apply button for language/country changes.");
        helper.clickElement(applyButton);
        try {
            Thread.sleep(2000);
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
            case "return":
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
        helper.waitForElementVisibility(departureDateInput).sendKeys(date);
    }

    public void clickShowAllPassengerTypes() {
        System.out.println("Clicking 'Show all passenger types' button.");
        helper.clickElement(showAllPassengerTypesButton);
    }

    public void setAdultPassengers(int count) {
        System.out.println("Setting adult passengers to: " + count);
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
            Thread.sleep(500);
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
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
