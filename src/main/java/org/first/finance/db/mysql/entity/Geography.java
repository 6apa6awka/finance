package org.first.finance.db.mysql.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Geography {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String parentGeography;

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

    public String getParentGeography() {
        return parentGeography;
    }

    public void setParentGeography(String parentGeography) {
        this.parentGeography = parentGeography;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Geography geography = (Geography) o;
        return id.equals(geography.id) && name.equals(geography.name) && Objects.equals(parentGeography, geography.parentGeography);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, parentGeography);
    }
}
