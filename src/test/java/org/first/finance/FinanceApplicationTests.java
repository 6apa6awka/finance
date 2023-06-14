package org.first.finance;

import org.first.finance.automation.BalanceAdvisor;
import org.first.finance.automation.ServiceProviderUpdater;
import org.first.finance.automation.selenium.services.ScotiaBankSelenium;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FinanceApplicationTests {

	private ServiceProviderUpdater serviceProviderUpdater;
	private BalanceAdvisor balanceAdvisor;
	private ScotiaBankSelenium scotiaBankSelenium;

	@Test
	void startServiceProviderUpdate() {
		serviceProviderUpdater.start();
	}

	@Test
	void startBalanceAdvisor() {
		balanceAdvisor.start();
	}

	@Test
	void startScotiaBankSelenium() {
		scotiaBankSelenium.start();
	}


	@Autowired
	public void setServiceProviderUpdater(ServiceProviderUpdater serviceProviderUpdater) {
		this.serviceProviderUpdater = serviceProviderUpdater;
	}

	@Autowired
	public void setBalanceAdvisor(BalanceAdvisor balanceAdvisor) {
		this.balanceAdvisor = balanceAdvisor;
	}

	@Autowired
	public void setScotiaBankSelenium(ScotiaBankSelenium scotiaBankSelenium) {
		this.scotiaBankSelenium = scotiaBankSelenium;
	}
}
