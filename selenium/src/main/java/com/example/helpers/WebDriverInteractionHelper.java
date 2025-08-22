package com.example.helpers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class WebDriverInteractionHelper {
    private WebDriver driver;
    private WebDriverWait wait;

    public WebDriverInteractionHelper(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, timeout);
    }

    /**
     * Waits for an element to be visible and returns it.
     * @param locator The By locator for the element.
     * @return The located WebElement.
     */
    public WebElement waitForElementVisibility(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Waits for an element to be clickable and returns it.
     * @param locator The By locator for the element.
     * @return The located WebElement.
     */
    public WebElement waitForElementClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Clicks an element after waiting for it to be clickable.
     * @param locator The By locator for the element.
     */
    public void clickElement(By locator) {
        waitForElementClickable(locator).click();
    }

    /**
     * Sends keys to an element after waiting for it to be clickable.
     * @param locator The By locator for the element.
     * @param text The text to send.
     */
    public void sendKeysToElement(By locator, String text) {
        waitForElementClickable(locator).sendKeys(text);
    }
}
