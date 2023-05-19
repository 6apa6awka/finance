package org.first.finance.automation.parcer;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.first.finance.automation.parcer.utils.CommonUtils.sleep;

public class WebElementPlus implements WebElement {
    private WebElement delegate;
    private ChromeDriverPlus chromeDriver;
    public WebElementPlus(WebElement webElement, ChromeDriverPlus chromeDriver) {
        delegate = webElement;
        this.chromeDriver = chromeDriver;
    }

    public void clickPlus() {
        new Actions(chromeDriver).moveToElement(delegate).perform();
        sleep();
        new Actions(chromeDriver).click(delegate).perform();
    }

    public WebElementPlus findElementPlus(By by) {
        return new WebElementPlus(delegate.findElement(by), chromeDriver);
    }

    public List<WebElementPlus> findElementsPlus(By by) {
        return delegate.findElements(by)
                .stream()
                .map(element -> new WebElementPlus(element, chromeDriver))
                .collect(Collectors.toList());
    }

    public void setText(String text) {
        clickPlus();
        sleep();
        chromeDriver.getActions().sendKeys(delegate, text).perform();
        sleep();
        chromeDriver.getActions().moveToElement(delegate
                , new Random().nextInt(50) - 25
                , new Random().nextInt(10) - 5)
                .click().perform();
    }
    @Override
    public void click() {
        delegate.click();
    }

    @Override
    public void submit() {
        delegate.submit();
    }

    @Override
    public void sendKeys(CharSequence... keysToSend) {
        delegate.sendKeys(keysToSend);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public String getTagName() {
        return delegate.getTagName();
    }

    @Override
    public String getAttribute(String name) {
        return delegate.getAttribute(name);
    }

    @Override
    public boolean isSelected() {
        return delegate.isSelected();
    }

    @Override
    public boolean isEnabled() {
        return delegate.isEnabled();
    }

    @Override
    public String getText() {
        return delegate.getText();
    }

    @Override
    public List<WebElement> findElements(By by) {
        return delegate.findElements(by);
    }

    @Override
    public WebElement findElement(By by) {
        return delegate.findElement(by);
    }

    @Override
    public boolean isDisplayed() {
        return delegate.isDisplayed();
    }

    @Override
    public Point getLocation() {
        return delegate.getLocation();
    }

    @Override
    public Dimension getSize() {
        return delegate.getSize();
    }

    @Override
    public Rectangle getRect() {
        return delegate.getRect();
    }

    @Override
    public String getCssValue(String propertyName) {
        return delegate.getCssValue(propertyName);
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        return delegate.getScreenshotAs(target);
    }

    @Override
    public String getDomProperty(String name) {
        return delegate.getDomProperty(name);
    }

    @Override
    public String getDomAttribute(String name) {
        return delegate.getDomAttribute(name);
    }

    @Override
    public String getAriaRole() {
        return delegate.getAriaRole();
    }

    @Override
    public String getAccessibleName() {
        return delegate.getAccessibleName();
    }

    @Override
    public SearchContext getShadowRoot() {
        return delegate.getShadowRoot();
    }
}
