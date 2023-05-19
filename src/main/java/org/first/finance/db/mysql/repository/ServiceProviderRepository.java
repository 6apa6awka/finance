package org.first.finance.db.mysql.repository;

import org.first.finance.db.mysql.entity.ServiceProvider;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

public interface ServiceProviderRepository extends ListCrudRepository<ServiceProvider, Long> {
    ServiceProvider findFirstByName(String name);

    @Query(value = "SELECT * FROM service_provider WHERE ?1 LIKE CONCAT('%', name, '%') LIMIT 1",
            nativeQuery = true)
    ServiceProvider findFirstByNameContainingIgnoreCase(String text);
}
