package org.first.finance.db.mysql.repository;


import org.first.finance.db.mysql.entity.ServiceProvider;
import org.first.finance.db.mysql.entity.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface TransactionRepository extends ListCrudRepository<Transaction, Long> {
    List<Transaction> findTransactionsByAccount_IdAndTransactionDateEquals(Long id, LocalDate currentDate);
    List<Transaction> findTransactionsByAccount_IdAndAsset_IdAndTransactionDateEquals(Long accountId, Long assetId, LocalDate transactionDate);
    List<Transaction> findByServiceProvider(ServiceProvider serviceProvider);
    List<Transaction> findByAccount_IdOrderByTransactionDateDesc(Long accountId);
    @Query(value = "" +
            "SELECT SUM(IF(t.type = 0, t.amount, 0)) spendings, " +
            "SUM(IF(t.type = 1, t.amount, 0)) income, sp.category from Transaction t " +
            "JOIN service_provider sp on t.service_provider_id = sp.id " +
            "WHERE sp.category <> 'TRANSFER' AND MONTH(t.transaction_date) = MONTH(?1) AND " +
            "YEAR(t.transaction_date) = YEAR(?1) " +
            "GROUP BY sp.category;"
            , nativeQuery = true)
    List<Object[]> findTransactionsForYearAndMonth(LocalDate localDate);

    @Query(value = "" +
            "SELECT SUM(IF(t.type = 0, t.amount, 0)) outcome, " +
            "SUM(IF(t.type = 1, t.amount, 0)) income, " +
            "CONCAT(YEAR(transaction_date), ' ', MONTH(t.transaction_date)) period " +
            "from Transaction t " +
            "JOIN service_provider sp on t.service_provider_id = sp.id " +
            "WHERE sp.category <> 'TRANSFER' AND t.transaction_date >= ?1 AND " +
            "t.transaction_date <= ?2 " +
            "GROUP BY CONCAT(YEAR(transaction_date), ' ', MONTH(t.transaction_date)) " +
            "ORDER BY CONCAT(YEAR(transaction_date), ' ', MONTH(t.transaction_date));"
            , nativeQuery = true)
    List<Object[]> getIncomeOutcomeStatisticForPeriod(LocalDate start, LocalDate end);
}
