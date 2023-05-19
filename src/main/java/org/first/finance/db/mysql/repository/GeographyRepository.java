package org.first.finance.db.mysql.repository;

import org.first.finance.db.mysql.entity.Account;
import org.first.finance.db.mysql.entity.Geography;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

public interface GeographyRepository extends ListCrudRepository<Geography, Long> {
    Geography findByNameAndParentGeographyIsNull(String name);

    @Query(value = "SELECT * FROM geography WHERE ?1 LIKE CONCAT('%', name, '%') AND parent_geography_id = ?2 LIMIT 1",
            nativeQuery = true)
    Geography findByNameLikeAndParentGeography(String name, Long parentGeographyId);
}
