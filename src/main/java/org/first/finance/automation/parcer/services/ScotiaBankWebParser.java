package org.first.finance.automation.parcer.services;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public class ScotiaBankWebParser {
    private static final String AUTHENTICATION_BNS = "https://www.scotiaonline.scotiabank.com/online/authentication/authentication.bns";

    private static final String MAIN_TAB = "https://www.scotiaonline.scotiabank.com/online/views/accounts/summary/summaryStandard.bns?SBL=all&convid=286845";

    public static void main(String[] args) throws Exception {
        getPageContent(AUTHENTICATION_BNS, null);
       /* Map<String, String> cookies= new HashMap<>();
        Connection authenticationConnection = getConnection(cookies, AUTHENTICATION_BNS);
        Connection.Response authenticationResponse = authenticationConnection.execute();
        cookies.putAll(authenticationResponse.cookies());
        Connection onlineConnection = getConnection(cookies, authenticationResponse.url().toString());
        Connection.Response onlineResponse = onlineConnection.execute();
        cookies.putAll(onlineResponse.cookies());*/

    }

    private static Connection getConnection(Map<String, String> cookies, String url) {
        Connection connect = Jsoup.connect(url);
        connect.cookies(cookies);
        connect.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        connect.header("Accept-Encoding", "gzip, deflate, br");
        connect.header("Accept-Language", "en-US,en;q=0.9");
        connect.header("Connection", "keep-alive");
        connect.header("DNT", "1");
        //connect.header("Host", "www.scotiaonline.scotiabank.com");
        connect.header("sec-ch-ua", "\"Not_A Brand\";v=\"99\", \"Google Chrome\";v=\"109\", \"Chromium\";v=\"109\"");
        connect.header("sec-ch-ua-mobile", "?0");
        connect.header("sec-ch-ua-platform", "\"Windows\"");
        connect.header("Sec-Fetch-Dest", "document");
        connect.header("Sec-Fetch-Mode", "navigate");
        connect.header("Sec-Fetch-Site", "none");
        connect.header("Sec-Fetch-User", "?1");
        connect.header("Upgrade-Insecure-Requests", "1");
        connect.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36");
        return connect;
    }

    private static Map<String, String> parseCookies(String cookies) {
        String[] cookie = cookies.split("; ");
        HashMap<String, String> result = new HashMap<>();
        for (String c : cookie) {
            String[] pair = c.split("=", 2);
            result.put(pair[0], pair[1]);
        }
        return result;
    }

    private static String getPageContent(String url, Map<String, String> cookies) throws Exception {

        URL obj = new URL(url);
        HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();

        // default is GET
        conn.setRequestMethod("GET");

        conn.setUseCaches(false);
        conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("DNT", "1");

        conn.setRequestProperty("sec-ch-ua", "\"Not_A Brand\";v=\"99\", \"Google Chrome\";v=\"109\", \"Chromium\";v=\"109\"");
        conn.setRequestProperty("sec-ch-ua-mobile", "?0");
        conn.setRequestProperty("sec-ch-ua-platform", "\"Windows\"");
        conn.setRequestProperty("Sec-Fetch-Dest", "document");
        conn.setRequestProperty("Sec-Fetch-Mode", "navigate");
        conn.setRequestProperty("Sec-Fetch-Site", "none");
        conn.setRequestProperty("Sec-Fetch-User", "?1");
        conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36");

        if (cookies != null) {
            /*for (String cookie : cookies) {
                conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
            }*/
        }
        int responseCode = conn.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in =
                new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Get the response cookies
        //setCookies(conn.getHeaderFields().get("Set-Cookie"));
        System.out.println(response);

        return response.toString();

    }
}
