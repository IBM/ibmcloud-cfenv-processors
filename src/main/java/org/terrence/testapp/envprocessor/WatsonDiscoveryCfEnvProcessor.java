package org.terrence.testapp.envprocessor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.core.CfService;
import io.pivotal.cfenv.spring.boot.CfEnvProcessor;
import io.pivotal.cfenv.spring.boot.CfEnvProcessorProperties;

public class WatsonDiscoveryCfEnvProcessor implements CfEnvProcessor {

    public WatsonDiscoveryCfEnvProcessor() {
        System.out.println("WatsonDiscoveryCfEnvProcessor built");
    }

    @Override
    public boolean accept(CfService service) {
        // System.out.println("service credentials username: " +
        // service.createCredentials().getUsername());
        // System.out.println("service credentials password: " +
        // service.createCredentials().getPassword());
        // System.out.println("service credentials getMap: " +
        // service.createCredentials().getMap());
        // System.out.println("service data getMap: " + service.getMap());
        boolean match = service.existsByLabelStartsWith("discovery");
        // System.out.println("Match [" + match + "] to service " + service.toString());
        return match;
    }

    @Override
    public CfEnvProcessorProperties getProperties() {
        // System.out.println("CfEnvProcessorProperties string: " +
        // CfEnvProcessorProperties.builder());
        // System.out.println("CfEnvProcessorProperties: "
        // +
        // CfEnvProcessorProperties.builder().propertyPrefixes("discovery").serviceName("Discovery").build());
        return CfEnvProcessorProperties.builder().propertyPrefixes("discovery").serviceName("Discovery").build();
    }

    @Override
    public void process(CfCredentials cfCredentials, Map<String, Object> properties) {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String versionDate = simpleDateFormat.format(new Date());
        // System.out.println("versionDate: " + versionDate);

        // System.out.println("cfCredentials getMap = " + cfCredentials.getMap());
        // System.out.println("cfCredentials getMap toString = " +
        // cfCredentials.getMap().toString());

        // System.out.println("createOrGetUriInfo: " + cfCredentials.getUriInfo());

        properties.put("watson.discovery.url", cfCredentials.getUri("http"));
        properties.put("watson.discovery.iam-api-key", cfCredentials.getString("apikey"));
        properties.put("watson.discovery.versionDate", versionDate);

    }
}