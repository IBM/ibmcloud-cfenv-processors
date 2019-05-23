package org.terrence.cfenvprocessors.watsoncomparecomply;

import java.util.Map;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.core.CfService;
import io.pivotal.cfenv.spring.boot.CfEnvProcessor;
import io.pivotal.cfenv.spring.boot.CfEnvProcessorProperties;

public class WatsonCompareComplyCfEnvProcessor implements CfEnvProcessor {

    public WatsonCompareComplyCfEnvProcessor() {
        System.out.println("WatsonCompareComplyCfEnvProcessor built");
    }

    @Override
    public boolean accept(CfService service) {
        boolean match = service.existsByLabelStartsWith("compare-comply");
        System.out.println("Match [" + match + "] to service " + service.toString());
        return match;
    }

    @Override
    public CfEnvProcessorProperties getProperties() {
        return CfEnvProcessorProperties.builder().propertyPrefixes("watson_vision_combined")
                .serviceName("Watson_Vision_Combined").build();
    }

    @Override
    public void process(CfCredentials cfCredentials, Map<String, Object> properties) {
        // set watsonVersion to date of the released watson spring boot starter
        // version 1.0.0 was released on 2019-05-07
        String watsonVersion = "2019-05-07";
        properties.put("watson.compare-comply.url", cfCredentials.getUri("http"));
        properties.put("watson.compare-comply.iam-api-key", cfCredentials.getString("apikey"));
        properties.put("watson.compare-comply.versionDate", watsonVersion);

    }
}