package com.ibm.boot.cfenvprocessors.cloudant;

import java.util.Map;
import java.util.logging.Logger;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.core.CfService;
import io.pivotal.cfenv.spring.boot.CfEnvProcessor;
import io.pivotal.cfenv.spring.boot.CfEnvProcessorProperties;

public class CloudantCfEnvProcessor implements CfEnvProcessor {

    private static final Logger LOG = Logger.getLogger(CloudantCfEnvProcessor.class.getName());

    public CloudantCfEnvProcessor() {
        LOG.info("CloudantCfEnvProcessor built");
    }

    @Override
    public boolean accept(CfService service) {
        boolean match = service.existsByLabelStartsWith("cloudantNoSQLDB");
        LOG.info("Match [" + match + "] to service " + service.toString());
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