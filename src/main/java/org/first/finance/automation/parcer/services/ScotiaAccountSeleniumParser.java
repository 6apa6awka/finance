package org.first.finance.automation.parcer.services;

import jakarta.transaction.Transactional;
import org.first.finance.automation.parcer.ChromeDriverPlus;
import org.first.finance.automation.parcer.SeleniumPath;
import org.first.finance.core.dto.AccountDto;
import org.first.finance.core.services.ServiceProviderService;
import org.first.finance.db.mysql.entity.Account;
import org.first.finance.db.mysql.entity.AccountType;
import org.first.finance.db.mysql.entity.Asset;
import org.first.finance.db.mysql.entity.ServiceProvider;
import org.first.finance.db.mysql.entity.Transaction;
import org.first.finance.db.mysql.repository.AccountRepository;
import org.first.finance.db.mysql.repository.AssetRepository;
import org.first.finance.db.mysql.repository.ServiceProviderRepository;
import org.first.finance.db.mysql.repository.TransactionRepository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import static org.first.finance.automation.parcer.utils.CommonUtils.sleep;

@Service
public abstract class ScotiaAccountSeleniumParser {
    private static final Logger LOG = LoggerFactory.getLogger(ScotiaAccountSeleniumParser.class);
    private ServiceProviderRepository serviceProviderRepository;
    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private AssetRepository assetRepository;
    private Environment environment;
    private ServiceProviderService serviceProviderService;
    private static final String PROPERTY_FILE_NAME_TEMPLATE = "class path resource [selenium/path/%s.properties]";
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

    public Transaction createTransaction(Transaction transaction) {
        BigDecimal transactionAmount = transaction.getAmount();
        if (!transaction.isDebit()) {
            transactionAmount = transactionAmount.negate();
        }
        Account account = accountRepository.findById(transaction.getAccount().getId())
                .orElseThrow();
        account.setAmount(account.getAmount().add(transactionAmount));
        accountRepository.save(account);
        if (transaction.getAsset() != null) {
            Asset asset = assetRepository.findById(transaction.getAsset().getId())
                    .orElseThrow();
            asset.setAmount(asset.getAmount().add(transactionAmount));
            assetRepository.save(asset);
        }
        if (transaction.getServiceProvider() != null) {
            serviceProviderRepository.save(transaction.getServiceProvider());
        }
        return transactionRepository.save(transaction);
    }

    @Transactional
    protected ServiceProvider resolveServiceProvider(String name, String category) {
        return serviceProviderService.findByText(name);
    }

    protected void loadInitialScreen(AccountDto uiAccount, ChromeDriverPlus chromeDriver) {
        chromeDriver.get(uiAccount.getLink());
        sleep(5000);
    }

    public String getPath(SeleniumPath seleniumPath) {
        PropertySource<?> propertySource = ((ConfigurableEnvironment) environment).getPropertySources()
                .get(String.format(PROPERTY_FILE_NAME_TEMPLATE, getPropertyFileName()));
        return seleniumPath.get(propertySource);
    }

    private String getPropertyFileName() {
        Pattern pattern = Pattern.compile("Scotia([A-z]+)AccountSeleniumParser");
        MatchResult match = pattern.matcher(getClass().getSimpleName()).results().findFirst().orElseThrow();
        return match.group(1).toLowerCase();
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
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Autowired
    public void setServiceProviderResolver(ServiceProviderService serviceProviderService) {
        this.serviceProviderService = serviceProviderService;
    }
}
