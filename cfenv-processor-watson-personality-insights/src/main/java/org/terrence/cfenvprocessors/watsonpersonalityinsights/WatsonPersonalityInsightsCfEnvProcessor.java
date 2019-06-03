package org.terrence.cfenvprocessors.watsonpersonalityinsights;

import java.util.Map;
import java.util.logging.Logger;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.core.CfService;
import io.pivotal.cfenv.spring.boot.CfEnvProcessor;
import io.pivotal.cfenv.spring.boot.CfEnvProcessorProperties;

public class WatsonPersonalityInsightsCfEnvProcessor implements CfEnvProcessor {

    private static final Logger LOG = Logger.getLogger(WatsonPersonalityInsightsCfEnvProcessor.class.getName());

    public WatsonPersonalityInsightsCfEnvProcessor() {
        LOG.info("WatsonPersonalityInsightsCfEnvProcessor built");
    }

    @Override
    public boolean accept(CfService service) {
        boolean match = service.existsByLabelStartsWith("personality_insights");
        LOG.info("Match [" + match + "] to service " + service.toString());
        return match;
    }

    @Override
    public CfEnvProcessorProperties getProperties() {
        return CfEnvProcessorProperties.builder().propertyPrefixes("personality_insights")
                .serviceName("Personality_Insights").build();
    }

    @Override
    public void process(CfCredentials cfCredentials, Map<String, Object> properties) {
        // set watsonVersion to date of the released watson spring boot starter
        // version 1.0.0 was released on 2019-05-07
        String watsonVersion = "2018-05-07";
        properties.put("watson.personality-insights.url", cfCredentials.getUri("http"));
        properties.put("watson.personality-insights.iam-api-key", cfCredentials.getString("apikey"));
        properties.put("watson.personality-insights.versionDate", watsonVersion);

    }
}