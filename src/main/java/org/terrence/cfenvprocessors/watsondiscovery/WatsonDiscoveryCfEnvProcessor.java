package org.terrence.cfenvprocessors.watsondiscovery;

import java.util.Map;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.core.CfService;
import io.pivotal.cfenv.spring.boot.CfEnvProcessor;
import io.pivotal.cfenv.spring.boot.CfEnvProcessorProperties;

public class WatsonDiscoveryCfEnvProcessor implements CfEnvProcessor {

    public WatsonDiscoveryCfEnvProcessor() {
        System.out.println("WatsonDiscoveryCfEnvProcessor built");
    }

    @Override
    public boolean accept(CfService service) {
        boolean match = service.existsByLabelStartsWith("discovery");
        return match;
    }

    @Override
    public CfEnvProcessorProperties getProperties() {
        return CfEnvProcessorProperties.builder().propertyPrefixes("discovery").serviceName("Discovery").build();
    }

    @Override
    public void process(CfCredentials cfCredentials, Map<String, Object> properties) {
        // set watsonVersion to date of the released watson spring boot starter
        // version 0.3.0 was released on 2019-05-07
        String watsonVersion = "2018-05-07";

        properties.put("watson.discovery.url", cfCredentials.getUri("http"));
        properties.put("watson.discovery.iam-api-key", cfCredentials.getString("apikey"));
        properties.put("watson.discovery.versionDate", watsonVersion);

    }
}