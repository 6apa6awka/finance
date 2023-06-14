package org.first.finance.core.services;

import jakarta.transaction.Transactional;
import org.first.finance.db.mysql.entity.ServiceProvider;
import org.first.finance.db.mysql.entity.ServiceProviderAlias;
import org.first.finance.db.mysql.entity.ServiceProviderAliasType;
import org.first.finance.db.mysql.entity.Transaction;
import org.first.finance.db.mysql.repository.ServiceProviderAliasRepository;
import org.first.finance.db.mysql.repository.ServiceProviderRepository;
import org.first.finance.db.mysql.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
public class ServiceProviderService {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceProviderService.class);
    private ServiceProviderRepository serviceProviderRepository;
    private ServiceProviderAliasRepository serviceProviderAliasRepository;
    private TransactionRepository transactionRepository;
    public ServiceProviderService() {
        //serviceProviderDescriptions.put();
    }

    public ServiceProvider findByText(String textToResolve) {
        ServiceProviderAlias serviceProviderAlias = serviceProviderAliasRepository.findFirstByAlias(textToResolve);
        ServiceProvider result;
        if (serviceProviderAlias != null) {
            result = serviceProviderAlias.getServiceProvider();
            LOG.debug("Found service provider {} for alias {}", result.getName(), serviceProviderAlias.getValue());
            return result;
        }
        result = serviceProviderRepository.findFirstByName(textToResolve);
        if (result != null) {
            ServiceProviderAlias newAlias = new ServiceProviderAlias();
            newAlias.setValue(textToResolve);
            newAlias.setServiceProvider(result);
            newAlias.setType(ServiceProviderAliasType.ALIAS);
            serviceProviderAliasRepository.save(newAlias);
            LOG.debug("Service provider with name {} exists, but no alias. It will be created.", result.getName());
            return result;
        }
        serviceProviderAlias = serviceProviderAliasRepository.findFirstByMnemonicContainingIgnoreCase(textToResolve);
        if (serviceProviderAlias != null) {
            ServiceProviderAlias newAlias = new ServiceProviderAlias();
            newAlias.setValue(textToResolve);
            newAlias.setServiceProvider(serviceProviderAlias.getServiceProvider());
            newAlias.setType(ServiceProviderAliasType.ALIAS);
            serviceProviderAliasRepository.save(newAlias);
            result = newAlias.getServiceProvider();
            LOG.debug("Found service provider {} for mnemonic {}. Adding new alias {}", result.getName(), serviceProviderAlias.getValue(), textToResolve);
            return result;
        }
        return null;
    }

    public void updateServiceProvidersThatApplicable(ServiceProviderAlias serviceProviderAlias) {
        List<ServiceProvider> serviceProviderList = serviceProviderRepository
                .findByNameContainingAndIsApprovedIsFalse(serviceProviderAlias.getValue());
        for (ServiceProvider serviceProvider : serviceProviderList) {
            updateServiceProvider(serviceProviderAlias.getServiceProvider(), serviceProvider);
        }
    }

    public void updateServiceProvider(ServiceProvider newServiceProvider, ServiceProvider serviceProviderToReplace) {
        transactionRepository.findByServiceProvider(serviceProviderToReplace)
                .forEach(t -> updateTransaction(t, newServiceProvider));
        serviceProviderAliasRepository.findAllByServiceProvider(serviceProviderToReplace)
                .forEach(t -> updateAlias(t, newServiceProvider));
        serviceProviderRepository.delete(serviceProviderToReplace);
    }

    private void updateTransaction(Transaction transaction, ServiceProvider serviceProvider) {
        transaction.setServiceProvider(serviceProvider);
        transactionRepository.save(transaction);
    }

    private void updateAlias(ServiceProviderAlias alias, ServiceProvider serviceProvider) {
        alias.setServiceProvider(serviceProvider);
        serviceProviderAliasRepository.save(alias);
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
