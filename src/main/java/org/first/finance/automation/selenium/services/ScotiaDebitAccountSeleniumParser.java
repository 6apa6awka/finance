package org.first.finance.automation.selenium.services;

import org.first.finance.automation.selenium.ChromeDriverPlus;
import org.first.finance.automation.selenium.WebElementPlus;
import org.first.finance.automation.selenium.core.SeleniumPath;
import org.first.finance.automation.selenium.core.UITransactionField;
import org.first.finance.core.dto.AccountDto;
import org.first.finance.core.dto.TransactionDto;
import org.first.finance.db.mysql.entity.Account;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.first.finance.automation.selenium.core.UITransactionField.CREDIT;
import static org.first.finance.automation.selenium.core.UITransactionField.DATE;
import static org.first.finance.automation.selenium.core.UITransactionField.DEBIT;
import static org.first.finance.automation.selenium.core.UITransactionField.DESCRIPTION;

@Service
@PropertySource(value = "classpath:selenium/path/debit.properties")
public class ScotiaDebitAccountSeleniumParser extends ScotiaCardAccountSeleniumParser {

    @Override
    public void compareTransactionsForAccount(AccountDto uiAccount, ChromeDriverPlus chromeDriver) {
        if (!uiAccount.getAccountType().equals(getApplicableAccountType())) {
            return;
        }
        loadInitialScreen(uiAccount, chromeDriver);
        Account dbAccount = getAccountRepository().findAccountByName(uiAccount.getName());
        List<WebElementPlus> uiTransactions = chromeDriver.conditionalGetElements(getPath(SeleniumPath.TRANSACTIONS_LIST));
        List<TransactionDto> transactionDtos = uiTransactions.stream()
                .map(this::collectTransactionData)
                .peek(dto -> dto.setAccountId(dbAccount.getId()))
                .toList();
        getTransactionService().compareTransactionsForAccount(transactionDtos, dbAccount);
    }

    @Override
    protected void preProcessDescriptionForKnownTransactions(TransactionDto transactionDto) {
        //if ()
    }

    @Override
    protected UITransactionField[] getUITransactionFieldsInOrder() {
        return new UITransactionField[] {
                DATE,
                DESCRIPTION,
                CREDIT,
                DEBIT
        };
    }

    @Override
    public String getApplicableAccountType() {
        return "Debit";
    }
}
