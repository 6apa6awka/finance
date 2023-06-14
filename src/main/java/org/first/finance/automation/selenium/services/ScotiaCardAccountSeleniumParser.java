package org.first.finance.automation.selenium.services;

import org.first.finance.automation.selenium.ChromeDriverPlus;
import org.first.finance.core.dto.AccountDto;
import org.first.finance.db.mysql.entity.Account;
import org.openqa.selenium.support.ui.Select;

import static org.first.finance.automation.selenium.core.SeleniumPath.TRANSACTION_FILTER_MONTH;
import static org.first.finance.automation.selenium.core.SeleniumPath.TRANSACTION_FILTER_SEARCH;
import static org.first.finance.automation.selenium.utils.CommonUtils.sleep;

public abstract class ScotiaCardAccountSeleniumParser extends ScotiaAccountSeleniumParser {
    @Override
    public abstract void processAccount(AccountDto uiAccount, Account dbAccount, ChromeDriverPlus chromeDriver);

    @Override
    protected void loadInitialScreen(AccountDto uiAccount, ChromeDriverPlus chromeDriver) {
        super.loadInitialScreen(uiAccount, chromeDriver);
        loadAllTransactions(chromeDriver);
    }

    private void loadAllTransactions(ChromeDriverPlus chromeDriver) {
        new Select(chromeDriver.getElement(getPath(TRANSACTION_FILTER_MONTH)))
                .selectByValue("All Transactions");
        sleep(1000);
        chromeDriver.conditionalGetElement(getPath(TRANSACTION_FILTER_SEARCH)).click();
        sleep(3000);
    }

    @Override
    public abstract String getApplicableAccountType();
}
