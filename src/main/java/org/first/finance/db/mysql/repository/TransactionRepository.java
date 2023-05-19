package org.first.finance.db.mysql.repository;


import org.first.finance.db.mysql.entity.Transaction;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface TransactionRepository extends ListCrudRepository<Transaction, Long> {
    List<Transaction> findTransactionsByAccount_IdAndTransactionDateEquals(Long id, long currentDate);
    List<Transaction> findTransactionsByAccount_IdAndAsset_IdAndTransactionDateBetween(Long accountId, Long assetId, long startDate, long endDate);

}
