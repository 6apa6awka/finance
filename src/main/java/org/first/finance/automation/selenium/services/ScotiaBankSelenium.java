package org.first.finance.automation.selenium.services;

import org.first.finance.automation.selenium.ChromeDriverPlus;
import org.first.finance.automation.selenium.WebElementPlus;
import org.first.finance.automation.selenium.core.ScotiaDomConstants;
import org.first.finance.core.dto.AccountDto;
import org.first.finance.db.mysql.entity.Account;
import org.first.finance.db.mysql.repository.AccountRepository;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScotiaBankSelenium {
    private static final Logger LOG = LoggerFactory.getLogger(ScotiaBankSelenium.class);
    private AccountRepository accountRepository;
    private List<ScotiaAccountSeleniumParser> accountParsers;
    private ChromeDriverPlus chromeDriver;
    private static final String START_PAGE_URL = "https://www.scotiabank.com/ca/en/personal.html";

    public void start() {
        chromeDriver = ChromeDriverPlus.getInstance();
        try {
            login();
            List<WebElementPlus> existingAccountTypesList = chromeDriver.conditionalGetElements(ScotiaDomConstants.ACCOUNT_TYPES);
            List<AccountDto> uiAccounts =  existingAccountTypesList.stream().flatMap(at -> getUiAccounts(at.getText(), at).stream()).toList();
            LOG.info("Starting process {} accounts", uiAccounts.size());
            uiAccounts.forEach(this::processAccount);
        } catch (Exception e) {
            LOG.error("ERROR {} \n {}", e.getMessage(), e.getStackTrace());
            chromeDriver.close();
            chromeDriver.quit();
            throw e;
        }
        chromeDriver.close();
        chromeDriver.quit();
        LOG.info("Parsing job finished");
    }

    private void login() {
        chromeDriver.get(START_PAGE_URL);
        /*WebElementPlus signInButton = chromeDriver.conditionalGetElement(By.className("btn-signin"));
        signInButton.click();
        sleep(2000);
        chromeDriver.conditionalGetElement(By.id("usernameInput-input")).setText("orlov.first");
        sleep(2000);
        chromeDriver.getElement(By.id("password-input")).setText(new ScotiaBankSelenium().getString());
        sleep(6000);
        chromeDriver.getElement(By.id("signIn")).clickPlus();
        sleep(10000);
        chromeDriver.conditionalGetElement(By.id("trustDevice")).clickPlus();
        chromeDriver.getElement(By.id("continue")).clickPlus();
        sleep(5000);*/

    }

    private List<AccountDto> getUiAccounts(String accountType, WebElementPlus accountGroup) {
        return accountGroup.findElementsPlus(ScotiaDomConstants.ACCOUNTS)
                .stream()
                .map(ag -> getInfoFromUiAccount(accountType, ag))
                .collect(Collectors.toList());
    }

    private AccountDto getInfoFromUiAccount(String accountType, WebElementPlus uiAccount) {
        AccountDto accountDto = new AccountDto();
        WebElementPlus titleElement = uiAccount.findElementPlus(ScotiaDomConstants.ACCOUNT_TITLE);
        accountDto.setLink(titleElement.getAttribute("href"));
        accountDto.setName(titleElement.getText());
        accountDto.setAccountType(accountType);
        String accountBalance = uiAccount.findElementPlus(ScotiaDomConstants.ACCOUNT_BALANCE).getText();
        accountDto.setAmount(new BigDecimal(accountBalance.replaceAll("[\\$, \\,]", "")));
        return accountDto;
    }

    private void processAccount(AccountDto uiAccount) {
        if ("Borrowing".equals(uiAccount.getAccountType())) {
            uiAccount.setAmount(uiAccount.getAmount().negate());
            uiAccount.setAccountType("Credit");
        } 
        if ("Banking".equals(uiAccount.getAccountType())) {
            if (uiAccount.getName().contains("Momentum PLUS Savings")) {
                uiAccount.setAccountType("Saving");
            } else {
                uiAccount.setAccountType("Debit");
            }
        }
        Account dbAccount = accountRepository.findAccountByName(uiAccount.getName());
        if (dbAccount == null || uiAccount.getAmount().compareTo(dbAccount.getAmount()) != 0) {
            LOG.info("{} account should be updated. Expected amount is {}, current amount is {}", uiAccount.getName(),
                    uiAccount.getAmount(), dbAccount == null ? "0.00" : dbAccount.getAmount());
        } else {
            LOG.info("{} account is up to date. Amount is {}", uiAccount.getName(), uiAccount.getAmount());
            return;
        }
        accountParsers.forEach(parser -> parser.processAccountIfApplicable(uiAccount, dbAccount, chromeDriver));
    }

    private boolean isAccountUpToDate(Account account, By pathToCurrentAmount) {
        BigDecimal currentAmount = new BigDecimal(chromeDriver.getElement(pathToCurrentAmount)
                .getText().replaceAll("[\\$, \\,]", ""));
        if (currentAmount.compareTo(account.getAmount()) == 0) {
            LOG.info("{} account is up to date", account.getName());
            return true;
        }
        return false;
    }

    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Autowired()
    public void setAccountParsers(List<ScotiaAccountSeleniumParser> accountParsers) {
        this.accountParsers = accountParsers;
    }
}
