package org.first.finance.automation.parcer;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.first.finance.automation.parcer.utils.CommonUtils.sleep;
import static org.first.finance.automation.parcer.utils.CookiesUtils.restoreCookies;
import static org.first.finance.automation.parcer.utils.CookiesUtils.storeCookies;

public class ChromeDriverPlus extends ChromeDriver {
    private Actions actions;
    private boolean isCookieSet = false;
    private final static String CHROME_DRIVER_LOCATION_PROPERTY_NAME = "webdriver.chrome.driver";
    private final static String CHROME_DRIVER_LOCATION_PROPERTY_PATH = "D:\\Programming\\chromedriver\\chromedriver.exe";
    private final static String CHROME_DRIVER_DISABLE_BLINK_PROPERTY = "--disable-blink-features=AutomationControlled";

    private ChromeDriverPlus(ChromeOptions chromeOptions) {
        super(chromeOptions);
        actions = new Actions(this);
    }

    public WebElementPlus getElement(By searchCondition) {
        return new WebElementPlus(findElement(searchCondition), this);
    }

    public List<WebElementPlus> getElements(By searchCondition) {
        return findElements(searchCondition)
                .stream()
                .map(element -> new WebElementPlus(element, this))
                .collect(Collectors.toList());
    }

    public WebElementPlus conditionalGetElement(By searchCondition) {
        try {
            return new WebElementPlus(new WebDriverWait(this, Duration.ofSeconds(4))
                .until(driver -> driver.findElement(searchCondition)), this);
        } catch (NoSuchElementException | TimeoutException e) {
            return null;
        }
    }

    public List<WebElementPlus> conditionalGetElements(By searchCondition) {
        return new WebDriverWait(this, Duration.ofSeconds(4))
                .until(driver -> driver.findElements(searchCondition))
                .stream()
                .map(ele -> new WebElementPlus(ele, this))
                .collect(Collectors.toList());
    }

    public static ChromeDriverPlus getInstance() {
        System.setProperty(CHROME_DRIVER_LOCATION_PROPERTY_NAME, CHROME_DRIVER_LOCATION_PROPERTY_PATH);
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments(CHROME_DRIVER_DISABLE_BLINK_PROPERTY);
        chromeOptions.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        chromeOptions.setExperimentalOption("useAutomationExtension", false);
        chromeOptions.addArguments("--remote-allow-origins=*");
        chromeOptions.addArguments("start-maximized");
        return new ChromeDriverPlus(chromeOptions);
    }

    @Override
    public void get(String url) {
        if (!isCookieSet && this.manage().getCookies().isEmpty()) {
            //restoreCookies(this);
            isCookieSet = true;
        }
        super.get(url);
        sleep(2000);
    }

    @Override
    public void quit() {
        //storeCookies(this);
        super.quit();
    }

    public Actions getActions() {
        return actions;
    }



}
