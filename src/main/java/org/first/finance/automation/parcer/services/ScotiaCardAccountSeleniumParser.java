package org.first.finance.automation.parcer.services;

import org.first.finance.automation.parcer.ChromeDriverPlus;
import org.first.finance.automation.parcer.SeleniumPath;
import org.first.finance.core.dto.AccountDto;
import org.first.finance.db.mysql.entity.Account;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

import static org.first.finance.automation.parcer.SeleniumPath.TRANSACTION_FILTER_MONTH;
import static org.first.finance.automation.parcer.SeleniumPath.TRANSACTION_FILTER_SEARCH;
import static org.first.finance.automation.parcer.utils.CommonUtils.sleep;

public abstract class ScotiaCardAccountSeleniumParser extends ScotiaAccountSeleniumParser {
    @Override
    public abstract void processAccount(AccountDto uiAccount, Account dbAccount, ChromeDriverPlus chromeDriver);

    @Override
    protected void loadInitialScreen(AccountDto uiAccount, ChromeDriverPlus chromeDriver) {
        super.loadInitialScreen(uiAccount, chromeDriver);
        loadAllTransactions(chromeDriver);
    }

    private void loadAllTransactions(ChromeDriverPlus chromeDriver) {
        new Select(chromeDriver.getElement(By.id(getPath(TRANSACTION_FILTER_MONTH)))).selectByValue("All Transactions");
        sleep(1000);
        chromeDriver.conditionalGetElement(By.xpath(getPath(TRANSACTION_FILTER_SEARCH))).click();
        sleep(3000);
    }

    @Override
    public abstract String getApplicableAccountType();
}
