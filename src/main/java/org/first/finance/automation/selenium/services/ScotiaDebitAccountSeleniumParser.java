package org.first.finance.automation.selenium.services;

import org.first.finance.automation.selenium.core.UITransactionField;
import org.first.finance.db.mysql.entity.ServiceProvider;
import org.first.finance.db.mysql.entity.ServiceProviderAlias;
import org.first.finance.db.mysql.entity.ServiceProviderAliasType;
import org.first.finance.db.mysql.repository.ServiceProviderAliasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import static org.first.finance.automation.selenium.core.UITransactionField.CREDIT;
import static org.first.finance.automation.selenium.core.UITransactionField.DATE;
import static org.first.finance.automation.selenium.core.UITransactionField.DEBIT;
import static org.first.finance.automation.selenium.core.UITransactionField.DESCRIPTION;

@Service
@PropertySource(value = "classpath:selenium/path/debit.properties")
public class ScotiaDebitAccountSeleniumParser extends ScotiaCardAccountSeleniumParser {
    private ServiceProviderAliasRepository serviceProviderAliasRepository;

    @Override
    protected UITransactionField[] getUITransactionFieldsInOrder() {
        return new UITransactionField[] {
                DATE,
                DESCRIPTION,
                CREDIT,
                DEBIT
        };
    }

    @Override
    protected ServiceProvider resolveServiceProvider(String name, String category) {
        ServiceProvider serviceProvider = super.resolveServiceProvider(name, category);
        if (serviceProvider == null) {
            serviceProvider = new ServiceProvider();
            serviceProvider.setApproved(false);
            serviceProvider.setName(name);
            serviceProvider = getServiceProviderRepository().save(serviceProvider);
            ServiceProviderAlias serviceProviderAlias = new ServiceProviderAlias();
            serviceProviderAlias.setValue(name);
            serviceProviderAlias.setServiceProvider(serviceProvider);
            serviceProviderAlias.setType(ServiceProviderAliasType.ALIAS);
            serviceProviderAliasRepository.save(serviceProviderAlias);
        }
        return serviceProvider;
    }

    @Override
    public String getApplicableAccountType() {
        return "Debit";
    }

    @Autowired
    public void setServiceProviderAliasRepository(ServiceProviderAliasRepository serviceProviderAliasRepository) {
        this.serviceProviderAliasRepository = serviceProviderAliasRepository;
    }
}
