package org.first.finance.ui;

import org.first.finance.db.mysql.entity.Transaction;
import org.first.finance.db.mysql.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.List;

@Service
public class AutomaticServiceProviderDefiner {
    private TransactionRepository transactionRepository;
    /*private MongoTemplate mongoTemplate;
    TransactionOrmService transactionOrmService;

    ServiceProviderRepository serviceProviderRepository;
    ServiceProviderAliasRepository serviceProviderAliasRepository;
    CategoryRepository categoryRepository;*/

    public Transaction start() {
        List<Transaction> transactionsToUpdate = transactionRepository.findAll();
        //List<Transaction> transactionsToUpdate = transactionRepository.findAllTransactionsWithServiceProviderToProcess();
        if (transactionsToUpdate.isEmpty()) {
            Transaction transaction = new Transaction();
            transaction.setDescription("Test transaction");
            transactionRepository.save(transaction);
        }
        /*ServiceProviderAlias serviceProviderAlias = serviceProviderAliasRepository.findByAlias(transactionsToUpdate.getDescription());
        if (serviceProviderAlias == null) {
            serviceProviderAlias = serviceProviderAliasRepository.findByMnemonicIsLikeIgnoreCase(transactionsToUpdate.getDescription());
        }
        ServiceProvider serviceProvider;
        if (serviceProviderAlias == null) {
            serviceProvider = serviceProviderRepository.findByNameIsLikeIgnoreCase(transactionsToUpdate.getDescription());
        } else {
            serviceProvider = serviceProviderRepository.findById(serviceProviderAlias.getServiceProviderId()).orElseThrow();
        }
        if (serviceProvider != null) {
            transactionsToUpdate.setServiceProvider(serviceProvider);
            transactionOrmService.save(transactionsToUpdate);
            return start();
        } else {
            return transactionsToUpdate;
        }*/
        return null;
    }

    /*public void updateServiceProviders() {
        Iterable<ServiceProvider> serviceProviders = serviceProviderRepository.findAll();
        Iterator<ServiceProvider> iterator = serviceProviders.iterator();
        List<ServiceProviderAlias> serviceProvidersMnemonics = serviceProviderAliasRepository.findAllByMnemonicIsNotNull();
        while (iterator.hasNext()) {
            ServiceProvider serviceProvider = iterator.next();
            ServiceProviderAlias serviceProviderAlias = serviceProviderAliasRepository.findByAlias(serviceProvider.getName());
            if (serviceProviderAlias == null) {
                serviceProviderAlias = serviceProvidersMnemonics.stream()
                        .filter(spm -> serviceProvider.getName().contains(spm.getMnemonic()))
                        .findFirst().orElse(null);
            }
            if (serviceProviderAlias == null) {
                //parseDescription(serviceProvider);
            }
        }
    }

    private void parseDescription(ServiceProvider serviceProvider, BigDecimal transactionAmount) {
        String name = serviceProvider.getName();
        if ("DEPOSIT\nMB-DEP".equals(name)) {
            if (transactionAmount.remainder(new BigDecimal("26")).compareTo(BigDecimal.ZERO) == 0) {
                int days = transactionAmount.divide(new BigDecimal("26"), RoundingMode.UNNECESSARY).intValue();
                if (days >= 10 && days <= 19) {
                    serviceProvider.setName("French Courses");
                }
            }
            if (new BigDecimal("178.20").compareTo(BigDecimal.ZERO) == 0) {
                serviceProvider.setName("Canada Child Benefit");
            }
        }
    }

    public Transaction update(TransactionDto transactionDto) {
        ServiceProvider serviceProvider = serviceProviderRepository.findFirstByName(transactionDto.getServiceProviderName());
        if (serviceProvider == null) {
            serviceProvider = new ServiceProvider();
            serviceProvider.setName(transactionDto.getServiceProviderName());
            serviceProvider.setCategory(transactionDto.getCategory());
            serviceProvider = serviceProviderRepository.save(serviceProvider);
        }
        ServiceProviderAlias serviceProviderAlias = new ServiceProviderAlias();
        serviceProviderAlias.setAlias(transactionDto.getDescription());
        serviceProviderAlias.setMnemonic(transactionDto.getMnemonic());
        serviceProviderAlias.setServiceProviderId(serviceProvider.getId());
        serviceProviderAliasRepository.save(serviceProviderAlias);
        Transaction transaction = transactionOrmService.findById(transactionDto.getTransactionId());
        transaction.setServiceProvider(serviceProvider);
        transactionOrmService.save(transaction);
        return start();
    }

    @Autowired
    public void setTransactionOrmService(TransactionOrmService transactionOrmService) {
        this.transactionOrmService = transactionOrmService;
    }

    @Autowired
    public void setServiceProviderRepository(ServiceProviderRepository serviceProviderRepository) {
        this.serviceProviderRepository = serviceProviderRepository;
    }

    @Autowired
    public void setServiceProviderAliasRepository(ServiceProviderAliasRepository serviceProviderAliasRepository) {
        this.serviceProviderAliasRepository = serviceProviderAliasRepository;
    }

    @Autowired
    public void setCategoryRepository(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }*/

    @Autowired
    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
}
