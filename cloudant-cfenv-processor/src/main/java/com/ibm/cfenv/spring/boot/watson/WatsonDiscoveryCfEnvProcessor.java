package com.ibm.cfenv.spring.boot.watson;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.core.CfService;
import io.pivotal.cfenv.spring.boot.CfEnvProcessor;
import io.pivotal.cfenv.spring.boot.CfEnvProcessorProperties;

import java.util.Map;
import java.util.logging.Logger;

public class WatsonDiscoveryCfEnvProcessor implements CfEnvProcessor {

    private static final Logger LOG = Logger.getLogger(WatsonDiscoveryCfEnvProcessor.class.getName());

    public WatsonDiscoveryCfEnvProcessor() {
        LOG.info("WatsonDiscoveryCfEnvProcessor built");
    }

    @Override
    public boolean accept(CfService service) {
        boolean match = service.existsByLabelStartsWith("discovery");
        LOG.info("Match [" + match + "] to service " + service.toString());
        return match;
    }

    @Override
    public CfEnvProcessorProperties getProperties() {
        return CfEnvProcessorProperties.builder().propertyPrefixes("discovery").serviceName("Discovery").build();
    }

    @Override
    public void process(CfCredentials cfCredentials, Map<String, Object> properties) {
        // set watsonVersion to date of the released watson spring boot starter
        // version 1.0.0 was released on 2019-05-07
        String watsonVersion = "2018-05-07";

        properties.put("watson.discovery.url", cfCredentials.getUri("http"));
        properties.put("watson.discovery.iam-api-key", cfCredentials.getString("apikey"));
        properties.put("watson.discovery.versionDate", watsonVersion);

    }
}