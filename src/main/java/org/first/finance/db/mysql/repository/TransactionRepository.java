package org.first.finance.db.mysql.repository;


import org.first.finance.db.mysql.entity.ServiceProvider;
import org.first.finance.db.mysql.entity.Transaction;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface TransactionRepository extends ListCrudRepository<Transaction, Long> {
    List<Transaction> findTransactionsByAccount_IdAndTransactionDateEquals(Long id, long currentDate);
    List<Transaction> findTransactionsByAccount_IdAndAsset_IdAndTransactionDateEquals(Long accountId, Long assetId, long transactionDate);
    List<Transaction> findByServiceProvider(ServiceProvider serviceProvider);
    List<Transaction> findByAccount_IdOrderByTransactionDateDesc(Long accountId);
}
