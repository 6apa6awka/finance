package org.first.finance.core.services;

import org.first.finance.db.mysql.entity.ServiceProvider;
import org.first.finance.db.mysql.entity.ServiceProviderAlias;
import org.first.finance.db.mysql.entity.ServiceProviderAliasType;
import org.first.finance.db.mysql.entity.Transaction;
import org.first.finance.db.mysql.repository.ServiceProviderAliasRepository;
import org.first.finance.db.mysql.repository.ServiceProviderRepository;
import org.first.finance.db.mysql.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service
public class ServiceProviderService {
    private ServiceProviderRepository serviceProviderRepository;
    private ServiceProviderAliasRepository serviceProviderAliasRepository;
    private TransactionRepository transactionRepository;
    public ServiceProviderService() {
        //serviceProviderDescriptions.put();
    }

    public ServiceProvider findByText(String textToResolve) {
        ServiceProviderAlias serviceProviderAlias = serviceProviderAliasRepository.findFirstByAlias(textToResolve);
        if (serviceProviderAlias != null) {
            return serviceProviderAlias.getServiceProvider();
        }
        serviceProviderAlias = serviceProviderAliasRepository.findFirstByMnemonicContainingIgnoreCase(textToResolve);
        if (serviceProviderAlias != null) {
            ServiceProviderAlias newAlias = new ServiceProviderAlias();
            newAlias.setValue(textToResolve);
            newAlias.setServiceProvider(serviceProviderAlias.getServiceProvider());
            newAlias.setType(ServiceProviderAliasType.ALIAS);
            serviceProviderAliasRepository.save(newAlias);
            return newAlias.getServiceProvider();
        }
        return null;
    }

    public void updateServiceProvidersThatApplicable(ServiceProviderAlias serviceProviderAlias) {
        List<ServiceProvider> serviceProviderList = serviceProviderRepository
                .findByNameContainingAndIsApprovedIsFalse(serviceProviderAlias.getValue());
        for (ServiceProvider serviceProvider : serviceProviderList) {
            List<Transaction> transactions = transactionRepository.findByServiceProvider(serviceProvider);
            transactions.forEach(t -> updateTransaction(t, serviceProviderAlias.getServiceProvider()));
            ServiceProviderAlias newServiceProviderAlias = new ServiceProviderAlias();
            newServiceProviderAlias.setValue(serviceProvider.getName());
            newServiceProviderAlias.setServiceProvider(serviceProviderAlias.getServiceProvider());
            newServiceProviderAlias.setType(ServiceProviderAliasType.ALIAS);
            serviceProviderAliasRepository.save(newServiceProviderAlias);
            serviceProviderRepository.delete(serviceProvider);
        }
    }

    private void updateTransaction(Transaction transaction, ServiceProvider serviceProvider) {
        transaction.setServiceProvider(serviceProvider);
        transactionRepository.save(transaction);
    }

    @Autowired
    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Autowired
    public void setServiceProviderRepository(ServiceProviderRepository serviceProviderRepository) {
        this.serviceProviderRepository = serviceProviderRepository;
    }

    @Autowired
    public void setServiceProviderAliasRepository(ServiceProviderAliasRepository serviceProviderAliasRepository) {
        this.serviceProviderAliasRepository = serviceProviderAliasRepository;
    }
}
