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
import com.google.api.services.sheets.v4.model.ValueRange;
import org.first.finance.sheets.core.GoogleSheetsDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleSheetsProcessingService {
    private final Sheets sheets;

    @Autowired
    public GoogleSheetsProcessingService(ResourceLoader resourceLoader) {
        NetHttpTransport googleNetHttpTransport;
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        try {
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(resourceLoader.getResource("classpath:sheets\\credentials.json").getInputStream()));
            googleNetHttpTransport = GoogleNetHttpTransport.newTrustedTransport();
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    googleNetHttpTransport, jsonFactory, clientSecrets, SheetsScopes.all())
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("sheets\\tokens")))
                    .setAccessType("offline")
                    .build();
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
            sheets = new Sheets.Builder(googleNetHttpTransport, jsonFactory, credential).setApplicationName("Finance").build();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<ArrayList<String>> read(GoogleSheetsDocument document) {
        if (sheets == null) {
            return null;
        }

        try {
            return sheets.spreadsheets().values()
                    .get(document.getSheetId(), document.getSheetRange())
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

    public void write(GoogleSheetsDocument document, List<List<Object>> values) {
        ValueRange valueRange = new ValueRange();
        valueRange.setValues(values);
        valueRange.setRange(document.getSheetRange());
        try {
            sheets.spreadsheets()
                    .values()
                    .update(document.getSheetId(), document.getSheetRange(), valueRange)
                    .setValueInputOption("RAW").execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
