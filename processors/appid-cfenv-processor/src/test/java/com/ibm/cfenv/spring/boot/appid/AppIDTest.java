package com.ibm.cfenv.spring.boot.appid;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.env.Environment;

import io.pivotal.cfenv.test.AbstractCfEnvTests;

public class AppIDTest extends AbstractCfEnvTests {
        
    @Test
    public void vcapServicesSetAsEnvVarInPom_appIsLoaded_requiredAppIDPropertiesSetCorrectly() {
        Environment environment = getEnvironment();
        assertThat(environment.getProperty("spring.security.oauth2.client.registration.appid.clientId")).isEqualTo("appid_clientId");
        assertThat(environment.getProperty("spring.security.oauth2.client.registration.appid.clientSecret")).isEqualTo("appid_secret");
        assertThat(environment.getProperty("spring.security.oauth2.client.registration.appid.issuerUri")).isEqualTo("appid_oauthServerUrl");
    }
    
    public Environment getEnvironment() {
        return new SpringApplicationBuilder(TestApp.class).web(WebApplicationType.NONE)
                .bannerMode(Banner.Mode.OFF)
                .run()
                .getEnvironment();
    }
    
    @SpringBootApplication
    static class TestApp {
    }
}

