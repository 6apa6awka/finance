package org.first.finance.sheets.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Service
public class GoogleSheetsProcessingService {
    private ResourceLoader resourceLoader;

    public ArrayList<ArrayList<String>> get() {
        NetHttpTransport googleNetHttpTransport;
        Credential credential;
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        try {
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(resourceLoader.getResource("classpath:sheets\\credentials.json").getInputStream()));
            googleNetHttpTransport = GoogleNetHttpTransport.newTrustedTransport();
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    googleNetHttpTransport, jsonFactory, clientSecrets, Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY))
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("sheets\\tokens")))
                    .setAccessType("offline")
                    .build();
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        Sheets sheets = new Sheets.Builder(googleNetHttpTransport, jsonFactory, credential).setApplicationName("Finance").build();
        try {
            return sheets.spreadsheets().values()
                    .get("1MxvBbQ3uPFTcJUynn3REcbKQqv6fw9xNoLJt9xZaIgU", "Sheet1!A1:C8")
                    .execute()
                    .values()
                    .stream()
                    .filter(a -> a instanceof ArrayList)
                    .map(a -> (ArrayList<ArrayList<String>>) a)
                    .findFirst()
                    .orElse(new ArrayList<>());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
