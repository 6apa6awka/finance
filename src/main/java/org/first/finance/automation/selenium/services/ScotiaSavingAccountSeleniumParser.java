package org.first.finance.automation.selenium.services;

import jakarta.transaction.Transactional;
import org.first.finance.automation.selenium.ChromeDriverPlus;
import org.first.finance.automation.selenium.WebElementPlus;
import org.first.finance.automation.selenium.core.UITransactionField;
import org.first.finance.automation.selenium.utils.AmountUtils;
import org.first.finance.automation.selenium.utils.CommonUtils;
import org.first.finance.automation.selenium.utils.StringUtils;
import org.first.finance.core.dto.AccountDto;
import org.first.finance.core.dto.TransactionDto;
import org.first.finance.db.mysql.entity.Account;
import org.first.finance.db.mysql.entity.Asset;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.first.finance.automation.selenium.core.SeleniumPath.PERIODS;
import static org.first.finance.automation.selenium.core.SeleniumPath.PERIODS_BALANCE;
import static org.first.finance.automation.selenium.core.SeleniumPath.PERIODS_DURATION;
import static org.first.finance.automation.selenium.core.SeleniumPath.PERIODS_GOAL;
import static org.first.finance.automation.selenium.core.SeleniumPath.PERIODS_INTEREST;
import static org.first.finance.automation.selenium.core.SeleniumPath.PERIODS_PROGRESS;
import static org.first.finance.automation.selenium.core.SeleniumPath.PERIODS_PROGRESS_BUTTON;
import static org.first.finance.automation.selenium.core.SeleniumPath.PERIODS_TRANSACTIONS;
import static org.first.finance.automation.selenium.core.SeleniumPath.PERIODS_TRANSACTIONS_CONTAINER;
import static org.first.finance.automation.selenium.core.SeleniumPath.PERIODS_TRANSACTIONS_PERIOD;
import static org.first.finance.automation.selenium.core.UITransactionField.CREDIT;
import static org.first.finance.automation.selenium.core.UITransactionField.DATE;
import static org.first.finance.automation.selenium.core.UITransactionField.DEBIT;
import static org.first.finance.automation.selenium.core.UITransactionField.DESCRIPTION;

@Service
@PropertySource(value = "classpath:selenium/path/saving.properties")
public class ScotiaSavingAccountSeleniumParser extends ScotiaAccountSeleniumParser {
    private static final Logger LOG = LoggerFactory.getLogger(ScotiaSavingAccountSeleniumParser.class);
    @Override
    public void processAccount(AccountDto uiAccount, Account dbAccount, ChromeDriverPlus chromeDriver) {
        List<WebElementPlus> periods = chromeDriver.getElements(getPath(PERIODS));
        for (WebElementPlus period : periods) {
            Asset asset = mapSavingAsset(period);
            WebElementPlus periodButton = period.findElementPlus(getPath(PERIODS_PROGRESS_BUTTON));
            periodButton.click();
            CommonUtils.sleep(1000);
            BigDecimal assetCurrentAmount = AmountUtils.parseAmount(period.findElementPlus(getPath(PERIODS_BALANCE)).getText());
            if (assetCurrentAmount.compareTo(asset.getAmount()) == 0) {
                continue;
            }
            Select select = new Select(chromeDriver.getElement(getPath(PERIODS_TRANSACTIONS_PERIOD)));
            List<WebElement> dateOptions = select.getOptions();
            for (int i = 0; i < dateOptions.size(); i++) {
                select.selectByIndex(i);
                CommonUtils.sleep(3000);
                processPeriod(asset, dbAccount, chromeDriver, assetCurrentAmount);
            }
            LOG.info("{} asset processed", asset.getName());
        }
        LOG.info("{} account processed", uiAccount.getName());
    }

    @Transactional
    private void processPeriod(Asset asset, Account dbAccount, ChromeDriverPlus chromeDriver, BigDecimal assetCurrentAmount) {
        List<WebElementPlus> transactions = chromeDriver.conditionalGetElement(getPath(PERIODS_TRANSACTIONS_CONTAINER))
                .findElementsPlus(getPath(PERIODS_TRANSACTIONS));
        if (transactions.size() == 0 || "No transactions available".equals(transactions.get(0).getText())) {
            return;
        }
        long currentDate = LocalDate.now().toEpochDay();
        Collection<TransactionDto> transactionsToProcess = new ArrayList<>();
        for (WebElement uiTransaction : transactions) {
            TransactionDto transactionDto = collectTransactionData(uiTransaction);
            if (transactionDto == null) {
                continue;
            }
            transactionDto.setAccountId(dbAccount.getId());
            transactionDto.setAssetId(asset.getId());
            long transactionDate = transactionDto.getTransactionDate();

            if (currentDate != transactionDate) {
                getTransactionService().processTransactions(transactionsToProcess, dbAccount, asset.getId(), currentDate);
                asset = getAssetRepository().findById(asset.getId()).orElseThrow();
                dbAccount = getAccountRepository().findById(dbAccount.getId()).orElseThrow();
                if (assetCurrentAmount.compareTo(asset.getAmount()) == 0) {
                    break;
                }
                transactionsToProcess = new ArrayList<>();
                currentDate = transactionDate;
            }
            transactionsToProcess.add(transactionDto);
        }
        getTransactionService().processTransactions(transactionsToProcess, dbAccount, asset.getId(), currentDate);
    }

    @Transactional
    private Asset mapSavingAsset(WebElementPlus period) {
        String duration = period.findElementPlus(getPath(PERIODS_DURATION)).getText();
        String interest = period.findElementPlus(getPath(PERIODS_INTEREST)).getText();
        String currentDay = period.findElementPlus(getPath(PERIODS_PROGRESS)).getText();
        String name = StringUtils.buildStringWithSpaces(duration, interest, period.findElementPlus(getPath(PERIODS_GOAL)).getText());
        Asset asset = getAssetRepository().findAssetByName(name);
        if (asset == null) {
            asset = new Asset();
            asset.setName(name);
            asset.setAmount(BigDecimal.ZERO);
            asset.setQuantity(BigDecimal.ZERO);
            asset.setCost(BigDecimal.ONE);
            asset.setCode(name);
            asset.setFixedIncome(AmountUtils.parseAmount(interest));
            asset.setEndDate(LocalDate.now().plusDays(Long.parseLong(duration.split("-")[0]) - Long.parseLong(currentDay)).toEpochDay());
            LOG.info(" {} new asset created", asset.getName());
            asset = getAssetRepository().save(asset);
        }
        return asset;
    }

    @Override
    public String getApplicableAccountType() {
        return "Saving";
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
}
