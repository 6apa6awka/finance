/*
package org.first.finance.db.mongo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.math.BigDecimal;
import java.util.Objects;

@Document
public class Transaction {
    @Id
    private String id;

    private TransactionType type;
    private BigDecimal amount;
    private String description;

    private Long creationTime;

    private Long transactionDate;

    @DocumentReference
    private Account account;

    @DocumentReference
    private Asset asset;

    @DocumentReference(lookup = "{ '_id' : ?#{#self.serviceProvider} }")
    private ServiceProvider serviceProvider;

    public boolean isDebit() {
        return TransactionType.DEBIT.equals(type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return type == that.type && Objects.equals(amount, that.amount) && description.equals(that.description) && Objects.equals(transactionDate, that.transactionDate) && Objects.equals(account, that.account) && Objects.equals(asset, that.asset) && Objects.equals(serviceProvider, that.serviceProvider);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, amount, description, transactionDate, account, asset, serviceProvider);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public Long getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Long transactionDate) {
        this.transactionDate = transactionDate;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }
}
*/
