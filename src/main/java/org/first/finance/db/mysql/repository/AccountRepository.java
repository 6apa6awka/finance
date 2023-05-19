package org.first.finance.db.mysql.repository;

import org.first.finance.db.mysql.entity.Account;
import org.springframework.data.repository.ListCrudRepository;

public interface AccountRepository extends ListCrudRepository<Account, Long> {
    boolean existsAccountByName(String name);

    Account findAccountByName(String name);
}
