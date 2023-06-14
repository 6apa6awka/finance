package org.first.finance.core.services;

import jakarta.transaction.Transactional;
import org.first.finance.core.dto.TransactionDto;
import org.first.finance.db.mysql.entity.Account;
import org.first.finance.db.mysql.entity.Asset;
import org.first.finance.db.mysql.entity.Transaction;
import org.first.finance.db.mysql.repository.AccountRepository;
import org.first.finance.db.mysql.repository.AssetRepository;
import org.first.finance.db.mysql.repository.ServiceProviderRepository;
import org.first.finance.db.mysql.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;

@Service
public class TransactionService {
    private static final Logger LOG = LoggerFactory.getLogger(TransactionService.class);
    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private AssetRepository assetRepository;
    private ServiceProviderRepository serviceProviderRepository;

    @Transactional
    public void processTransactions(Collection<TransactionDto> transactionsToProcess, Account account, long currentDate) {
        Collection<Transaction> transactions = transactionsToProcess.stream()
                .map(this::convertDtoToEntity)
                .peek(transaction -> transaction.setAccount(account))
                .toList();
        Collection<Transaction> dbTransactions = transactionRepository.findTransactionsByAccount_IdAndTransactionDateEquals(account.getId(), currentDate);
        for (Transaction transactionToProcess : transactions) {
            int count = Collections.frequency(transactions, transactionToProcess);
            while (count > Collections.frequency(dbTransactions, transactionToProcess)) {
                dbTransactions.add(createTransaction(transactionToProcess));
                LOG.info("New transaction added to {} account, {}", account.getName(), transactionToProcess);
            }
        }
    }

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

    public Transaction convertDtoToEntity(TransactionDto transactionDto) {
        Transaction transaction = new Transaction();
        transaction.setAccount(accountRepository.findById(transactionDto.getAccountId()).orElseThrow());
        transaction.setServiceProvider(serviceProviderRepository.findById(transactionDto.getServiceProviderId()).orElseThrow());
        transaction.setCreationTime(System.currentTimeMillis());
        transaction.setType(transactionDto.getType());
        transaction.setDescription(transactionDto.getDescription());
        transaction.setAmount(transactionDto.getAmount());
        transaction.setTransactionDate(transactionDto.getTransactionDate());
        return transaction;
    }

    @Autowired
    public void setServiceProviderRepository(ServiceProviderRepository serviceProviderRepository) {
        this.serviceProviderRepository = serviceProviderRepository;
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
    public void setAssetRepository(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }
}
