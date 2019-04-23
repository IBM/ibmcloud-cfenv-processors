package org.terrence.testapp.envprocessor;

import java.util.Map;

import org.terrence.testapp.credentials.WatsonDiscoveryCfCredentials;

import io.pivotal.cfenv.core.CfCredentials;
// import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.core.CfService;
import io.pivotal.cfenv.spring.boot.CfEnvProcessor;
import io.pivotal.cfenv.spring.boot.CfEnvProcessorProperties;

public class CloudantCfEnvProcessor implements CfEnvProcessor {

    public CloudantCfEnvProcessor() {
        System.out.println("WatsonDiscoveryCfEnvProcessor built");
    }

    @Override
    public boolean accept(CfService service) {
        boolean match = service.existsByLabelStartsWith("discovery");
        System.out.println("Match [" + match + "] to service " + service.toString());
        return match;
    }

    @Override
    public CfEnvProcessorProperties getProperties() {
        return CfEnvProcessorProperties.builder().propertyPrefixes("discovery").serviceName("Discovery").build();
    }

    @Override
    public void process(CfCredentials cfCredentials, Map<String, Object> properties) {
        properties.put("watson.discovery.url", cfCredentials.getUri("http"));
        properties.put("watson.discovery.username", cfCredentials.getUsername());
        properties.put("watson.discovery.password", cfCredentials.getPassword());
        if (cfCredentials instanceof WatsonDiscoveryCfCredentials) {
            WatsonDiscoveryCfCredentials watsonDiscoveryCreds = (WatsonDiscoveryCfCredentials) cfCredentials;
            properties.put("watson.discovery.version", watsonDiscoveryCreds.getVersion());
        }

    }
}