package com.ibm.cfenv.spring.boot.watsontexttospeech;

import java.util.Map;
import java.util.logging.Logger;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.core.CfService;
import io.pivotal.cfenv.spring.boot.CfEnvProcessor;
import io.pivotal.cfenv.spring.boot.CfEnvProcessorProperties;

public class WatsonTextToSpeechCfEnvProcessor implements CfEnvProcessor {

    private static final Logger LOG = Logger.getLogger(WatsonTextToSpeechCfEnvProcessor.class.getName());

    public WatsonTextToSpeechCfEnvProcessor() {
        LOG.info("WatsonTextToSpeechCfEnvProcessor built");
    }

    @Override
    public boolean accept(CfService service) {
        boolean match = service.existsByLabelStartsWith("text_to_speech");
        LOG.info("Match [" + match + "] to service " + service.toString());
        return match;
    }

    @Override
    public CfEnvProcessorProperties getProperties() {
        return CfEnvProcessorProperties.builder().propertyPrefixes("text_to_speech").serviceName("Text_To_Speech")
                .build();
    }

    @Override
    public void process(CfCredentials cfCredentials, Map<String, Object> properties) {
        properties.put("watson.text_to_speech.url", cfCredentials.getUri("http"));
        properties.put("watson.text_to_speech.iam-api-key", cfCredentials.getString("apikey"));

    }
}