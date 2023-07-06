package org.first.finance.automation;

import org.first.finance.automation.selenium.services.ScotiaBankSelenium;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountComparisonService {
    private ScotiaBankSelenium scotiaBankSelenium;

    public void compareTransactionsForAccount() {
        scotiaBankSelenium.getAllTransactionsForAccount("Ultimate Package - 334310260983");
    }

    @Autowired
    public void setScotiaBankSelenium(ScotiaBankSelenium scotiaBankSelenium) {
        this.scotiaBankSelenium = scotiaBankSelenium;
    }
}
