package org.first.finance.automation;

import jakarta.transaction.Transactional;
import org.first.finance.core.services.CategoryService;
import org.first.finance.core.services.ServiceProviderService;
import org.first.finance.db.mysql.entity.Category;
import org.first.finance.db.mysql.entity.ServiceProvider;
import org.first.finance.db.mysql.entity.ServiceProviderAlias;
import org.first.finance.db.mysql.entity.ServiceProviderAliasType;
import org.first.finance.db.mysql.repository.CategoryRepository;
import org.first.finance.db.mysql.repository.ServiceProviderAliasRepository;
import org.first.finance.db.mysql.repository.ServiceProviderRepository;
import org.first.finance.sheets.core.GoogleSheetsDocument;
import org.first.finance.sheets.service.GoogleSheetsProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceProviderUpdater {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceProviderUpdater.class);
    private ServiceProviderService serviceProviderService;
    private ServiceProviderRepository serviceProviderRepository;

    private CategoryService categoryService;
    private ServiceProviderAliasRepository serviceProviderAliasRepository;
    private GoogleSheetsProcessingService googleSheetsProcessingService;

    public void start() {
        update(googleSheetsProcessingService.read(GoogleSheetsDocument.SERVICE_PROVIDERS));
        List<String> serviceProviders = serviceProviderRepository.findTop100DistinctNameByApprovedIsFalse();
        googleSheetsProcessingService.write(GoogleSheetsDocument.SERVICE_PROVIDERS, map(serviceProviders));
    }

    private List<List<Object>> map(List<String> serviceProviders) {
        List<List<Object>> result = new ArrayList<>(100);
        for (int i = 0; i < 100; i ++) {
            if (serviceProviders.size() < i + 1) {
                result.add(List.of("", "", "", ""));
            } else {
                result.add(List.of(serviceProviders.get(i), "", "", ""));
            }
        }
        return result;
    }

    private void update(List<ArrayList<String>> values) {
        values.stream().filter(l -> l.size() > 1).forEach(this::mapServiceProvider);
    }

    @Transactional
    private void mapServiceProvider(List<String> values) {
        if (values.size() < 2) {
            LOG.error("Values should contain at least old name and new name");
            return;
        }
        String category = null;
        String mnemonic = null;
        String name = values.get(1);
        String alias = values.get(0);
        if (ObjectUtils.isEmpty(alias)) {
            LOG.error("Please check aliases for service provider. One of them is null");
            return;
        }
        switch (values.size()) {
            case 4 : {
                category = values.get(3);
            }
            case 3 : {
                mnemonic = values.get(2);
                if (ObjectUtils.isEmpty(name)) {
                    name = mnemonic;
                }
            }
        }
        if (ObjectUtils.isEmpty(name)) {
            LOG.error("Name can't be null for service provider {}", alias);
            return;
        }

        ServiceProvider existingServiceProvider = serviceProviderRepository.findFirstByName(name);
        ServiceProvider serviceProvider = serviceProviderRepository.findFirstByName(alias);
        if (existingServiceProvider == null) {
            serviceProvider.setName(name);
        } else {
            serviceProvider = existingServiceProvider;
        }
        if (!ObjectUtils.isEmpty(category)) {
            serviceProvider.setCategory(category.toUpperCase());
            serviceProviderRepository.save(serviceProvider);
        }
        serviceProvider.setApproved(true);
        serviceProviderRepository.save(serviceProvider);

        ServiceProviderAlias serviceProviderAlias = serviceProviderAliasRepository.findFirstByMnemonic(mnemonic);
        if (serviceProviderAlias == null) {
            serviceProviderAlias = new ServiceProviderAlias();
            serviceProviderAlias.setServiceProvider(serviceProvider);
            serviceProviderAlias.setType(ServiceProviderAliasType.MNEMONIC);
            serviceProviderAlias.setValue(mnemonic);
            serviceProviderAliasRepository.save(serviceProviderAlias);
            serviceProviderService.updateServiceProvidersThatApplicable(serviceProviderAlias);
        }
    }

    @Autowired
    public void setServiceProviderService(ServiceProviderService serviceProviderService) {
        this.serviceProviderService = serviceProviderService;
    }

    @Autowired
    public void setServiceProviderRepository(ServiceProviderRepository serviceProviderRepository) {
        this.serviceProviderRepository = serviceProviderRepository;
    }

    @Autowired
    public void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Autowired
    public void setServiceProviderAliasRepository(ServiceProviderAliasRepository serviceProviderAliasRepository) {
        this.serviceProviderAliasRepository = serviceProviderAliasRepository;
    }

    @Autowired
    public void setGoogleSheetsProcessingService(GoogleSheetsProcessingService googleSheetsProcessingService) {
        this.googleSheetsProcessingService = googleSheetsProcessingService;
    }
}
