package org.first.finance.db.mysql.repository;

import org.first.finance.db.mysql.entity.ServiceProviderAlias;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

public interface ServiceProviderAliasRepository extends ListCrudRepository<ServiceProviderAlias, Long> {
    @Query(value = "SELECT * FROM service_provider_alias WHERE ?1 = value AND type = 'ALIAS' LIMIT 1",
            nativeQuery = true)
    ServiceProviderAlias findFirstByAlias(String alias);
    @Query(value = "SELECT * FROM service_provider_alias WHERE type = 'MNEMONIC' AND ?1 LIKE CONCAT('%', mnemonic, '%') LIMIT 1",
            nativeQuery = true)
    ServiceProviderAlias findFirstByMnemonicContainingIgnoreCase(String text);
}
