package com.ibm.cfenv.spring.boot.data;

import io.pivotal.cfenv.core.CfCredentials;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class MongoDBCfEnvProcessorTest {

    MongoDBCfEnvProcessor mongoDBCfEnvProcessor = new MongoDBCfEnvProcessor();

    @Test
    void process() {
        List<String> uris = Arrays.asList("http://localhost:9093");
        HashMap<Object, Object> certificate = new HashMap<>();
        certificate.put("certificate_base64", "test");

        HashMap<Object, Object> mongodb = new HashMap<>();
        mongodb.put("composed", uris);
        mongodb.put("certificate", certificate);

        HashMap<Object, Object> connection = new HashMap<>();
        connection.put("mongodb", mongodb);

        Map<String, Object> credentialsData = new HashMap<>();
        credentialsData.put("connection", connection);

        CfCredentials cfCredentials = new CfCredentials(credentialsData);
        Map<String, Object> properties = new HashMap<>();
        mongoDBCfEnvProcessor.process(cfCredentials, properties);

        assertThat(properties.get("spring.data.mongodb.uri")).isEqualTo("http://localhost:9093");
        assertThat(properties.get("sslcontext.enabled")).isEqualTo(true);
        assertThat(properties.get("sslcontext.contexts.mongodb.trustedcert")).isEqualTo("test");
        assertThat(properties.get("cfenv.processor.icdmongo.enabled")).isEqualTo(true);
        assertThat(properties.get("cfenv.processor.icdmongo.sslcontext")).isEqualTo("mongodb");
    }
}