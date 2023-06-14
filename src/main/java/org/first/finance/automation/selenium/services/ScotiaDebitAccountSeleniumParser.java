package org.first.finance.automation.selenium.services;

import org.first.finance.automation.selenium.ChromeDriverPlus;
import org.first.finance.automation.selenium.WebElementPlus;
import org.first.finance.core.dto.AccountDto;
import org.first.finance.db.mysql.entity.Account;
import org.first.finance.db.mysql.entity.Transaction;
import org.first.finance.db.mysql.entity.TransactionType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

//@Service
@PropertySource(value = "classpath:selenium/path/debit.properties")
public class ScotiaDebitAccountSeleniumParser extends ScotiaCardAccountSeleniumParser {
    private static final Logger LOG = LoggerFactory.getLogger(ScotiaDebitAccountSeleniumParser.class);
    @Override
    public void processAccount(AccountDto uiAccount, Account dbAccount, ChromeDriverPlus chromeDriver) {
        List<WebElementPlus> uiTransactions = chromeDriver.conditionalGetElements(By.xpath("//table[@summary='table summary']/tbody/tr"));
        long currentDate = LocalDate.now().toEpochDay();
        Collection<Transaction> transactionsToProcess = new ArrayList<>();
        for (WebElementPlus uiTransaction : uiTransactions) {
            List<WebElement> uiTransactionFields = uiTransaction.findElements(By.tagName("td"));
            if (uiTransactionFields.isEmpty()) {
                continue;
            }
            String date = uiTransactionFields.get(0).getText();
            if (date.isEmpty()) {
                continue;
            }
            long transactionDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("MMMM d, yyyy")).toEpochDay();
            Transaction newTransaction = new Transaction();
            newTransaction.setAccount(dbAccount);
            newTransaction.setCreationTime(System.currentTimeMillis());
            newTransaction.setTransactionDate(transactionDate);
            String description = uiTransactionFields.get(1).getText();
            newTransaction.setServiceProvider(resolveServiceProvider(description, null));
            newTransaction.setDescription(description);
            boolean isDebit = uiTransactionFields.get(2).getText().length() == 0;
            newTransaction.setType(isDebit ? TransactionType.DEBIT : TransactionType.CREDIT);
            newTransaction.setAmount(isDebit ? new BigDecimal(uiTransactionFields.get(3).getText().replaceAll("[\\$, \\,]", "")) : new BigDecimal(uiTransactionFields.get(2).getText().replaceAll("[\\$, \\,]", "")));

            if (currentDate != transactionDate) {
                processTransactions(transactionsToProcess, dbAccount, currentDate);
                dbAccount = getAccountRepository().findById(dbAccount.getId()).orElseThrow();
                if (uiAccount.getAmount().compareTo(dbAccount.getAmount()) == 0) {
                    return;
                }
                transactionsToProcess = new ArrayList<>();
                currentDate = transactionDate;
            }
            transactionsToProcess.add(newTransaction);
        }
        processTransactions(transactionsToProcess, dbAccount, currentDate);
    }

    private void processTransactions(Collection<Transaction> transactionsToProcess, Account account, long currentDate) {
        Collection<Transaction> dbTransactions = getTransactionRepository().findTransactionsByAccount_IdAndTransactionDateEquals(account.getId(), currentDate);
        for (Transaction transactionToProcess : transactionsToProcess) {
            int count = Collections.frequency(transactionsToProcess, transactionToProcess);
            while (count > Collections.frequency(dbTransactions, transactionToProcess)) {
                //dbTransactions.add(createTransaction(transactionToProcess));
                LOG.info("New transaction added to {} account, {}", account.getName(), transactionToProcess);
            }
        }
    }

    @Override
    protected BigDecimal calculateInitialAmount(BigDecimal totalAccountBalance, ChromeDriverPlus chromeDriver) {
        List<WebElementPlus> transactionsTotals = chromeDriver.conditionalGetElement(By.xpath("//table[@summary='table summary']/tfoot/tr[@class]")).findElementsPlus(By.xpath("./td"));
        return totalAccountBalance.add(processCreditAccountTransactionTotal(transactionsTotals.get(0)))
                .add(processCreditAccountTransactionTotal(transactionsTotals.get(1)).negate());
    }

    private BigDecimal processCreditAccountTransactionTotal(WebElementPlus tte) {
        String amount = tte.getText();
        return amount.isEmpty() ? BigDecimal.ZERO : new BigDecimal(amount.replaceAll("[\\$, \\,]", ""));
    }

    @Override
    public String getApplicableAccountType() {
        return "xxDebit";
    }
}
