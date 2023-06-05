package org.first.finance.sheets.core;

public enum GoogleSheetsDocument {
    SPENDING_CATEGORIES("1MxvBbQ3uPFTcJUynn3REcbKQqv6fw9xNoLJt9xZaIgU", "Sheet1!A1:C8"),
    SERVICE_PROVIDERS("12fbufLD5tKxowf-CfVrakK0m2a4kv0ct7LQtb7PzMzc", "Sheet1!A2:D101");

    private final String sheetId;
    private final String sheetRange;

    GoogleSheetsDocument(String sheetId, String sheetRange) {
        this.sheetId = sheetId;
        this.sheetRange = sheetRange;
    }

    public String getSheetId() {
        return sheetId;
    }

    public String getSheetRange() {
        return sheetRange;
    }
}
