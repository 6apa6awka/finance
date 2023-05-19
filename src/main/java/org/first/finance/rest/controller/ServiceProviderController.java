/*
package org.first.finance.rest.controller;

import org.first.finance.db.mongo.entity.Category;
import org.first.finance.db.mongo.entity.Transaction;
import org.first.finance.db.mongo.repository.CategoryRepository;
import org.first.finance.rest.entity.TransactionDto;
import org.first.finance.ui.AutomaticServiceProviderDefiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("serviceProvider")
public class ServiceProviderController {
    private CategoryRepository categoryRepository;
    private AutomaticServiceProviderDefiner automaticServiceProviderDefiner;

    @GetMapping(value = "/categories", produces = "application/json")
    public ResponseEntity<Iterable<Category>> getAllCategories() {
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity<>(categoryRepository.findAll(), httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = "/updateServiceProvider", produces = "application/json")
    public ResponseEntity<Transaction> updateServiceProvider(@RequestBody TransactionDto transactionDto) {
        return new ResponseEntity<>(automaticServiceProviderDefiner.update(transactionDto), new HttpHeaders(), HttpStatus.OK);
    } 

    @Autowired
    public void setCategoryRepository(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Autowired
    public void setAutomaticServiceProviderDefiner(AutomaticServiceProviderDefiner automaticServiceProviderDefiner) {
        this.automaticServiceProviderDefiner = automaticServiceProviderDefiner;
    }
}
*/
