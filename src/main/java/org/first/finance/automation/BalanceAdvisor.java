package org.first.finance.automation;

import org.first.finance.automation.parcer.services.ScotiaBankSelenium;
import org.first.finance.db.mysql.entity.Account;
import org.first.finance.db.mysql.repository.AccountRepository;
import org.first.finance.sheets.service.GoogleSheetsProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.first.finance.sheets.core.GoogleSheetsDocument.SPENDING_CATEGORIES;

@Service
public class BalanceAdvisor {
    private AccountRepository accountRepository;
    private ScotiaBankSelenium scotiaBankSelenium;
    private GoogleSheetsProcessingService googleSheetsProcessingService;

    private static final Logger LOG = LoggerFactory.getLogger(BalanceAdvisor.class);
    public void start() {
        LOG.info("Start processing balances");
        LOG.info("_________________________________________");
        scotiaBankSelenium.start();
        ArrayList<ArrayList<String>> cells = googleSheetsProcessingService.read(SPENDING_CATEGORIES);
        BigDecimal currentAmount = accountRepository.findAll().stream()
                .map(this::getAccountAmountAndLog)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LOG.info("Total current amount {}", currentAmount);
        LOG.info("_________________________________________");

        ArrayList<String> headers = cells.remove(0);
        int currentAmountIndex = headers.indexOf("Current Amount");
        BigDecimal plannedAmount = cells.stream()
                .map(l -> getPlannedAmountAndLog(l, currentAmountIndex))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LOG.info("Total planned amount {}", plannedAmount);
        LOG.info("_________________________________________");

        BigDecimal result = currentAmount.subtract(plannedAmount);
        if (result.compareTo(BigDecimal.ZERO) > 0) {
            LOG.warn("You still have amount to plan.\nLeft amount is {}", result);
        } else if (result.compareTo(BigDecimal.ZERO) == 0) {
            LOG.info("Everything Planned Perfectly. Have a good day");
        } else {
            LOG.error("Planned amount bigger than current!!!!!!!!! Danger!!!!!\nDifference {}", result);
        }
        LOG.info("Finished.");
    }

    private BigDecimal getAccountAmountAndLog(Account account) {
        LOG.info("Account {}, current amount is {}", account.getName(), account.getAmount());
        LOG.info("_________________________________________");
        return account.getAmount();
    }

    private BigDecimal getPlannedAmountAndLog(ArrayList<String> data, int amountIndex) {
        LOG.info("Planned category {}, planned amount is {}", data.get(0), data.get(amountIndex));
        LOG.info("_________________________________________");
        return new BigDecimal(data.get(amountIndex));
    }

    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Autowired
    public void setGoogleSheetsProcessingService(GoogleSheetsProcessingService googleSheetsProcessingService) {
        this.googleSheetsProcessingService = googleSheetsProcessingService;
    }

    @Autowired
    public void setScotiaBankSelenium(ScotiaBankSelenium scotiaBankSelenium) {
        this.scotiaBankSelenium = scotiaBankSelenium;
    }
}
