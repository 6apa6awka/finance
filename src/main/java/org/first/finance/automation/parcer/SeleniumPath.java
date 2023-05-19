package org.first.finance.automation.parcer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

public enum SeleniumPath {
    TRANSACTION_FILTER_MONTH("transactions.filter.month"),
    TRANSACTION_FILTER_SEARCH("transactions.filter.search");

    private final String propertyName;

    SeleniumPath(String propertyName) {
        this.propertyName = propertyName;
    }

    public String get(PropertySource<?> propertySource) {
        return (String) propertySource.getProperty(propertyName);
    }
}
