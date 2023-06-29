package org.first.finance.automation.selenium.utils;

import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private final static String DATE_PATTERN = "[MMM d, yyyy][MMM. d, yyyy][MMMM d, yyyy]";
    public static LocalDate toLocalDate(String date) {
        if (ObjectUtils.isEmpty(date)) {
            throw new IllegalArgumentException("Date can't be empty");
        }
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_PATTERN));
    }
}
