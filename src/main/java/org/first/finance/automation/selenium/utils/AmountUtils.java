package org.first.finance.automation.selenium.utils;

import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;

public class AmountUtils {
    private static final String AMOUNT_SIGNS_TO_REMOVE = "[$ ,]";

    public static BigDecimal parseAmountOrZero(String amount) {
        return ObjectUtils.isEmpty(amount) ? BigDecimal.ZERO : AmountUtils.parseAmount(amount);
    }

    public static BigDecimal parseAmount(String amount) {
        return new BigDecimal(amount.replaceAll(AMOUNT_SIGNS_TO_REMOVE, ""));
    }
}
