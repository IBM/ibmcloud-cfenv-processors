package com.ibm.cfenv.spring.boot.appid;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.core.CfService;
import io.pivotal.cfenv.spring.boot.CfEnvProcessor;
import io.pivotal.cfenv.spring.boot.CfEnvProcessorProperties;

import java.util.Map;
import java.util.logging.Logger;

public class AppIDCfEnvProcessor implements CfEnvProcessor {

    @Override
    public boolean accept(CfService service) {
        boolean match = service.existsByLabelStartsWith("AppID");
        return match;
    }

    @Override
    public CfEnvProcessorProperties getProperties() {
        return CfEnvProcessorProperties.builder()
                .propertyPrefixes("spring.security.oauth2.client.registration.appid")
                .serviceName("AppID")
                .build();
    }

    @Override
    public void process(CfCredentials cfCredentials, Map<String, Object> properties) {
        Map<String, Object> credentials = cfCredentials.getMap();
        properties.put("spring.security.oauth2.client.registration.appid.clientId", credentials.get("clientId"));
        properties.put("spring.security.oauth2.client.registration.appid.clientSecret", credentials.get("secret"));
        properties.put("spring.security.oauth2.client.registration.appid.issuerUri", credentials.get("discoveryEndpoint"));
    }
}