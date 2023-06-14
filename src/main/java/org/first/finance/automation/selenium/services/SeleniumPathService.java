package org.first.finance.automation.selenium.services;

import org.first.finance.automation.selenium.core.SeleniumPath;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Service;

@Service
public class SeleniumPathService {
    private static final String PROPERTY_FILE_NAME_TEMPLATE = "class path resource [selenium/path/%s.properties]";
    private Environment environment;

    public By getPath(SeleniumPath seleniumPath, String fileName) {
        PropertySource<?> propertySource = ((ConfigurableEnvironment) environment).getPropertySources()
                .get(String.format(PROPERTY_FILE_NAME_TEMPLATE, fileName));
        if (propertySource == null) {
            throw new IllegalArgumentException("property file name is incorrect");
        }
        return seleniumPath.get(propertySource);
    }

    @Autowired
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
