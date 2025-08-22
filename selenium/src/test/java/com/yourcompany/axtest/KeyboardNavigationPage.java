package com.yourcompany.axtest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.Keys; // Import for keyboard actions
import org.openqa.selenium.interactions.Actions; // Import for Actions class
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

// Assuming WebDriverInteractionHelper exists and compiles
import com.example.helpers.WebDriverInteractionHelper;

public class KeyboardNavigationPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actions; // Actions class for keyboard interactions
    private WebDriverInteractionHelper helper;

    // Locators for interactive elements
    private By simpleButton = By.id("simpleButton");
    private By simpleLink = By.id("simpleLink");
    private By textInput = By.id("textInput");
    private By modalTriggerButton = By.id("modalTriggerButton");

    // Locators for modal dialog
    private By keyboardModal = By.id("keyboardModal");
    private By closeKeyboardModalButton = By.id("closeKeyboardModalButton");
    private By modalTextInput = By.id("modalTextInput");
    private By submitModalButton = By.id("submitModalButton");


    public KeyboardNavigationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.actions = new Actions(driver);
        this.helper = new WebDriverInteractionHelper(driver, Duration.ofSeconds(10));
    }

    /**
     * Simulates a Tab key press.
     */
    public void pressTab() {
        System.out.println("Simulating Tab key press.");
        actions.sendKeys(Keys.TAB).perform();
    }

    /**
     * Simulates an Enter key press.
     */
    public void pressEnter() {
        System.out.println("Simulating Enter key press.");
        actions.sendKeys(Keys.ENTER).perform();
    }

    /**
     * Simulates a Space key press.
     */
    public void pressSpace() {
        System.out.println("Simulating Space key press.");
        actions.sendKeys(Keys.SPACE).perform();
    }

    /**
     * Simulates an Escape key press.
     */
    public void pressEscape() {
        System.out.println("Simulating Escape key press.");
        actions.sendKeys(Keys.ESCAPE).perform();
    }

    /**
     * Types text into the currently focused element.
     * @param text The text to type.
     */
    public void typeText(String text) {
        System.out.println("Typing text: '" + text + "' into focused element.");
        actions.sendKeys(text).perform();
    }

    /**
     * Clicks the button that triggers the modal.
     */
    public void clickModalTriggerButton() {
        System.out.println("Clicking 'Open Dialog' button.");
        helper.clickElement(modalTriggerButton);
    }

    /**
     * Waits for the modal dialog to become visible.
     */
    public void waitForModalToAppear() {
        System.out.println("Waiting for modal to appear...");
        wait.until(ExpectedConditions.visibilityOfElementLocated(keyboardModal));
        System.out.println("Modal is visible.");
    }

    /**
     * Waits for the modal dialog to become invisible (closed).
     */
    public void waitForModalToDisappear() {
        System.out.println("Waiting for modal to disappear...");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(keyboardModal));
        System.out.println("Modal is hidden.");
    }

    /**
     * Enters text into the text input field inside the modal.
     * @param text The text to enter.
     */
    public void enterTextInModalInput(String text) {
        System.out.println("Entering text into modal input: '" + text + "'");
        helper.sendKeysToElement(modalTextInput, text);
    }

    /**
     * Clicks the submit button inside the modal.
     */
    public void clickModalSubmitButton() {
        System.out.println("Clicking modal submit button.");
        helper.clickElement(submitModalButton);
    }

    /**
     * Clicks the close button inside the modal.
     */
    public void clickModalCloseButton() {
        System.out.println("Clicking modal close button.");
        helper.clickElement(closeKeyboardModalButton);
    }

    /**
     * Gets the currently focused element.
     * @return The WebElement that currently has focus.
     */
    public WebElement getActiveElement() {
        return driver.switchTo().activeElement();
    }
}
