package org.first.finance.automation.selenium.core;

import org.openqa.selenium.By;

public class ScotiaDomConstants {
    public static final By ACCOUNT_TYPES = By.xpath("//a[@role]");
    public static final By ACCOUNT_TITLE = By.xpath("./td/a");
    public static final By ACCOUNT_BALANCE = By.xpath("./td/div/div[@class = 'number']");
    public static final By ACCOUNTS = By.xpath("../../table/tbody/tr[@id]");

    public static final By CREDIT_ACCOUNT_TRANSACTION_TOTALS = By.xpath("//table[@summary='Transactions posted since last statement']/tbody/tr[@class]");
    public static final By CREDIT_ACCOUNT_TRANSACTION_TOTALS_ELEMENTS = By.xpath("./th");
    private ScotiaDomConstants() {
        //empty
    }
}
