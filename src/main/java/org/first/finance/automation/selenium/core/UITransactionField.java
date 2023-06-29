package org.first.finance.automation.selenium.core;

public enum UITransactionField {
    CREDIT, DEBIT, DESCRIPTION, CATEGORY, DATE;

    public int findPositionIn(UITransactionField[] uiTransactionFieldsInOrder) {
        return getOrder(uiTransactionFieldsInOrder);
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
