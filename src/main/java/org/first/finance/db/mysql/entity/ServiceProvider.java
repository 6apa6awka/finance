package org.first.finance.db.mysql.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class ServiceProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String name;

    private String details;

    private String category;

    private boolean isApproved;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String description) {
        this.details = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceProvider that = (ServiceProvider) o;
        return name.equals(that.name) && Objects.equals(details, that.details)
                && Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, details, category);
    }

    @Override
    public String toString() {
        return "ServiceProvider{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + details + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
