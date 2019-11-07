package com.ibm.cfenv.spring.boot.watsonspeechtotext;

import java.util.Map;
import java.util.logging.Logger;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.core.CfService;
import io.pivotal.cfenv.spring.boot.CfEnvProcessor;
import io.pivotal.cfenv.spring.boot.CfEnvProcessorProperties;

public class WatsonSpeechToTextCfEnvProcessor implements CfEnvProcessor {

    private static final Logger LOG = Logger.getLogger(WatsonSpeechToTextCfEnvProcessor.class.getName());

    public WatsonSpeechToTextCfEnvProcessor() {
        LOG.info("WatsonSpeechToTextCfEnvProcessor built");
    }

    @Override
    public boolean accept(CfService service) {
        boolean match = service.existsByLabelStartsWith("speech_to_text");
        LOG.info("Match [" + match + "] to service " + service.toString());
        return match;
    }

    @Override
    public CfEnvProcessorProperties getProperties() {
        return CfEnvProcessorProperties.builder().propertyPrefixes("speech_to_text").serviceName("Speech_To_Text")
                .build();
    }

    @Override
    public void process(CfCredentials cfCredentials, Map<String, Object> properties) {
        properties.put("watson.speech-to-text.url", cfCredentials.getUri("http"));
        properties.put("watson.speech-to-text.iam-api-key", cfCredentials.getString("apikey"));

    }
}