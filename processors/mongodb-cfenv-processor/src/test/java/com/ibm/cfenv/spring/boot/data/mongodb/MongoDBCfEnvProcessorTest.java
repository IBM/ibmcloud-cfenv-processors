package com.ibm.cfenv.spring.boot.data.mongodb;

import io.pivotal.cfenv.core.CfCredentials;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MongoDBCfEnvProcessorTest {

    MongoDBCfEnvProcessor mongoDBCfEnvProcessor = new MongoDBCfEnvProcessor();

    @Test
    public void invalidCredentials_noPropertiesAreSet() {
        Map<String, Object> properties = new HashMap<>();
        mongoDBCfEnvProcessor.process(new CfCredentials(new HashMap<>()), properties);

        assertThat(properties.isEmpty()).isTrue();
    }

    @Test
    void validCredentials_propertiesAreSet() {
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

        assertThat(properties.size()).isEqualTo(2);
        assertThat(properties.get("spring.data.mongodb.uri")).isEqualTo("http://localhost:9093");
        assertThat(properties.get("sslcontext.contexts.mongodb.trustedcert")).isEqualTo("test");
    }
}