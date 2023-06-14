package org.first.finance.automation.selenium.services;

import org.first.finance.automation.selenium.ChromeDriverPlus;
import org.first.finance.automation.selenium.core.SeleniumPath;
import org.first.finance.automation.selenium.WebElementPlus;
import org.first.finance.automation.selenium.core.ScotiaDomConstants;
import org.first.finance.automation.selenium.utils.DateUtils;
import org.first.finance.core.dto.AccountDto;
import org.first.finance.core.dto.TransactionDto;
import org.first.finance.core.services.TransactionService;
import org.first.finance.db.mysql.entity.Geography;
import org.first.finance.db.mysql.entity.ServiceProvider;
import org.first.finance.db.mysql.entity.ServiceProviderAlias;
import org.first.finance.db.mysql.entity.ServiceProviderAliasType;
import org.first.finance.db.mysql.repository.GeographyRepository;
import org.first.finance.db.mysql.repository.ServiceProviderAliasRepository;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.first.finance.db.mysql.entity.Account;

@Service
@PropertySource(value = "classpath:selenium/path/credit.properties")
public class ScotiaCreditAccountSeleniumParser extends ScotiaCardAccountSeleniumParser {
    private GeographyRepository geographyRepository;
    private ServiceProviderAliasRepository serviceProviderAliasRepository;
    private TransactionService transactionService;

    @Override
    public void processAccount(AccountDto uiAccount, Account dbAccount, ChromeDriverPlus chromeDriver) {
        List<WebElementPlus> uiTransactions = chromeDriver.conditionalGetElements(getPath(SeleniumPath.TRANSACTIONS_LIST));
        processUITransactions(uiTransactions, dbAccount, uiAccount);
    }

    private void processUITransactions(List<WebElementPlus> uiTransactions, Account dbAccount, AccountDto uiAccount) {
        long currentDate = LocalDate.now().toEpochDay();
        Collection<TransactionDto> transactionsToProcess = new ArrayList<>();
        for (WebElementPlus uiTransaction : uiTransactions) {
            List<WebElement> uiTransactionFields = uiTransaction.findElements(getPath(SeleniumPath.TRANSACTIONS_LIST_FIELDS));
            if (uiTransactionFields.isEmpty()) {
                continue;
            }
            String date = uiTransaction.findElement(getPath(SeleniumPath.TRANSACTIONS_LIST_FIELD_DATE)).getText();
            if (date.isEmpty()) {
                throw new IllegalArgumentException("Date can't be null");
            }
            long transactionDate = DateUtils.toLocalDate(date).toEpochDay();

            TransactionDto transactionDto = collectTransactionData(uiTransactionFields);
            transactionDto.setAccountId(dbAccount.getId());
            transactionDto.setTransactionDate(transactionDate);

            if (currentDate != transactionDate) {
                transactionService.processTransactions(transactionsToProcess, dbAccount, currentDate);
                dbAccount = getAccountRepository().findById(dbAccount.getId()).orElseThrow();
                if (uiAccount.getAmount().compareTo(dbAccount.getAmount()) == 0) {
                    return;
                }
                transactionsToProcess = new ArrayList<>();
                currentDate = transactionDate;
            }
            transactionsToProcess.add(transactionDto);
        }
        transactionService.processTransactions(transactionsToProcess, dbAccount, currentDate);
    }

    @Override
    public ServiceProvider resolveServiceProvider(String name, String category) {
        ServiceProvider serviceProvider = super.resolveServiceProvider(name, category);
        if (serviceProvider == null) {
            serviceProvider = parseFromDescription(name);
        }
        return serviceProvider;
    }

    protected ServiceProvider parseFromDescription(String description) {
        String[] wordTokens = description.split(" ");
        String lastToken = wordTokens[wordTokens.length - 1];
        ServiceProvider serviceProvider = null;
        String name;
        if (lastToken.length() == 2) {
            Geography geography = geographyRepository.findByNameAndParentGeographyIsNull(lastToken.toUpperCase());
            if (geography != null) {
                //String locationToken = wordTokens[wordTokens.length - 2];
                //Geography location = geographyRepository.findByNameLikeAndParentGeography(locationToken.toUpperCase(), geography.getId());
                name = Arrays.stream(wordTokens).limit(wordTokens.length - 2).collect(Collectors.joining(" "));
                serviceProvider = getServiceProviderRepository().findFirstByName(name);
            }
        }
        if (serviceProvider == null) {
            serviceProvider = new ServiceProvider();
            serviceProvider.setApproved(false);
            serviceProvider.setName(description);
            serviceProvider = getServiceProviderRepository().save(serviceProvider);
        }
        ServiceProviderAlias serviceProviderAlias = new ServiceProviderAlias();
        serviceProviderAlias.setValue(description);
        serviceProviderAlias.setServiceProvider(serviceProvider);
        serviceProviderAlias.setType(ServiceProviderAliasType.ALIAS);
        serviceProviderAliasRepository.save(serviceProviderAlias);
        return serviceProvider;
    }

    @Override
    protected BigDecimal calculateInitialAmount(BigDecimal totalAccountBalance, ChromeDriverPlus chromeDriver) {
        List<WebElementPlus> transactionsTotals = chromeDriver.conditionalGetElement(ScotiaDomConstants.CREDIT_ACCOUNT_TRANSACTION_TOTALS).findElementsPlus(ScotiaDomConstants.CREDIT_ACCOUNT_TRANSACTION_TOTALS_ELEMENTS);
        return totalAccountBalance.add(processCreditAccountTransactionTotal(transactionsTotals.get(2)))
                .add(processCreditAccountTransactionTotal(transactionsTotals.get(3)).negate());
    }

    private BigDecimal processCreditAccountTransactionTotal(WebElementPlus tte) {
        String amount = tte.getText();
        return amount.isEmpty() ? BigDecimal.ZERO : parseAmount(amount);
    }

    @Override
    public String getApplicableAccountType() {
        return "Credit";
    }

    @Autowired
    public void setGeographyRepository(GeographyRepository geographyRepository) {
        this.geographyRepository = geographyRepository;
    }

    @Autowired
    public void setServiceProviderAliasRepository(ServiceProviderAliasRepository serviceProviderAliasRepository) {
        this.serviceProviderAliasRepository = serviceProviderAliasRepository;
    }

    @Autowired
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
}
