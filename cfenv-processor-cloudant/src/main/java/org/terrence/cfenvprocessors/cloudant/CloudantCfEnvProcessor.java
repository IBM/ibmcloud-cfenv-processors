package org.terrence.cfenvprocessors.cloudant;

import java.util.Map;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.core.CfService;
import io.pivotal.cfenv.spring.boot.CfEnvProcessor;
import io.pivotal.cfenv.spring.boot.CfEnvProcessorProperties;

public class CloudantCfEnvProcessor implements CfEnvProcessor {

    public CloudantCfEnvProcessor() {
        System.out.println("CloudantCfEnvProcessor built");
    }

    @Override
    public boolean accept(CfService service) {
        boolean match = service.existsByLabelStartsWith("cloudantNoSQLDB");
        System.out.println("Match [" + match + "] to service " + service.toString());
        return match;
    }

    @Override
    public CfEnvProcessorProperties getProperties() {
        return CfEnvProcessorProperties.builder().propertyPrefixes("cloudant").serviceName("Cloudant").build();
    }

    @Override
    public void process(CfCredentials cfCredentials, Map<String, Object> properties) {
        properties.put("cloudant.url", cfCredentials.getUri("http"));
        properties.put("cloudant.username", cfCredentials.getUsername());
        properties.put("cloudant.password", cfCredentials.getPassword());
    }
}