package org.terrence.cfenvprocessors.watsonvisualrecognition;

import java.util.Map;
import java.util.logging.Logger;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.core.CfService;
import io.pivotal.cfenv.spring.boot.CfEnvProcessor;
import io.pivotal.cfenv.spring.boot.CfEnvProcessorProperties;

public class WatsonVisualRecognitionCfEnvProcessor implements CfEnvProcessor {

    private static final Logger LOG = Logger.getLogger(WatsonVisualRecognitionCfEnvProcessor.class.getName());

    public WatsonVisualRecognitionCfEnvProcessor() {
        LOG.info("WatsonVisualRecognitionCfEnvProcessor built");
    }

    @Override
    public boolean accept(CfService service) {
        boolean match = service.existsByLabelStartsWith("watson_vision_combined");
        LOG.info("Match [" + match + "] to service " + service.toString());
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
        properties.put("watson.visual-recognition.url", cfCredentials.getUri("http"));
        properties.put("watson.visual-recognition.iam-api-key", cfCredentials.getString("apikey"));
        properties.put("watson.visual-recognition.versionDate", watsonVersion);

    }
}