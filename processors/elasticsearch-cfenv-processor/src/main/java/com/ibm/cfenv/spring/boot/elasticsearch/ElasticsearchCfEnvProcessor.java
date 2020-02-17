package com.ibm.cfenv.spring.boot.elasticsearch;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.core.CfService;
import io.pivotal.cfenv.spring.boot.CfEnvProcessor;
import io.pivotal.cfenv.spring.boot.CfEnvProcessorProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ElasticsearchCfEnvProcessor implements CfEnvProcessor {

    private static final String REACTIVE_CLIENT = "reactive";
    private static final String REST_CLIENT = "rest";
    
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
        List<String> clients = getClients(System.getenv("IBM_CFENVPROCESSOR_ELASTICSEARCH_CLIENTS_ENABLE"));
        
        Map<String, Object> credentialsData = cfCredentials.getMap();
        Map<String, Object> connection = (Map<String, Object>) credentialsData.get("connection");
        Map<String, Object> https = (Map<String, Object>) connection.get("https");
        List<Map<String, Object>> hosts = (List<Map<String, Object>>) https.get("hosts");
        Map<String, Object> host = hosts.get(0);
        String hostname = (String) host.get("hostname");
        int port = (int) host.get("port");
        
        
        Map<String, String> authentication = (Map<String, String>) https.get("authentication");        
        Map<String, String> certificate = (Map<String, String>) https.get("certificate");
        String username = authentication.get("username");
        String password = authentication.get("password");
        String uris = "https://" + hostname + ":" + port;
        
        if (clients.contains(REST_CLIENT)) {
            properties.put("spring.elasticsearch.rest.uris", uris);
            properties.put("spring.elasticsearch.rest.username", username);
            properties.put("spring.elasticsearch.rest.password", password);
        }

        if (clients.contains(REACTIVE_CLIENT)) {
            properties.put("spring.data.elasticsearch.client.reactive.endpoints", hostname + ":" + port);
            properties.put("spring.data.elasticsearch.client.reactive.username", username);
            properties.put("spring.data.elasticsearch.client.reactive.password", password);
        } else {
            properties.put("spring.elasticsearch.rest.uris", uris);
            properties.put("spring.elasticsearch.rest.username", username);
            properties.put("spring.elasticsearch.rest.password", password);
        }
        
        properties.put("sslcontext.contexts.elasticsearch.certificate", certificate.get("certificate_base64"));
    }
    
    private List<String> getClients(String elasticSearchClients) {
        if (elasticSearchClients == null || elasticSearchClients.isEmpty()) {
            return new ArrayList<String>();
        }
        return Arrays.asList(System.getenv("IBM_CFENVPROCESSOR_ELASTICSEARCH_CLIENTS_ENABLE").toLowerCase().split(","));
    }
}
