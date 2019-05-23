package org.terrence.cfenvprocessors.watsonnaturallanguageunderstanding;

import java.util.Map;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.core.CfService;
import io.pivotal.cfenv.spring.boot.CfEnvProcessor;
import io.pivotal.cfenv.spring.boot.CfEnvProcessorProperties;

public class WatsonNaturalLanguageUnderstandingCfEnvProcessor implements CfEnvProcessor {

    public WatsonNaturalLanguageUnderstandingCfEnvProcessor() {
        System.out.println("WatsonNaturalLanguageUnderstandingCfEnvProcessor built");
    }

    @Override
    public boolean accept(CfService service) {
        boolean match = service.existsByLabelStartsWith("natural-language-understanding");
        System.out.println("Match [" + match + "] to service " + service.toString());
        return match;
    }

    @Override
    public CfEnvProcessorProperties getProperties() {
        return CfEnvProcessorProperties.builder().propertyPrefixes("natural-language-understanding")
                .serviceName("Natural_Language_Understanding").build();
    }

    @Override
    public void process(CfCredentials cfCredentials, Map<String, Object> properties) {
        // set watsonVersion to date of the released watson spring boot starter
        // version 1.0.0 was released on 2019-05-07
        String watsonVersion = "2019-05-07";
        properties.put("watson.natural-language-understanding.url", cfCredentials.getUri("http"));
        properties.put("watson.natural-language-understanding.iam-api-key", cfCredentials.getString("apikey"));
        properties.put("watson.natural-language-understanding.versionDate", watsonVersion);

    }
}