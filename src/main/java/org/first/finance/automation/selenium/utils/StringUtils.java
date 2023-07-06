package org.first.finance.automation.selenium.utils;

public class StringUtils {
    private static final String SPACE = " ";
    private static final String EMPTY_STRING = "";
    private static final int MIN_PARTS_FOR_BUILD = 2;
    public static String buildStringWithSpaces(String... parts) {
        if (parts.length < MIN_PARTS_FOR_BUILD) {
            return EMPTY_STRING;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            sb.append(parts[i]);
            sb.append(SPACE);
        }
        sb.append(parts[parts.length-1]);
        return sb.toString();
    }
}
