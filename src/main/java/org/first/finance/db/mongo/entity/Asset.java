package org.first.finance.db.mongo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Objects;

@Document
public class Asset {
    @Id
    private String id;

    @Indexed(unique = true)
    private String code;
    private String name;
    private String description;

    private BigDecimal cost;
    private BigDecimal amount;
    private BigDecimal quantity;
    private BigDecimal fixedIncome;
    private long endDate;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getFixedIncome() {
        return fixedIncome;
    }

    public void setFixedIncome(BigDecimal fixedIncome) {
        this.fixedIncome = fixedIncome;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asset asset = (Asset) o;
        return endDate == asset.endDate && code.equals(asset.code) && name.equals(asset.name) && Objects.equals(description, asset.description) && Objects.equals(cost, asset.cost) && Objects.equals(amount, asset.amount) && Objects.equals(fixedIncome, asset.fixedIncome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name, description, cost, amount, fixedIncome, endDate);
    }
}
