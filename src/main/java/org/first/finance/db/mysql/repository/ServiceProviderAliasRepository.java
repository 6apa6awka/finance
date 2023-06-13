package org.first.finance.db.mysql.repository;

import org.first.finance.db.mysql.entity.ServiceProvider;
import org.first.finance.db.mysql.entity.ServiceProviderAlias;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface ServiceProviderAliasRepository extends ListCrudRepository<ServiceProviderAlias, Long> {
    @Query(value = "SELECT * FROM service_provider_alias WHERE ?1 = value AND type = 1 LIMIT 1",
            nativeQuery = true)
    ServiceProviderAlias findFirstByAlias(String alias);
    @Query(value = "SELECT * FROM service_provider_alias WHERE type = 0 AND ?1 LIKE CONCAT('%', value, '%') LIMIT 1",
            nativeQuery = true)
    ServiceProviderAlias findFirstByMnemonicContainingIgnoreCase(String text);

    @Query(value = "SELECT * FROM service_provider_alias WHERE type = 0 AND value = ?1 LIMIT 1",
            nativeQuery = true)
    ServiceProviderAlias findFirstByMnemonic(String text);

    void deleteAllByServiceProvider(ServiceProvider serviceProvider);
    List<ServiceProviderAlias> findAllByServiceProvider(ServiceProvider serviceProvider);
}
