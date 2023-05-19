package org.first.finance.db.mongo.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "account")
public class SavingAccount extends Account{
    private List<SavingPeriod> savingPeriods;
}
