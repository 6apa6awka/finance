/*
package org.first.finance.db.mongo.services;

import org.first.finance.configuration.IgnoreScan;
import org.first.finance.core.dto.AccountDto;
import org.first.finance.db.mongo.entity.Account;
import org.first.finance.db.mongo.entity.AccountType;
import org.first.finance.db.mongo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@IgnoreScan
@Service
public class AccountOrmService {
    private AccountRepository accountRepository;

    public boolean isAccountExists(String accountName) {
        return accountRepository.existsAccountByName(accountName);
    }

    public Account findById(String id) {
        return accountRepository.findById(id).orElseThrow();
    }

    public Account findAccountByName(String name) {
        return accountRepository.findAccountByName(name);
    }

    public Account createAccount(AccountDto accountDto) {
        Account account = new Account();
        account.setName(accountDto.getName());
        account.setAccountType(AccountType.valueOf(accountDto.getAccountType()));
        account.setAccountNumber(accountDto.getAccountNumber());
        account.setAmount(accountDto.getAmount());
        account.setInstitution(accountDto.getInstitution());
        return accountRepository.save(account);
    }

    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
}
*/
