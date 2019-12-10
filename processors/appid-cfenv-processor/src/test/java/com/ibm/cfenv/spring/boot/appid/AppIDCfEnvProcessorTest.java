package com.ibm.cfenv.spring.boot.appid;

import io.pivotal.cfenv.core.CfService;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.cfenv.spring.boot.appid.AppIDCfEnvProcessor;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppIDCfEnvProcessorTest {

    AppIDCfEnvProcessor appIDCfEnvProcessor = new AppIDCfEnvProcessor();
    private CfService service;
    
    @BeforeAll
    public void initService() throws IOException {
        URL url = AppIDCfEnvProcessor.class.getClassLoader().getResource("./vcap-services.json");
        Map<String, Object> serviceData = (Map<String, Object>) ((List) new ObjectMapper()
                .readValue(url, Map.class)
                .get("AppID"))
                .get(0);
        service = new CfService(serviceData);
    }
    
    @Test
    public void accept() {
        assertThat(appIDCfEnvProcessor.accept(service)).isTrue();
    }

    @Test
    public void process() {
        Map<String, Object> properties = new HashMap<>();
        appIDCfEnvProcessor.process(service.getCredentials(), properties);
        assertThat(properties.size()).isEqualTo(3);
        assertThat(properties.get("spring.security.oauth2.client.registration.appid.clientId")).isEqualTo("appid_clientId");
        assertThat(properties.get("spring.security.oauth2.client.registration.appid.clientSecret")).isEqualTo("appid_secret");
        assertThat(properties.get("spring.security.oauth2.client.registration.appid.issuerUri")).isEqualTo("appid_discoveryEndpoint");
    }

    @Test
    public void getProperties() {
        assertThat(appIDCfEnvProcessor.getProperties().getServiceName()).isEqualTo("AppID");
        assertThat(appIDCfEnvProcessor.getProperties().getPropertyPrefixes()).isEqualTo("spring.security.oauth2.client.registration.appid");

    }
}