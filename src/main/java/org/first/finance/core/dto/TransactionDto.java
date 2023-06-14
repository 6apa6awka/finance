package org.first.finance.core.dto;

import org.first.finance.db.mysql.entity.TransactionType;

import java.math.BigDecimal;

public class TransactionDto {
    private Long accountId;
    private Long serviceProviderId;
    private long transactionDate;
    private String description;
    private String category;
    private BigDecimal amount;
    private TransactionType type;

    public TransactionDto() {
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getServiceProviderId() {
        return serviceProviderId;
    }

    public void setServiceProviderId(Long serviceProviderId) {
        this.serviceProviderId = serviceProviderId;
    }

    public void setTransactionDate(long transactionDate) {
        this.transactionDate = transactionDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getAccountId() {
        return accountId;
    }

    public long getTransactionDate() {
        return transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }
}
