package org.first.finance.automation.selenium.services;

import org.first.finance.automation.selenium.core.SeleniumPath;
import org.first.finance.automation.selenium.core.UITransactionField;
import org.first.finance.automation.selenium.utils.DateUtils;
import org.first.finance.core.dto.TransactionDto;
import org.first.finance.db.mysql.entity.Geography;
import org.first.finance.db.mysql.entity.ServiceProvider;
import org.first.finance.db.mysql.entity.ServiceProviderAlias;
import org.first.finance.db.mysql.entity.ServiceProviderAliasType;
import org.first.finance.db.mysql.repository.GeographyRepository;
import org.first.finance.db.mysql.repository.ServiceProviderAliasRepository;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.first.finance.automation.selenium.core.UITransactionField.CATEGORY;
import static org.first.finance.automation.selenium.core.UITransactionField.CREDIT;
import static org.first.finance.automation.selenium.core.UITransactionField.DEBIT;
import static org.first.finance.automation.selenium.core.UITransactionField.DESCRIPTION;

@Service
@PropertySource(value = "classpath:selenium/path/credit.properties")
public class ScotiaCreditAccountSeleniumParser extends ScotiaCardAccountSeleniumParser {
    private GeographyRepository geographyRepository;
    private ServiceProviderAliasRepository serviceProviderAliasRepository;

    @Override
    public ServiceProvider resolveServiceProvider(String name, String category) {
        ServiceProvider serviceProvider = super.resolveServiceProvider(name, category);
        if (serviceProvider == null) {
            serviceProvider = parseFromDescription(name);
        }
        return serviceProvider;
    }

    @Override
    protected TransactionDto collectTransactionData(WebElement uiTransaction) {
        TransactionDto transactionDto = super.collectTransactionData(uiTransaction);
        if (transactionDto != null) {
            String date = uiTransaction.findElement(getPath(SeleniumPath.TRANSACTIONS_LIST_FIELD_DATE)).getText();
            if (date.isEmpty()) {
                throw new IllegalArgumentException("Date can't be null");
            }
            transactionDto.setTransactionDate(DateUtils.toLocalDate(date).toEpochDay());
        }
        return transactionDto;
    }

    @Override
    protected UITransactionField[] getUITransactionFieldsInOrder() {
        return new UITransactionField[] {
                CATEGORY,
                DESCRIPTION,
                CREDIT,
                DEBIT
        };
    }

    protected ServiceProvider parseFromDescription(String description) {
        String[] wordTokens = description.split(" ");
        String lastToken = wordTokens[wordTokens.length - 1];
        ServiceProvider serviceProvider = null;
        String name;
        if (lastToken.length() == 2) {
            Geography geography = geographyRepository.findByNameAndParentGeographyIsNull(lastToken.toUpperCase());
            if (geography != null) {
                //String locationToken = wordTokens[wordTokens.length - 2];
                //Geography location = geographyRepository.findByNameLikeAndParentGeography(locationToken.toUpperCase(), geography.getId());
                name = Arrays.stream(wordTokens).limit(wordTokens.length - 2).collect(Collectors.joining(" "));
                serviceProvider = getServiceProviderRepository().findFirstByName(name);
            }
        }
        if (serviceProvider == null) {
            serviceProvider = new ServiceProvider();
            serviceProvider.setApproved(false);
            serviceProvider.setName(description);
            serviceProvider = getServiceProviderRepository().save(serviceProvider);
        }
        ServiceProviderAlias serviceProviderAlias = new ServiceProviderAlias();
        serviceProviderAlias.setValue(description);
        serviceProviderAlias.setServiceProvider(serviceProvider);
        serviceProviderAlias.setType(ServiceProviderAliasType.ALIAS);
        serviceProviderAliasRepository.save(serviceProviderAlias);
        return serviceProvider;
    }

    @Override
    public String getApplicableAccountType() {
        return "Credit";
    }

    @Autowired
    public void setGeographyRepository(GeographyRepository geographyRepository) {
        this.geographyRepository = geographyRepository;
    }

    @Autowired
    public void setServiceProviderAliasRepository(ServiceProviderAliasRepository serviceProviderAliasRepository) {
        this.serviceProviderAliasRepository = serviceProviderAliasRepository;
    }
}
