package org.first.finance.db.mysql.repository;

import org.first.finance.db.mysql.entity.ServiceProvider;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface ServiceProviderRepository extends ListCrudRepository<ServiceProvider, Long> {
    ServiceProvider findFirstByNameAndIsApprovedIsTrue(String name);
    ServiceProvider findFirstByName(String name);

    @Query(value = "SELECT * FROM service_provider WHERE ?1 LIKE CONCAT('%', name, '%') LIMIT 1",
            nativeQuery = true)
    ServiceProvider findFirstByNameContainingIgnoreCase(String text);

    @Query(value = "SELECT DISTINCT(name) FROM service_provider WHERE is_approved = false LIMIT 100",
            nativeQuery = true)
    List<String> findTop100DistinctNameByApprovedIsFalse();

    List<ServiceProvider> findByNameContainingAndIsApprovedIsFalse(String text);
}
