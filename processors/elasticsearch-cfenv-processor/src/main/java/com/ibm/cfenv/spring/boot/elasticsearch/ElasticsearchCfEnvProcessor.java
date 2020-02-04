package com.ibm.cfenv.spring.boot.elasticsearch;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.core.CfService;
import io.pivotal.cfenv.spring.boot.CfEnvProcessor;
import io.pivotal.cfenv.spring.boot.CfEnvProcessorProperties;

import java.util.List;
import java.util.Map;

public class ElasticsearchCfEnvProcessor implements CfEnvProcessor {

    @Override
    public boolean accept(CfService service) {
        return service.existsByLabelStartsWith("databases-for-elasticsearch");
    }

    @Override
    public CfEnvProcessorProperties getProperties() {
        return CfEnvProcessorProperties.builder()
                .propertyPrefixes("sslcontext,elasticsearch")
                .serviceName("elasticsearch")
                .build();
    }

    @Override
    public void process(CfCredentials cfCredentials, Map<String, Object> properties) {
        Map<String, Object> credentialsData = cfCredentials.getMap();
        Map<String, Object> connection = (Map<String, Object>) credentialsData.get("connection");
        Map<String, Object> https = (Map<String, Object>) connection.get("https");
        List<Map<String, Object>> hosts = (List<Map<String, Object>>) https.get("hosts");
        Map<String, Object> host = hosts.get(0);
        String hostname = (String) host.get("hostname");
        int port = (int) host.get("port");
        
        Map<String, String> authentication = (Map<String, String>) https.get("authentication");        
        Map<String, String> certificate = (Map<String, String>) https.get("certificate");
        
        properties.put("elasticsearch.host", hostname);
        properties.put("elasticsearch.port", port);
        properties.put("elasticsearch.username", authentication.get("username"));
        properties.put("elasticsearch.password", authentication.get("password"));
        properties.put("sslcontext.contexts.elasticsearch.certificate", certificate.get("certificate_base64"));
    }
}
