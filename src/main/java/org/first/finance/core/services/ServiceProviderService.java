package org.first.finance.core.services;

import org.first.finance.db.mysql.entity.ServiceProvider;
import org.first.finance.db.mysql.entity.ServiceProviderAlias;
import org.first.finance.db.mysql.entity.ServiceProviderAliasType;
import org.first.finance.db.mysql.repository.ServiceProviderAliasRepository;
import org.first.finance.db.mysql.repository.ServiceProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceProviderService {
    private ServiceProviderRepository serviceProviderRepository;
    private ServiceProviderAliasRepository serviceProviderAliasRepository;
    public ServiceProviderService() {
        //serviceProviderDescriptions.put();
    }

    public ServiceProvider findByText(String textToResolve) {
        ServiceProvider serviceProvider = serviceProviderAliasRepository.findFirstByAlias(textToResolve).getServiceProvider();
        if (serviceProvider != null) {
            return serviceProvider;
        }
        serviceProvider = serviceProviderAliasRepository.findFirstByMnemonicContainingIgnoreCase(textToResolve).getServiceProvider();
        if (serviceProvider != null) {
            ServiceProviderAlias serviceProviderAlias = new ServiceProviderAlias();
            serviceProviderAlias.setValue(textToResolve);
            serviceProviderAlias.setServiceProvider(serviceProvider);
            serviceProviderAlias.setType(ServiceProviderAliasType.ALIAS);
            serviceProviderAliasRepository.save(serviceProviderAlias);
        }
        return serviceProvider;
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
