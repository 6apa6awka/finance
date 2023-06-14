package org.first.finance.automation.selenium.utils;

import java.util.Random;

public class CommonUtils {
    public static void sleep(long ms) {
        try {
            Thread.sleep(new Random().nextLong(1000) + ms == 0 ? 1000 : ms);
        } catch (InterruptedException e) {
            System.out.println("Interrupted during sleep " + e);
        }
    }

    public static void sleep() {
        sleep(0);
    }
}
