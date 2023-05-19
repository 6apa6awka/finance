package org.first.finance.db.mongo.repository;

import org.first.finance.db.mongo.entity.Account;
import org.first.finance.db.mongo.entity.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, String> {
    Category findFirstByName(String name);
}
