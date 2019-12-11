package com.ibm.cfenv.spring.boot.appid;


import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.util.ResourceUtils;

import io.pivotal.cfenv.test.AbstractCfEnvTests;
import mockit.Mock;
import mockit.MockUp;

public class AppIDTest extends AbstractCfEnvTests {
        
    @Test
    public void mongoServiceCreation() {
        mockVcapServicesWithNames("./vcap-services.json");
        Environment environment = getEnvironment();
        assertThat(environment.getProperty("spring.security.oauth2.client.registration.appid.clientId")).isEqualTo("appid_clientId");
        assertThat(environment.getProperty("spring.security.oauth2.client.registration.appid.clientSecret")).isEqualTo("appid_secret");
        assertThat(environment.getProperty("spring.security.oauth2.client.registration.appid.issuerUri")).isEqualTo("appid_oauthServerUrl");
    }

    @SpringBootApplication
    static class TestApp {
    }
    
    private void mockVcapServicesWithNames(String fileName) {
        String fileContents;
        try {
            File file = ResourceUtils.getFile("classpath:" + fileName);
            fileContents = new String(Files.readAllBytes(file.toPath()));
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }

        Map<String, String> env = System.getenv();
        new MockUp<System>() {
            @Mock
            public String getenv(String name) {
                if (name.equalsIgnoreCase("VCAP_SERVICES")) {
                    return fileContents;
                }
                if (name.equalsIgnoreCase("VCAP_APPLICATION")) {
                    return "{\"instance_id\":\"123\"}";
                }
                return env.get(name);
            }
            @mockit.Mock
            public Map<String, String> getenv() {
                Map<String,String> finalMap = new HashMap<>();
                finalMap.putAll(env);
                finalMap.put("VCAP_SERVICES", fileContents);
                return finalMap;
            }
        };

    }
}
