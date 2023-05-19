/*
package org.first.finance.db.mongo.repository;

import org.first.finance.db.mongo.entity.ServiceProvider;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;

public interface ServiceProviderRepository  extends ListCrudRepository<ServiceProvider, String> {
    ServiceProvider findFirstByName(String name);
    @Query("SELECT sp FROM ServiceProvider sp WHERE LOWER(:text) LIKE LOWER(CONCAT(CONCAT('%', sp.name), '%'))")
    ServiceProvider findByNameIsLikeIgnoreCase(String text);
    ServiceProvider findByNameContainingIgnoreCase(String text);
    ServiceProvider findAllByServiceProviderAliasesIsNullOrServiceProviderAliasesEmpty(String text);
}
*/
