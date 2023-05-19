package org.first.finance.rest.controller;

import org.first.finance.automation.BalanceAdvisor;
//import org.first.finance.automation.parcer.services.ScotiaBankSelenium;

import org.first.finance.db.mysql.entity.Transaction;
import org.first.finance.sheets.service.GoogleSheetsProcessingService;
import org.first.finance.ui.AutomaticServiceProviderDefiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("job")
public class JobController {
    //private ScotiaBankSelenium scotiaBankSelenium;
    private BalanceAdvisor balanceAdvisor;
    //private GoogleSheetsProcessingService googleSheetsProcessingService;
    private AutomaticServiceProviderDefiner automaticServiceProviderDefiner;
    /*@GetMapping(value = "/scotiaParser", produces = "application/json")
    public ResponseEntity<String> runScotiaParserJob() {
        CompletableFuture.runAsync(() -> scotiaBankSelenium.start());
        return new ResponseEntity<>(HttpStatus.OK);
    }*/

    @GetMapping(value = "/balanceAdvisor", produces = "application/json")
    public ResponseEntity<String> runBalanceAdvisor() {
        CompletableFuture.runAsync(() -> balanceAdvisor.start());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/serviceProviderAutomation", produces = "application/json")
    public ResponseEntity<Transaction> runServiceProviderAutomationJob() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccessControlAllowOrigin("*");
        return new ResponseEntity<>(automaticServiceProviderDefiner.start(), httpHeaders, HttpStatus.OK);
    }

    /*@GetMapping(value = "/categoriesRefresh", produces = "application/json")
    public ResponseEntity<String> runCategoriesAutomationJob() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccessControlAllowOrigin("*");
        CompletableFuture.runAsync(() -> googleSheetsProcessingService.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Autowired
    public void setScotiaBankSelenium(ScotiaBankSelenium scotiaBankSelenium) {
        this.scotiaBankSelenium = scotiaBankSelenium;
    }*/

    @Autowired
    public void setAutomaticServiceProviderDefiner(AutomaticServiceProviderDefiner automaticServiceProviderDefiner) {
        this.automaticServiceProviderDefiner = automaticServiceProviderDefiner;
    }

    /*@Autowired
    public void setGoogleSheetsProcessingService(GoogleSheetsProcessingService googleSheetsProcessingService) {
        this.googleSheetsProcessingService = googleSheetsProcessingService;
    }*/

    @Autowired
    public void setBalanceAdvisor(BalanceAdvisor balanceAdvisor) {
        this.balanceAdvisor = balanceAdvisor;
    }
}
