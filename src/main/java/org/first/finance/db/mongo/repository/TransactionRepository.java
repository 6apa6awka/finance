/*
package org.first.finance.db.mongo.repository;

import org.first.finance.db.mongo.entity.AccountType;
import org.first.finance.db.mongo.entity.Transaction;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Collection;
import java.util.List;

public interface TransactionRepository extends ListCrudRepository<Transaction, String> {
    @Query("{'serviceProvider': {'serviceProviderAliases' :  {$exists: true}}}")
    List<Transaction> findAllTransactionsWithServiceProviderToProcess();
    Collection<Transaction> findTransactionsByAccount_Id(String accountId);
    Collection<Transaction> findTransactionsByAccount_IdAndAsset_IdAndTransactionDateBetween(String accountId, String assetId, long startTime, long endTime);
    Collection<Transaction> findTransactionsByAccount_IdAndTransactionDateEquals(String accountId, long transactionDate);
    Transaction findFirstByAccount_IdAndServiceProviderIsNull(String accountId);
    Collection<Transaction> findTransactionsByAccount_AccountTypeIn(List<AccountType> accountTypes);
    Transaction findFirstByServiceProviderIsNull();
}
*/
