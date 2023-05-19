/*
package org.first.finance.rest.controller;

import org.first.finance.db.mongo.services.TransactionOrmService;
import org.first.finance.db.mongo.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("transaction")
public class TransactionController {
    @Autowired
    private TransactionOrmService transactionService;

    @PostMapping(value = "/", produces = "application/json")
    public Transaction createTransaction(@RequestBody Transaction transaction) {
        return transactionService.createTransaction(transaction);
    }

    @PostMapping(value = "/new", produces = "application/json")
    public ResponseEntity<Transaction> createEmptyTransaction(@RequestBody Transaction transaction) {
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity<>(transactionService.createEmptyTransaction(transaction.getAccount().getId()), httpHeaders, HttpStatus.OK);
    }

    @GetMapping(value = "/", produces = "application/json")
    public ResponseEntity<Iterable<Transaction>> getAllTransactions() {
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity<>(transactionService.findAllTransactions(), httpHeaders, HttpStatus.OK);
    }

    @GetMapping(value = "/{accountId}", produces = "application/json")
    public ResponseEntity<Iterable<Transaction>> getTransactionsByAccount(@PathVariable("accountId") String accountId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity<>(transactionService.findTransactionsByAccountId(accountId), httpHeaders, HttpStatus.OK);
    }
}
*/
