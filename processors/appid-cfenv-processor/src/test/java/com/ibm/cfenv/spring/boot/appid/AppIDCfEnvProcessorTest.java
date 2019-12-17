package com.ibm.cfenv.spring.boot.appid;

import io.pivotal.cfenv.core.CfService;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.cfenv.spring.boot.appid.AppIDCfEnvProcessor;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppIDCfEnvProcessorTest {
    
    static {
        System.setProperty("CFENV_USER_PROVIDED_SERVICE_SEARCH_DISABLE", "false");
    }


    AppIDCfEnvProcessor appIDCfEnvProcessor = new AppIDCfEnvProcessor();
    
    public CfService initCFService() throws IOException {
        URL url = AppIDCfEnvProcessor.class.getClassLoader().getResource("./vcap-services.json");
        Map<String, Object> serviceData = (Map<String, Object>) ((List) new ObjectMapper()
                .readValue(url, Map.class)
                .get("AppID"))
                .get(0);
        return new CfService(serviceData);
    }
    
    public CfService initUserProvidedService() throws IOException {
        URL url = AppIDCfEnvProcessor.class.getClassLoader().getResource("./vcap-user-provided-services.json");
        Map<String, Object> serviceData = (Map<String, Object>) ((List) new ObjectMapper()
                .readValue(url, Map.class)
                .get("user-provided"))
                .get(0);
        return new CfService(serviceData);
    }
    
    @Test
    public void validCFService_accept_serviceAccepted() throws IOException {
        assertThat(appIDCfEnvProcessor.accept(initCFService())).isTrue();
    }

    @Test
    public void validCFService_propertiesProcessed_CorrectAppIDSetup() throws IOException {
        Map<String, Object> properties = new HashMap<>();
        appIDCfEnvProcessor.process(initCFService().getCredentials(), properties);
        assertThat(properties.size()).isEqualTo(3);
        assertThat(properties.get("spring.security.oauth2.client.registration.appid.clientId")).isEqualTo("appid_clientId");
        assertThat(properties.get("spring.security.oauth2.client.registration.appid.clientSecret")).isEqualTo("appid_secret");
        assertThat(properties.get("spring.security.oauth2.client.registration.appid.issuerUri")).isEqualTo("appid_oauthServerUrl");
    }
    
    @Test
    public void validUserProvidedService_propertiesProcessed_CorrectAppIDSetup() throws IOException {
        Map<String, Object> properties = new HashMap<>();
        System.out.println(initUserProvidedService().getCredentials().getMap());
        appIDCfEnvProcessor.process(initUserProvidedService().getCredentials(), properties);
        assertThat(properties.size()).isEqualTo(3);
        assertThat(properties.get("spring.security.oauth2.client.registration.appid.clientId")).isEqualTo("user_provided_appid_clientId");
        assertThat(properties.get("spring.security.oauth2.client.registration.appid.clientSecret")).isEqualTo("user_provided_appid_secret");
        assertThat(properties.get("spring.security.oauth2.client.registration.appid.issuerUri")).isEqualTo("user_provided_appid_oauthServerUrl");
    }

    @Test
    public void validCFService_getProperties_correctServiceNameAndPrefixes() {
        assertThat(appIDCfEnvProcessor.getProperties().getServiceName()).isEqualTo("AppID");
        assertThat(appIDCfEnvProcessor.getProperties().getPropertyPrefixes()).isEqualTo("spring.security.oauth2.client.registration.appid");
    }
}
