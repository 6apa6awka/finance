package org.first.finance.automation.parcer.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ScotiaCreditAccountSeleniumParserTest {
    @Autowired
    private ScotiaCreditAccountSeleniumParser scotiaCreditAccountSeleniumParser;

    @Test
    void parseFromDescription() {
        System.out.println(scotiaCreditAccountSeleniumParser.resolveServiceProvider("HOCHE GLACE MONTREAL QC", null));
    }
}