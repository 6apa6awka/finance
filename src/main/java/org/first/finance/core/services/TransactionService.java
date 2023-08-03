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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private static final Logger LOG = LoggerFactory.getLogger(TransactionService.class);
    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private AssetRepository assetRepository;
    private ServiceProviderRepository serviceProviderRepository;

    @Transactional
    public void processTransactions(Collection<TransactionDto> transactionsToProcess, Long accountId, Long assetId, LocalDate currentDate) {
        Account account = accountRepository.findById(accountId).orElseThrow();
        Collection<Transaction> transactions = transactionsToProcess.stream()
                .map(this::convertDtoToEntity)
                .peek(transaction -> transaction.setAccount(account))
                .toList();
        Collection<Transaction> dbTransactions;
        if (assetId != null) {
            dbTransactions = transactionRepository.findTransactionsByAccount_IdAndAsset_IdAndTransactionDateEquals(accountId, assetId, currentDate);
        } else {
            dbTransactions = transactionRepository.findTransactionsByAccount_IdAndTransactionDateEquals(accountId, currentDate);
        }
        for (Transaction transactionToProcess : transactions) {
            int count = Collections.frequency(transactions, transactionToProcess);
            while (count > Collections.frequency(dbTransactions, transactionToProcess)) {
                dbTransactions.add(createTransaction(transactionToProcess));
                LOG.info("New transaction added to {} account, {}", account.getName(), transactionToProcess);
            }
        }
    }

    public void compareTransactionsForAccount(Collection<TransactionDto> uiTransactionsDto, Account account) {
        Collection<Transaction> uiTransactions = uiTransactionsDto.stream()
                .map(this::convertDtoToEntity)
                .peek(transaction -> transaction.setAccount(account))
                .collect(Collectors.toCollection(ArrayList::new));

        Collection<Transaction> dbTransactions = transactionRepository.findByAccount_IdOrderByTransactionDateDesc(account.getId());
        uiTransactions.forEach(dbTransactions::remove);
        dbTransactions.forEach(System.out::println);
        System.out.println("-------------------------------------------------------------");
        dbTransactions = transactionRepository.findByAccount_IdOrderByTransactionDateDesc(account.getId());
        dbTransactions.forEach(uiTransactions::remove);
        uiTransactions.forEach(System.out::println);
    }

    @Transactional
    public void processTransactions(Collection<TransactionDto> transactionsToProcess, Long accountId, LocalDate currentDate) {
        processTransactions(transactionsToProcess, accountId, null, currentDate);
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
        if (transactionDto.getAssetId() != null) {
            transaction.setAsset(assetRepository.findById(transactionDto.getAssetId()).orElseThrow());
        }
        transaction.setCreationTime(LocalDateTime.now());
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
