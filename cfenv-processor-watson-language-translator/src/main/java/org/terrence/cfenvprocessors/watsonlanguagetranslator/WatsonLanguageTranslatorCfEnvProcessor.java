package org.terrence.cfenvprocessors.watsonlanguagetranslator;

import java.util.Map;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.core.CfService;
import io.pivotal.cfenv.spring.boot.CfEnvProcessor;
import io.pivotal.cfenv.spring.boot.CfEnvProcessorProperties;

public class WatsonLanguageTranslatorCfEnvProcessor implements CfEnvProcessor {

    public WatsonLanguageTranslatorCfEnvProcessor() {
        System.out.println("WatsonLanguageTranslatorCfEnvProcessor built");
    }

    @Override
    public boolean accept(CfService service) {
        boolean match = service.existsByLabelStartsWith("language_translator");
        System.out.println("Match [" + match + "] to service " + service.toString());
        return match;
    }

    @Override
    public CfEnvProcessorProperties getProperties() {
        return CfEnvProcessorProperties.builder().propertyPrefixes("language_translator")
                .serviceName("Language_Translator").build();
    }

    @Override
    public void process(CfCredentials cfCredentials, Map<String, Object> properties) {
        // set watsonVersion to date of the released watson spring boot starter
        // version 0.3.0 was released on 2019-05-07
        String watsonVersion = "2019-05-07";
        properties.put("watson.language-translator.url", cfCredentials.getUri("http"));
        properties.put("watson.language-translator.iam-api-key", cfCredentials.getString("apikey"));
        properties.put("watson.language-translator.versionDate", watsonVersion);

    }
}