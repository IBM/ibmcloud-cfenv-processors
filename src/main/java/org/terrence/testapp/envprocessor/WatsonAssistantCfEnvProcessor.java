package org.terrence.testapp.envprocessor;

import java.util.Map;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.core.CfService;
import io.pivotal.cfenv.spring.boot.CfEnvProcessor;
import io.pivotal.cfenv.spring.boot.CfEnvProcessorProperties;

public class WatsonAssistantCfEnvProcessor implements CfEnvProcessor {

    public WatsonAssistantCfEnvProcessor() {
        System.out.println("WatsonAssistantCfEnvProcessor built");
    }

    @Override
    public boolean accept(CfService service) {
        boolean match = service.existsByLabelStartsWith("conversation");
        System.out.println("Match [" + match + "] to service " + service.toString());
        return match;
    }

    @Override
    public CfEnvProcessorProperties getProperties() {
        return CfEnvProcessorProperties.builder().propertyPrefixes("assistant").serviceName("Assistant").build();
    }

    @Override
    public void process(CfCredentials cfCredentials, Map<String, Object> properties) {
        // set watsonVersion to date of the released watson spring boot starter
        // version 0.3.0 was released on 2018-06-22
        String watsonVersion = "2018-06-22";
        properties.put("watson.assistant.url", cfCredentials.getUri("http"));
        properties.put("watson.assistant.iam-api-key", cfCredentials.getString("apikey"));
        properties.put("watson.assistant.versionDate", watsonVersion);

    }
}