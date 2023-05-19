/*
package org.first.finance.db.mongo.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import javax.persistence.Id;
import java.util.List;
import java.util.Objects;

@Document
public class ServiceProvider {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private String description;

    private String category;

    @DocumentReference(lazy = true)
    private List<ServiceProviderAlias> serviceProviderAliases;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<ServiceProviderAlias> getServiceProviderAliases() {
        return serviceProviderAliases;
    }

    public void setServiceProviderAliases(List<ServiceProviderAlias> serviceProviderAliases) {
        this.serviceProviderAliases = serviceProviderAliases;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceProvider that = (ServiceProvider) o;
        return name.equals(that.name) && Objects.equals(description, that.description) && Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, category);
    }
}
*/
