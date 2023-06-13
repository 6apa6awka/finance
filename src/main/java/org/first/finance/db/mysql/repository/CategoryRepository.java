package org.first.finance.db.mysql.repository;

import org.first.finance.db.mysql.entity.Category;
import org.first.finance.db.mysql.entity.Geography;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

public interface CategoryRepository extends ListCrudRepository<Category, Long> {
    Category findFirstByNameIgnoreCase(String name);
}
