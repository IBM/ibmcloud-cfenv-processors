package com.ibm.cfenv.spring.boot.db2;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.core.CfService;
import io.pivotal.cfenv.spring.boot.CfEnvProcessor;
import io.pivotal.cfenv.spring.boot.CfEnvProcessorProperties;

import java.util.Map;
import java.util.logging.Logger;

public class DB2CfEnvProcessor implements CfEnvProcessor {

    private static final Logger LOG = Logger.getLogger(DB2CfEnvProcessor.class.getName());

    @Override
    public boolean accept(CfService service) {
        boolean match = service.existsByLabelStartsWith("dashDB For Transactions");
        LOG.info("Match [" + match + "] to service " + service.toString());
        return match;
    }

    @Override
    public CfEnvProcessorProperties getProperties() {
        return CfEnvProcessorProperties.builder().propertyPrefixes("spring.datasource").serviceName("DB2").build();
    }

    @Override
    public void process(CfCredentials cfCredentials, Map<String, Object> properties) {
        properties.put("spring.datasource.url", cfCredentials.getUri("http"));
        properties.put("spring.datasource.username", cfCredentials.getUsername());
        properties.put("spring.datasource.password", cfCredentials.getPassword());
    }
}