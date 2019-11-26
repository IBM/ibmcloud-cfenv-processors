package com.ibm.cfenv.spring.boot.data.mongodb;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.core.CfService;
import io.pivotal.cfenv.spring.boot.CfEnvProcessor;
import io.pivotal.cfenv.spring.boot.CfEnvProcessorProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class MongoDBCfEnvProcessor implements CfEnvProcessor {

    private static final Logger LOG = Logger.getLogger(MongoDBCfEnvProcessor.class.getName());

    @Override
    public boolean accept(CfService service) {
        boolean match = service.existsByLabelStartsWith("databases-for-mongodb");
        LOG.info("MongoDBCfEnvProcessor matched service entry : " + service.getName());
        return match;
    }

    @Override
    public CfEnvProcessorProperties getProperties() {
        return CfEnvProcessorProperties.builder()
                .propertyPrefixes("sslcontext,spring.data.mongodb")
                .serviceName("MongoDB")
                .build();
    }

    @Override
    public void process(CfCredentials cfCredentials, Map<String, Object> properties) {
        Map<String, Object> credentials = cfCredentials.getMap();
        Map<String, Object> connection = (Map<String, Object>) credentials.getOrDefault("connection", new HashMap<>());
        Map<String, Object> mongodb = (Map<String, Object>) connection.getOrDefault("mongodb", new HashMap<>());
        List<String> composed = (List<String>) mongodb.getOrDefault("composed", new ArrayList<>(0));
        if (!composed.isEmpty()) {
            String uri = composed.get(0);
            Map<String, Object> certificate = (Map<String, Object>) mongodb.get("certificate");
            String trustedcert = certificate.get("certificate_base64").toString();
            properties.put("spring.data.mongodb.uri", uri);
            properties.put("sslcontext.contexts.mongodb.trustedcert", trustedcert);
            LOG.info("Processed the mongodb connection correctly");
        }
    }
}