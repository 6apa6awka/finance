/*
package org.first.finance.db.mongo.services;

import org.first.finance.db.mongo.entity.Asset;
import org.first.finance.db.mongo.repository.AssetRepository;
import org.first.finance.db.mongo.repository.ServiceProviderRepository;
import org.first.finance.db.mongo.entity.Account;
import org.first.finance.db.mongo.entity.Transaction;
import org.first.finance.db.mongo.repository.AccountRepository;
import org.first.finance.db.mongo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;

@Service
public class TransactionOrmService {
    private TransactionRepository transactionRepository;
    private AccountRepository accountRepository;
    private AssetRepository assetRepository;
    private ServiceProviderRepository serviceProviderRepository;

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

    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Transaction findById(String id) {
        return transactionRepository.findById(id).orElseThrow();
    }

    public Collection<Transaction> findTransactionsForAssetPeriod(String accountId, String assetId, long startTime, long endTime){
        return transactionRepository.findTransactionsByAccount_IdAndAsset_IdAndTransactionDateBetween(accountId, assetId, startTime, endTime);
    }

    public Collection<Transaction> findTransactionsForAccountPeriod(String accountId, long transactionDate){
        return transactionRepository.findTransactionsByAccount_IdAndTransactionDateEquals(accountId, transactionDate);
    }

    public Iterable<Transaction> findAllTransactions() {
        return transactionRepository.findAll();
    }

    public Iterable<Transaction> findTransactionsByAccountId(String accountId) {
        return transactionRepository.findTransactionsByAccount_Id(accountId);
    }

    public Transaction createEmptyTransaction(String accountId) {
        Transaction transaction = new Transaction();
        transaction.setAccount(accountRepository.findById(accountId).orElse(null));
        return transactionRepository.save(transaction);
    }

    public Transaction findFirstByServiceProviderIsNull() {
        return transactionRepository.findFirstByAccount_IdAndServiceProviderIsNull("63ffa7f865af6f3b27622946");
    }

    @Autowired
    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Autowired
    public void setAssetRepository(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Autowired
    public void setServiceProviderRepository(ServiceProviderRepository serviceProviderRepository) {
        this.serviceProviderRepository = serviceProviderRepository;
    }
}
*/
