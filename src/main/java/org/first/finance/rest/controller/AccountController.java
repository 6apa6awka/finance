/*
package org.first.finance.rest.controller;

import org.first.finance.db.mongo.entity.Account;
import org.first.finance.db.mongo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("account")
public class AccountController {
    private AccountRepository accountRepository;

    @GetMapping(value = "/", produces = "application/json")
    public ResponseEntity<Iterable<Account>> getAllAccounts() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccessControlAllowOrigin("*");
        return new ResponseEntity<>(accountRepository.findAll(), httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = "/", produces = "application/json")
    public Account createAccount(@RequestBody Account account) {
        return accountRepository.save(account);
    }

    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
}
*/
