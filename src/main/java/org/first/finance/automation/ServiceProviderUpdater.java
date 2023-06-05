package org.first.finance.automation;

import jakarta.transaction.Transactional;
import org.first.finance.core.services.ServiceProviderService;
import org.first.finance.db.mysql.entity.ServiceProvider;
import org.first.finance.db.mysql.entity.ServiceProviderAlias;
import org.first.finance.db.mysql.entity.ServiceProviderAliasType;
import org.first.finance.db.mysql.repository.ServiceProviderAliasRepository;
import org.first.finance.db.mysql.repository.ServiceProviderRepository;
import org.first.finance.sheets.core.GoogleSheetsDocument;
import org.first.finance.sheets.service.GoogleSheetsProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceProviderUpdater {
    private ServiceProviderService serviceProviderService;
    private ServiceProviderRepository serviceProviderRepository;
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
                result.add(List.of("", "", ""));
            } else {
                result.add(List.of(serviceProviders.get(i), "", ""));
            }
        }
        return result;
    }

    private void update(List<ArrayList<String>> values) {
        values.stream().filter(l -> l.size() > 1).forEach(this::mapServiceProvider);
    }

    @Transactional
    private void mapServiceProvider(List<String> values) {
        ServiceProvider serviceProvider = serviceProviderRepository.findFirstByName(values.get(0));
        serviceProvider.setName(values.get(1));
        serviceProvider.setApproved(true);
        serviceProviderRepository.save(serviceProvider);
        if (values.size() == 3) {
            String mnemonic = values.get(2);
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
    public void setServiceProviderAliasRepository(ServiceProviderAliasRepository serviceProviderAliasRepository) {
        this.serviceProviderAliasRepository = serviceProviderAliasRepository;
    }

    @Autowired
    public void setGoogleSheetsProcessingService(GoogleSheetsProcessingService googleSheetsProcessingService) {
        this.googleSheetsProcessingService = googleSheetsProcessingService;
    }
}
