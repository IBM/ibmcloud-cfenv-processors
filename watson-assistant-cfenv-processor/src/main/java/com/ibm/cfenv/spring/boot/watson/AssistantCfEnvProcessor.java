package com.ibm.cfenv.spring.boot.watson;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.core.CfService;
import io.pivotal.cfenv.spring.boot.CfEnvProcessor;
import io.pivotal.cfenv.spring.boot.CfEnvProcessorProperties;

import java.util.Map;
import java.util.logging.Logger;

public class AssistantCfEnvProcessor implements CfEnvProcessor {

    private static final Logger LOG = Logger.getLogger(AssistantCfEnvProcessor.class.getName());

    public AssistantCfEnvProcessor() {
        LOG.info("WatsonAssistantCfEnvProcessor built");
    }

    @Override
    public boolean accept(CfService service) {
        boolean match = service.existsByLabelStartsWith("conversation");
        LOG.info("Match [" + match + "] to service " + service.toString());
        return match;
    }

    @Override
    public CfEnvProcessorProperties getProperties() {
        return CfEnvProcessorProperties.builder().propertyPrefixes("assistant").serviceName("Assistant").build();
    }

    @Override
    public void process(CfCredentials cfCredentials, Map<String, Object> properties) {
        // set watsonVersion to date of the released watson spring boot starter
        // version 1.0.0 was released on 2019-05-07
        String watsonVersion = "2018-05-07";
        properties.put("watson.assistant.url", cfCredentials.getUri("http"));
        properties.put("watson.assistant.iam-api-key", cfCredentials.getString("apikey"));
        properties.put("watson.assistant.versionDate", watsonVersion);

    }
}