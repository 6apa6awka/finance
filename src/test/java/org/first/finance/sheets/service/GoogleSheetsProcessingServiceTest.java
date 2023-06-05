package org.first.finance.sheets.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.first.finance.sheets.core.GoogleSheetsDocument.SERVICE_PROVIDERS;

@SpringBootTest
class GoogleSheetsProcessingServiceTest {
    @Autowired
    private GoogleSheetsProcessingService googleSheetsProcessingService;

    @Test
    void parseFromDescription() {
        googleSheetsProcessingService.read(SERVICE_PROVIDERS).forEach(System.out::println);
    }

}