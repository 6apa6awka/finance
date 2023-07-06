package org.first.finance.automation.selenium.core;

import org.openqa.selenium.By;
import org.springframework.core.env.PropertySource;

public enum SeleniumPath {
    TRANSACTION_FILTER_MONTH("transactions.filter.month", SeleniumPath.SeleniumPathType.ID),
    TRANSACTION_FILTER_SEARCH("transactions.filter.search", SeleniumPath.SeleniumPathType.XPATH),
    TRANSACTIONS_LIST("transactions.list", SeleniumPath.SeleniumPathType.XPATH),
    TRANSACTIONS_LIST_FIELDS("transactions.list.fields", SeleniumPath.SeleniumPathType.TAG_NAME),
    TRANSACTIONS_LIST_FIELD_DATE("transactions.list.field.date", SeleniumPath.SeleniumPathType.TAG_NAME),
    TRANSACTIONS_TOTAL_CREDIT("transactions.total.credit", SeleniumPath.SeleniumPathType.XPATH),
    TRANSACTIONS_TOTAL_DEBIT("transactions.total.debit", SeleniumPath.SeleniumPathType.XPATH),
    PERIODS("periods", SeleniumPath.SeleniumPathType.XPATH),
    PERIODS_DURATION("periods.duration", SeleniumPathType.CLASS_NAME),
    PERIODS_INTEREST("periods.interest", SeleniumPathType.CLASS_NAME),
    PERIODS_PROGRESS("periods.progress", SeleniumPathType.CLASS_NAME),
    PERIODS_PROGRESS_BUTTON("periods.progress.button", SeleniumPathType.CLASS_NAME),
    PERIODS_GOAL("periods.goal", SeleniumPathType.CLASS_NAME),
    PERIODS_BALANCE("periods.balance", SeleniumPathType.CLASS_NAME),
    PERIODS_TRANSACTIONS("periods.transactions", SeleniumPathType.XPATH),
    PERIODS_TRANSACTIONS_CONTAINER("periods.transactions.container", SeleniumPathType.CLASS_NAME),
    PERIODS_TRANSACTIONS_PERIOD("periods.transactions.period", SeleniumPathType.ID);

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
            case CLASS_NAME -> {
                return By.className(path);
            }
            default -> {
                return null;
            }
        }
    }

    private enum SeleniumPathType {
        XPATH, ID, TAG_NAME, CLASS_NAME
    }
}
