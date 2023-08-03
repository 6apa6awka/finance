package org.first.finance.automation;

import jakarta.transaction.Transactional;
import org.first.finance.db.mysql.entity.Account;
import org.first.finance.db.mysql.repository.AccountRepository;
import org.first.finance.db.mysql.repository.TransactionRepository;
import org.first.finance.sheets.service.GoogleSheetsProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static org.first.finance.sheets.core.GoogleSheetsDocument.SPENDING_CATEGORIES;

@Service
public class BalanceAdvisor {
    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private GoogleSheetsProcessingService googleSheetsProcessingService;

    private static final Logger LOG = LoggerFactory.getLogger(BalanceAdvisor.class);
    public void start() {
        LOG.info("Start processing balances");
        LOG.info("_________________________________________");
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

    @Transactional
    public void getCashFlowForMonthAndYear(YearMonth yearMonth) {
        List<Object[]> result = transactionRepository.findTransactionsForYearAndMonth(yearMonth.atDay(1));
        result.forEach(objects -> {
            System.out.println(objects[2] + ": Spending's -- " + objects[0] + " Income -- " + objects[1]);
        });
    }

    @Transactional
    public void getIncomeOutcomeStatisticForPeriod(LocalDate start, LocalDate end) {
        if (start == null) {
            start = LocalDate.of(2000, 1, 1);
        }
        if (end == null) {
            end = LocalDate.now();
        }
        List<Object[]> result = transactionRepository.getIncomeOutcomeStatisticForPeriod(start, end);
        result.forEach(objects -> {
            System.out.println(objects[2] + ": Spending's -- " + objects[0] + " Income -- " + objects[1]);
        });
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
    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Autowired
    public void setGoogleSheetsProcessingService(GoogleSheetsProcessingService googleSheetsProcessingService) {
        this.googleSheetsProcessingService = googleSheetsProcessingService;
    }
}
