package org.first.finance.automation.selenium.services;

import jakarta.transaction.Transactional;
import org.first.finance.automation.selenium.ChromeDriverPlus;
import org.first.finance.automation.selenium.core.SeleniumPath;
import org.first.finance.automation.selenium.core.UITransactionField;
import org.first.finance.automation.selenium.utils.AmountUtils;
import org.first.finance.automation.selenium.utils.DateUtils;
import org.first.finance.core.dto.AccountDto;
import org.first.finance.core.dto.TransactionDto;
import org.first.finance.core.services.ServiceProviderService;
import org.first.finance.core.services.TransactionService;
import org.first.finance.db.mysql.entity.Account;
import org.first.finance.db.mysql.entity.AccountType;
import org.first.finance.db.mysql.entity.ServiceProvider;
import org.first.finance.db.mysql.entity.TransactionType;
import org.first.finance.db.mysql.repository.AccountRepository;
import org.first.finance.db.mysql.repository.AssetRepository;
import org.first.finance.db.mysql.repository.ServiceProviderRepository;
import org.first.finance.db.mysql.repository.TransactionRepository;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import static org.first.finance.automation.selenium.core.UITransactionField.CATEGORY;
import static org.first.finance.automation.selenium.core.UITransactionField.CREDIT;
import static org.first.finance.automation.selenium.core.UITransactionField.DATE;
import static org.first.finance.automation.selenium.core.UITransactionField.DEBIT;
import static org.first.finance.automation.selenium.core.UITransactionField.DESCRIPTION;
import static org.first.finance.automation.selenium.utils.CommonUtils.sleep;

@Service
public abstract class ScotiaAccountSeleniumParser {
    private static final Logger LOG = LoggerFactory.getLogger(ScotiaAccountSeleniumParser.class);
    private ServiceProviderRepository serviceProviderRepository;
    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private AssetRepository assetRepository;
    private ServiceProviderService serviceProviderService;
    private SeleniumPathService seleniumPathService;
    private TransactionService transactionService;
    public void processAccountIfApplicable(AccountDto uiAccount, Account dbAccount, ChromeDriverPlus chromeDriver) {
        if (!uiAccount.getAccountType().equals(getApplicableAccountType())) {
            return;
        }
        loadInitialScreen(uiAccount, chromeDriver);
        if (dbAccount == null) {
            dbAccount = initAccount(uiAccount, chromeDriver);
        }
        processAccount(uiAccount, dbAccount, chromeDriver);
    }

    public void compareTransactionsForAccount(AccountDto uiAccount, ChromeDriverPlus chromeDriver) {
        // do nothing
    }
    public abstract void processAccount(AccountDto uiAccount, Account dbAccount, ChromeDriverPlus chromeDriver);
    public abstract String getApplicableAccountType();

    public Account initAccount(@NotNull AccountDto accountDto, ChromeDriverPlus chromeDriver) {
        Account account = new Account();
        account.setName(accountDto.getName());
        account.setAccountType(AccountType.valueOf(accountDto.getAccountType().toUpperCase()));
        account.setAccountNumber(accountDto.getName().split(" - ")[1]);
        account.setAmount(calculateInitialAmount(accountDto.getAmount(), chromeDriver));
        account.setInstitution("Scotia Bank");
        account = accountRepository.save(account);
        LOG.info("{} account saved to db", account.getName());
        return account;
    }

    protected BigDecimal calculateInitialAmount(BigDecimal currentAmount, ChromeDriverPlus chromeDriver) {
        return BigDecimal.ZERO;
    }

    @Transactional
    public ServiceProvider resolveServiceProvider(String name, String category) {
        ServiceProvider serviceProvider = serviceProviderService.findByText(name);
        if (serviceProvider == null) {
            serviceProvider = parseFromDescription(name);
        }
        if (serviceProvider == null) {
            serviceProvider = serviceProviderService.createServiceProvider(name);
        }
        return serviceProvider;
    }

    protected void loadInitialScreen(AccountDto uiAccount, ChromeDriverPlus chromeDriver) {
        chromeDriver.get(uiAccount.getLink());
        sleep(5000);
    }

    public By getPath(SeleniumPath seleniumPath) {
        return getSeleniumPathService().getPath(seleniumPath, getScotiaAccountName());
    }

    protected ServiceProvider parseFromDescription(String description) {
        return null;
    }

    public String getScotiaAccountName() {
        Pattern pattern = Pattern.compile("Scotia([A-z]+)AccountSeleniumParser");
        MatchResult match = pattern.matcher(getClass().getSimpleName()).results().findFirst().orElseThrow();
        return match.group(1).toLowerCase();
    }

    protected abstract UITransactionField[] getUITransactionFieldsInOrder();

    protected TransactionDto collectTransactionData(WebElement uiTransaction) {
        List<WebElement> uiTransactionFields = uiTransaction.findElements(getPath(SeleniumPath.TRANSACTIONS_LIST_FIELDS));
        if (uiTransactionFields.isEmpty()) {
            return null;
        }
        TransactionDto transactionDto = new TransactionDto();
        String date = findTextForField(uiTransactionFields, DATE);
        String category = findTextForField(uiTransactionFields, CATEGORY);
        String description = findTextForField(uiTransactionFields, DESCRIPTION);
        String creditAmount = findTextForField(uiTransactionFields, CREDIT);
        String debitAmount = findTextForField(uiTransactionFields, DEBIT);
        String amount;
        transactionDto.setCategory(category);
        transactionDto.setDescription(description);
        if (ObjectUtils.isEmpty(creditAmount)) {
            amount = debitAmount;
            transactionDto.setType(TransactionType.DEBIT);
        } else {
            amount = creditAmount;
            transactionDto.setType(TransactionType.CREDIT);
        }
        if (date != null) {
            transactionDto.setTransactionDate(DateUtils.toLocalDate(date));
        }
        if (amount != null) {
            transactionDto.setAmount(AmountUtils.parseAmount(amount));
        }
        preProcessDescriptionForKnownTransactions(transactionDto);
        transactionDto.setServiceProviderId(resolveServiceProvider(description, category).getId());
        return transactionDto;
    }

    protected void preProcessDescriptionForKnownTransactions(TransactionDto transactionDto) {
        // do nothing
    }

    protected String findTextForField(List<? extends WebElement> uiTransactionFields, UITransactionField uiTransactionField) {
        int index = getFieldIndex(uiTransactionField);
        return index == -1 ? null : uiTransactionFields.get(index).getText();
    }

    private int getFieldIndex(UITransactionField uiTransactionField) {
        return uiTransactionField.findPositionIn(getUITransactionFieldsInOrder());
    }

    public AccountRepository getAccountRepository() {
        return accountRepository;
    }

    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }

    public AssetRepository getAssetRepository() {
        return assetRepository;
    }

    protected ServiceProviderRepository getServiceProviderRepository() {
        return serviceProviderRepository;
    }

    @Autowired
    public void setServiceProviderRepository(ServiceProviderRepository serviceProviderRepository) {
        this.serviceProviderRepository = serviceProviderRepository;
    }

    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Autowired
    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Autowired
    public void setAssetRepository(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Autowired
    public void setServiceProviderResolver(ServiceProviderService serviceProviderService) {
        this.serviceProviderService = serviceProviderService;
    }

    public SeleniumPathService getSeleniumPathService() {
        return seleniumPathService;
    }

    @Autowired
    public void setSeleniumPathService(SeleniumPathService seleniumPathService) {
        this.seleniumPathService = seleniumPathService;
    }

    public TransactionService getTransactionService() {
        return transactionService;
    }

    @Autowired
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
}
