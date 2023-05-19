package org.first.finance.automation.parcer.services;

import org.first.finance.automation.parcer.ChromeDriverPlus;
import org.first.finance.automation.parcer.WebElementPlus;
import org.first.finance.core.dto.AccountDto;
import org.first.finance.db.mysql.entity.Geography;
import org.first.finance.db.mysql.entity.ServiceProvider;
import org.first.finance.db.mysql.entity.ServiceProviderAlias;
import org.first.finance.db.mysql.entity.ServiceProviderAliasType;
import org.first.finance.db.mysql.entity.Transaction;
import org.first.finance.db.mysql.entity.TransactionType;
import org.first.finance.db.mysql.repository.GeographyRepository;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.first.finance.db.mysql.entity.Account;

import static org.first.finance.automation.parcer.ScotiaDomConstants.CREDIT_ACCOUNT_TRANSACTION_TOTALS;
import static org.first.finance.automation.parcer.ScotiaDomConstants.CREDIT_ACCOUNT_TRANSACTION_TOTALS_ELEMENTS;
import static org.first.finance.automation.parcer.utils.CommonUtils.sleep;

@Service
@PropertySource(value = "classpath:selenium/path/credit.properties")
public class ScotiaCreditAccountSeleniumParser extends ScotiaCardAccountSeleniumParser {
    private static final Logger LOG = LoggerFactory.getLogger(ScotiaCreditAccountSeleniumParser.class);
    private GeographyRepository geographyRepository;
    @Override
    public void processAccount(AccountDto uiAccount, Account dbAccount, ChromeDriverPlus chromeDriver) {
        List<WebElementPlus> uiTransactions = chromeDriver.conditionalGetElements(By.xpath("//table[@summary='Transactions posted since last statement']/tbody/tr"));
        long currentDate = LocalDate.now().toEpochDay();
        Collection<Transaction> transactionsToProcess = new ArrayList<>();
        for (WebElementPlus uiTransaction : uiTransactions) {
            List<WebElement> uiTransactionFields = uiTransaction.findElements(By.tagName("td"));
            if (uiTransactionFields.isEmpty()) {
                continue;
            }
            String date = uiTransaction.findElement(By.tagName("th")).getText();
            if (date.isEmpty()) {
                continue;
            }
            long transactionDate;
            try {
               transactionDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("MMM. d, yyyy")).toEpochDay();
            } catch (DateTimeParseException e) {
                transactionDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("MMM d, yyyy")).toEpochDay();
            }
            Transaction newTransaction = new Transaction();
            newTransaction.setAccount(dbAccount);
            newTransaction.setCreationTime(System.currentTimeMillis());
            newTransaction.setTransactionDate(transactionDate);
            String description = uiTransactionFields.get(1).getText();
            newTransaction.setServiceProvider(resolveServiceProvider(description, uiTransactionFields.get(0).getText()));
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

    @Override
    protected ServiceProvider resolveServiceProvider(String name, String category) {
        ServiceProvider serviceProvider = super.resolveServiceProvider(name, category);
        if (serviceProvider != null) {
            return serviceProvider;
        }
        return parseFromDescription(name);
    }

    protected ServiceProvider parseFromDescription(String description) {
        String[] wordTokens = description.split(" ");
        String lastToken = wordTokens[wordTokens.length - 1];
        if (lastToken.length() == 2) {
            Geography geography = geographyRepository.findByNameAndParentGeographyIsNull(lastToken.toUpperCase());
            if (geography != null) {
                String locationToken = wordTokens[wordTokens.length - 2];
                Geography location = geographyRepository.findByNameLikeAndParentGeography(locationToken.toUpperCase(), geography.getId());
                ServiceProvider serviceProvider = new ServiceProvider();
                serviceProvider.setGeography(location);
                String name = Arrays.stream(wordTokens).limit(wordTokens.length - 2).collect(Collectors.joining(" "));
                serviceProvider.setName(name);
                ServiceProviderAlias serviceProviderAlias = new ServiceProviderAlias();
                serviceProviderAlias.setValue(description);
                serviceProviderAlias.setServiceProvider(serviceProvider);
                serviceProviderAlias.setType(ServiceProviderAliasType.ALIAS);
                return serviceProvider;
            }
        }
        return null;
    }



    private void processTransactions(Collection<Transaction> transactionsToProcess, Account account, long currentDate) {
        Collection<Transaction> dbTransactions = getTransactionRepository().findTransactionsByAccount_IdAndTransactionDateEquals(account.getId(), currentDate);
        for (Transaction transactionToProcess : transactionsToProcess) {
            int count = Collections.frequency(transactionsToProcess, transactionToProcess);
            while (count > Collections.frequency(dbTransactions, transactionToProcess)) {
                dbTransactions.add(createTransaction(transactionToProcess));
                LOG.info("New transaction added to {} account, {}", account.getName(), transactionToProcess);
            }
        }
    }

    @Override
    protected BigDecimal calculateInitialAmount(BigDecimal totalAccountBalance, ChromeDriverPlus chromeDriver) {
        List<WebElementPlus> transactionsTotals = chromeDriver.conditionalGetElement(CREDIT_ACCOUNT_TRANSACTION_TOTALS).findElementsPlus(CREDIT_ACCOUNT_TRANSACTION_TOTALS_ELEMENTS);
        return totalAccountBalance.add(processCreditAccountTransactionTotal(transactionsTotals.get(2)))
                .add(processCreditAccountTransactionTotal(transactionsTotals.get(3)).negate());
    }

    private BigDecimal processCreditAccountTransactionTotal(WebElementPlus tte) {
        String amount = tte.getText();
        return amount.isEmpty() ? BigDecimal.ZERO : new BigDecimal(amount.replaceAll("[\\$, \\,]", ""));
    }

    @Override
    public String getApplicableAccountType() {
        return "Credit";
    }

    @Autowired
    public void setGeographyRepository(GeographyRepository geographyRepository) {
        this.geographyRepository = geographyRepository;
    }
}
