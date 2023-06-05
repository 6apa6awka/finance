package org.first.finance;

import org.first.finance.automation.BalanceAdvisor;
import org.first.finance.automation.ServiceProviderUpdater;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FinanceApplicationTests {

	private ServiceProviderUpdater serviceProviderUpdater;
	private BalanceAdvisor balanceAdvisor;

	@Test
	void startServiceProviderUpdate() {
		serviceProviderUpdater.start();
	}

	@Test
	void startBalanceAdvisor() {
		balanceAdvisor.start();
	}

	@Autowired
	public void setServiceProviderUpdater(ServiceProviderUpdater serviceProviderUpdater) {
		this.serviceProviderUpdater = serviceProviderUpdater;
	}

	@Autowired
	public void setBalanceAdvisor(BalanceAdvisor balanceAdvisor) {
		this.balanceAdvisor = balanceAdvisor;
	}
}
