package org.first.finance.automation.selenium.core;

public enum UITransactionField {
    CREDIT, DEBIT, DESCRIPTION, CATEGORY;

    public int findPositionIn(UITransactionField[] uiTransactionFieldsInOrder) {
        int order = getOrder(uiTransactionFieldsInOrder);
        if (order == -1) {
            throw new IllegalArgumentException("Check fields order is correct");
        }
        return order;
    }

    private int getOrder(UITransactionField[] fieldsOrder) {
        for (int i = 0; i < fieldsOrder.length; i++) {
            if (this == fieldsOrder[i]) {
                return i;
            }
        }
        return -1;
    }
}
