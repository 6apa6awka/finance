package org.first.finance.automation.selenium.core;

import org.openqa.selenium.By;
import org.springframework.core.env.PropertySource;

public enum SeleniumPath {
    TRANSACTION_FILTER_MONTH("transactions.filter.month", SeleniumPath.SeleniumPathType.ID),
    TRANSACTION_FILTER_SEARCH("transactions.filter.search", SeleniumPath.SeleniumPathType.XPATH),
    TRANSACTIONS_LIST("transactions.list", SeleniumPath.SeleniumPathType.XPATH),
    TRANSACTIONS_LIST_FIELDS("transactions.list.fields", SeleniumPath.SeleniumPathType.TAG_NAME),
    TRANSACTIONS_LIST_FIELD_DATE("transactions.list.field.date", SeleniumPath.SeleniumPathType.TAG_NAME);

    private final String propertyName;
    private final SeleniumPathType type;

    SeleniumPath(String propertyName, SeleniumPathType type) {
        this.propertyName = propertyName;
        this.type = type;
    }

    public By get(PropertySource<?> propertySource) {
        String path = (String) propertySource.getProperty(propertyName);
        if (path == null) {
            throw new IllegalArgumentException("path name is incorrect");
        }

        switch (type) {
            case ID -> {
                return By.id(path);
            }
            case XPATH -> {
                return By.xpath(path);
            }
            case TAG_NAME -> {
                return By.tagName(path);
            }
            default -> {
                return null;
            }
        }
    }

    private enum SeleniumPathType {
        XPATH, ID, TAG_NAME
    }
}
