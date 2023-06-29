package org.first.finance.automation.selenium.services;

import org.first.finance.automation.selenium.ChromeDriverPlus;
import org.first.finance.automation.selenium.WebElementPlus;
import org.first.finance.automation.selenium.core.SeleniumPath;
import org.first.finance.automation.selenium.utils.AmountUtils;
import org.first.finance.core.dto.AccountDto;
import org.first.finance.core.dto.TransactionDto;
import org.first.finance.db.mysql.entity.Account;
import org.openqa.selenium.support.ui.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.first.finance.automation.selenium.core.SeleniumPath.TRANSACTION_FILTER_MONTH;
import static org.first.finance.automation.selenium.core.SeleniumPath.TRANSACTION_FILTER_SEARCH;
import static org.first.finance.automation.selenium.utils.CommonUtils.sleep;

public abstract class ScotiaCardAccountSeleniumParser extends ScotiaAccountSeleniumParser {
    @Override
    public void processAccount(AccountDto uiAccount, Account dbAccount, ChromeDriverPlus chromeDriver) {
        List<WebElementPlus> uiTransactions = chromeDriver.conditionalGetElements(getPath(SeleniumPath.TRANSACTIONS_LIST));
        long currentDate = LocalDate.now().toEpochDay();
        Collection<TransactionDto> transactionsToProcess = new ArrayList<>();
        for (WebElementPlus uiTransaction : uiTransactions) {
            TransactionDto transactionDto = collectTransactionData(uiTransaction);
            if (transactionDto == null) {
                continue;
            }
            transactionDto.setAccountId(dbAccount.getId());
            long transactionDate = transactionDto.getTransactionDate();

            if (currentDate != transactionDate) {
                getTransactionService().processTransactions(transactionsToProcess, dbAccount, currentDate);
                dbAccount = getAccountRepository().findById(dbAccount.getId()).orElseThrow();
                if (uiAccount.getAmount().compareTo(dbAccount.getAmount()) == 0) {
                    return;
                }
                transactionsToProcess = new ArrayList<>();
                currentDate = transactionDate;
            }
            transactionsToProcess.add(transactionDto);
        }
        getTransactionService().processTransactions(transactionsToProcess, dbAccount, currentDate);
    }

    @Override
    protected BigDecimal calculateInitialAmount(BigDecimal totalAccountBalance, ChromeDriverPlus chromeDriver) {
        WebElementPlus transactionsTotalsCredit = chromeDriver.conditionalGetElement(getPath(SeleniumPath.TRANSACTIONS_TOTAL_CREDIT));
        WebElementPlus transactionsTotalsDebit = chromeDriver.conditionalGetElement(getPath(SeleniumPath.TRANSACTIONS_TOTAL_DEBIT));
        return totalAccountBalance.add(AmountUtils.parseAmountOrZero(transactionsTotalsCredit.getText()))
                .add(AmountUtils.parseAmountOrZero(transactionsTotalsDebit.getText()).negate());
    }

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
