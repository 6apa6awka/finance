package org.first.finance.core.services;

import jakarta.transaction.Transactional;
import org.first.finance.db.mysql.entity.Category;
import org.first.finance.db.mysql.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CategoryService {
    private CategoryRepository categoryRepository;
    public Category findOrCreate(String name) {
        Category category = categoryRepository.findFirstByNameIgnoreCase(name);
        if (category == null) {
            category = new Category();
            category.setName(name);
            categoryRepository.save(category);
        }
        return category;
    }

    @Autowired
    public void setCategoryRepository(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
}
