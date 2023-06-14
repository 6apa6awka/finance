package org.first.finance.automation.selenium.utils;

import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private final static String DATE_PATTERN = "MMM d, yyyy";
    private final static String DATE_PATTERN_DOT_AFTER_MONTH = "MMM. d, yyyy";
    private final static String DOT = ".";
    public static LocalDate toLocalDate(String date) {
        if (ObjectUtils.isEmpty(date)) {
            throw new IllegalArgumentException("Date can't be empty");
        }
        String pattern;
        if (date.contains(DOT)) {
            pattern = DATE_PATTERN_DOT_AFTER_MONTH;
        } else {
            pattern = DATE_PATTERN;
        }
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern));
    }
}
