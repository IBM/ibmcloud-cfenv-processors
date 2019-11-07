package com.ibm.cfenv.spring.boot.watson;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.core.CfService;
import io.pivotal.cfenv.spring.boot.CfEnvProcessor;
import io.pivotal.cfenv.spring.boot.CfEnvProcessorProperties;

import java.util.Map;
import java.util.logging.Logger;

public class NaturalLanguageClassifierCfEnvProcessor implements CfEnvProcessor {

    private static final Logger LOG = Logger.getLogger(NaturalLanguageClassifierCfEnvProcessor.class.getName());

    public NaturalLanguageClassifierCfEnvProcessor() {
        LOG.info("WatsonNaturalLanguageClassifierCfEnvProcessor built");
    }

    @Override
    public boolean accept(CfService service) {
        boolean match = service.existsByLabelStartsWith("natural_language_classifier");
        LOG.info("Match [" + match + "] to service " + service.toString());
        return match;
    }

    @Override
    public CfEnvProcessorProperties getProperties() {
        return CfEnvProcessorProperties.builder().propertyPrefixes("natural_language_classifier")
                .serviceName("Natural_Language_Classifier").build();
    }

    @Override
    public void process(CfCredentials cfCredentials, Map<String, Object> properties) {
        properties.put("watson.natural-language-classifier.url", cfCredentials.getUri("http"));
        properties.put("watson.natural-language-classifier.iam-api-key", cfCredentials.getString("apikey"));

    }
}