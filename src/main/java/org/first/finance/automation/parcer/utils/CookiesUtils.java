package org.first.finance.automation.parcer.utils;

import com.google.common.base.Strings;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class CookiesUtils {
    private final static String COOKIES_FILE_NAME = "src\\main\\resources\\cookies\\ChromeDriverCookies.txt";
    private final static String COOKIES_TIMESTAMP = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private final static String COOKIES_SESSION_EXPIRE = "Session";
    private final static String COOKIES_SCOTIABANK_DOMAIN = "scotiabank";
    private final static String COOKIES_END_LINE_SEPARATOR = "\r\n";
    private final static String COOKIES_SEPARATOR = "\t";
    private final static String COOKIES_CHROME_TRUE_SIGN = "✓";

    public static void restoreCookies(WebDriver driver) {
        try{
            DateFormat formatter = new SimpleDateFormat(COOKIES_TIMESTAMP);
            String content = Files.readString(Paths.get(COOKIES_FILE_NAME), StandardCharsets.UTF_8);
            String[] cookie = content.split(COOKIES_END_LINE_SEPARATOR);
            for (String c : cookie) {
                String[] cookieProperty = c.split(COOKIES_SEPARATOR);
                if (COOKIES_SESSION_EXPIRE.equals(cookieProperty[CookieProperty.EXPIRES.ordinal()])
                        || !cookieProperty[CookieProperty.DOMAIN.ordinal()].contains(COOKIES_SCOTIABANK_DOMAIN)) {
                    continue;
                }
                /*if (".scotiabank.com".equals(cookieProperty[CookieProperty.DOMAIN.ordinal()])) {
                    cookieProperty[CookieProperty.DOMAIN.ordinal()] = "https://scotiabank.com/";
                }*/
                driver.manage().addCookie(new Cookie(
                        cookieProperty[CookieProperty.NAME.ordinal()],
                        cookieProperty[CookieProperty.VALUE.ordinal()],
                        cookieProperty[CookieProperty.DOMAIN.ordinal()],
                        cookieProperty[CookieProperty.PATH.ordinal()],
                        "".equals(cookieProperty[CookieProperty.EXPIRES.ordinal()]) ? null : formatter.parse(cookieProperty[CookieProperty.EXPIRES.ordinal()]),
                        !Strings.isNullOrEmpty(cookieProperty[CookieProperty.SECURE.ordinal()]),
                        !Strings.isNullOrEmpty(cookieProperty[CookieProperty.HTTP_ONLY.ordinal()]),
                        cookieProperty[CookieProperty.SAME_SIZE.ordinal()]));
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void storeCookies(WebDriver driver) {
        StringBuilder stringBuilder = new StringBuilder();
        DateFormat formatter = new SimpleDateFormat(COOKIES_TIMESTAMP);
        for (Cookie cookie : driver.manage().getCookies()) {
            stringBuilder.append(cookie.getName()).append(COOKIES_SEPARATOR)
                    .append(cookie.getValue()).append(COOKIES_SEPARATOR)
                    .append(cookie.getDomain()).append(COOKIES_SEPARATOR)
                    .append(cookie.getPath()).append(COOKIES_SEPARATOR + COOKIES_SEPARATOR);
            if (cookie.getExpiry() != null) {
                stringBuilder.append(formatter.format(cookie.getExpiry()));
            }
            stringBuilder.append(COOKIES_SEPARATOR);
            if (cookie.isSecure()) {
                stringBuilder.append(COOKIES_CHROME_TRUE_SIGN);
            }
            stringBuilder.append(COOKIES_SEPARATOR);
            if (cookie.isHttpOnly()) {
                stringBuilder.append(COOKIES_CHROME_TRUE_SIGN);
            }
            stringBuilder.append(COOKIES_SEPARATOR).append(cookie.getSameSite()).append(COOKIES_END_LINE_SEPARATOR);
        }
        try {
            Files.writeString(Paths.get(COOKIES_FILE_NAME), stringBuilder.toString());
        } catch (IOException e) {
            System.out.println("Can't store cookies " + e);
        }
    }

    private enum CookieProperty {
        NAME, VALUE, DOMAIN, PATH, EXPIRES, SIZE, HTTP_ONLY, SECURE, SAME_SIZE
    }
}
