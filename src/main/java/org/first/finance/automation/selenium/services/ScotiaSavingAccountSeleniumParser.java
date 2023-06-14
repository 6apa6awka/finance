package org.first.finance.automation.selenium.services;

import org.first.finance.automation.selenium.ChromeDriverPlus;
import org.first.finance.automation.selenium.WebElementPlus;
import org.first.finance.automation.selenium.utils.CommonUtils;
import org.first.finance.core.dto.AccountDto;
import org.first.finance.db.mysql.entity.Account;
import org.first.finance.db.mysql.entity.Asset;
import org.first.finance.db.mysql.entity.Transaction;
import org.first.finance.db.mysql.entity.TransactionType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

//@Service
@ConfigurationProperties(prefix = "saving")
public class ScotiaSavingAccountSeleniumParser extends ScotiaAccountSeleniumParser {
    private static final Logger LOG = LoggerFactory.getLogger(ScotiaSavingAccountSeleniumParser.class);
    @Override
    public void processAccount(AccountDto uiAccount, Account dbAccount, ChromeDriverPlus chromeDriver) {
        List<WebElementPlus> periods = chromeDriver.getElements(By.xpath("//div[@class='velocity-savings-periods__period']"));
        for (WebElementPlus period : periods) {
            String duration = period.findElementPlus(By.className("velocity-savings-periods__period-duration")).getText();
            String interest = period.findElementPlus(By.className("velocity-savings-periods__period-text-interest")).getText();
            String currentDay = period.findElementPlus(By.className("velocity-savings-periods__period-text-progress")).getText();
            WebElementPlus periodButton = period.findElementPlus(By.className("velocity-savings-periods__progress-bar"));
            String name = duration + " " + interest + " " + period.findElementPlus(By.className("velocity-savings-periods__period-goal")).getText();
            Asset asset = getAssetRepository().findAssetByName(name);
            if (asset == null) {
                asset = new Asset();
                asset.setName(name);
                asset.setAmount(BigDecimal.ZERO);
                asset.setQuantity(BigDecimal.ZERO);
                asset.setCost(BigDecimal.ONE);
                asset.setCode(name);
                asset.setFixedIncome(new BigDecimal(interest.replaceAll("\\%", "")));
                asset.setEndDate(LocalDate.now().plusDays(Long.parseLong(duration.split("-")[0]) - Long.parseLong(currentDay)).toEpochDay());
                LOG.info(" {} new asset created", asset.getName());
                asset = getAssetRepository().save(asset);
            }
            periodButton.click();
            CommonUtils.sleep(1000);
            BigDecimal assetCurrentAmount = new BigDecimal(period.findElementPlus(By.className("velocity-savings-periods__period-balance")).getText().replaceAll("[\\$, \\,]", ""));
            if (assetCurrentAmount.compareTo(asset.getAmount()) == 0) {
                continue;
            }
            Select select = new Select(chromeDriver.getElement(By.id("velocity-transaction-history-search-period")));
            List<WebElement> dateOptions = select.getOptions();
            for (int i = 0; i < dateOptions.size(); i++) {
                select.selectByIndex(i);
                CommonUtils.sleep(3000);
                List<WebElementPlus> transactions = chromeDriver.conditionalGetElement(By.className("velocity-transcation-history-table-container"))
                        .findElementsPlus(By.xpath("//tbody/tr"));
                if (transactions.size() == 0 || "No transactions available".equals(transactions.get(0).getText())) {
                    continue;
                }
                YearMonth date = YearMonth.parse(dateOptions.get(i).getText(), DateTimeFormatter.ofPattern("MMMM yyyy"));
                long startDate = date.atDay(1).toEpochDay() - 1;
                long endDate = date.atEndOfMonth().toEpochDay() + 1;
                Collection<Transaction> dbTransactions = getTransactionRepository().findTransactionsByAccount_IdAndAsset_IdAndTransactionDateBetween(dbAccount.getId(), asset.getId(), startDate, endDate);
                Collection<Transaction> transactionsToProcess = new ArrayList<>();
                for (WebElement uiTransaction : transactions) {
                    List<WebElement> transactionFields = uiTransaction.findElements(By.tagName("td"));
                    Transaction transaction = new Transaction();
                    transaction.setAccount(dbAccount);
                    transaction.setAsset(asset);
                    transaction.setCreationTime(System.currentTimeMillis());
                    transaction.setTransactionDate(LocalDate.parse(transactionFields.get(0).getText(), DateTimeFormatter.ofPattern("MMMM d, yyyy")).toEpochDay());
                    transaction.setDescription(transactionFields.get(1).getText());
                    boolean isDebit = transactionFields.get(2).getText().length() == 0;
                    transaction.setType(isDebit ? TransactionType.DEBIT : TransactionType.CREDIT);
                    transaction.setAmount(isDebit ? new BigDecimal(transactionFields.get(3).getText().replaceAll("[\\$, \\,]", "")) : new BigDecimal(transactionFields.get(2).getText().replaceAll("[\\$, \\,]", "")));
                    transactionsToProcess.add(transaction);
                }
                for (Transaction transactionToProcess : transactionsToProcess) {
                    int count = Collections.frequency(transactionsToProcess, transactionToProcess);
                    while (count > Collections.frequency(dbTransactions, transactionToProcess)) {
                        //dbTransactions.add(createTransaction(transactionToProcess));
                        LOG.info("New transaction added to {} account, {}", uiAccount.getName(), transactionToProcess);
                    }
                }
                asset = getAssetRepository().findById(asset.getId()).orElseThrow();
                dbAccount = getAccountRepository().findById(dbAccount.getId()).orElseThrow();
                if (assetCurrentAmount.compareTo(asset.getAmount()) == 0) {
                    break;
                }
            }
            LOG.info("{} asset processed", asset.getName());
        }
        LOG.info("{} account processed", uiAccount.getName());
    }

    @Override
    public String getApplicableAccountType() {
        return "Saving";
    }
}
