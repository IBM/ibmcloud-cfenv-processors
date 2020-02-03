package com.ibm.cfenv.spring.boot.elasticsearch;

import io.pivotal.cfenv.core.CfService;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ElasticsearchCfEnvProcessorTest {

    ElasticsearchCfEnvProcessor elasticsearchCfEnvProcessor = new ElasticsearchCfEnvProcessor();

    public CfService initCFService() throws IOException {
        URL url = ElasticsearchCfEnvProcessor.class.getClassLoader().getResource("./vcap-services.json");
        Map<String, Object> serviceData = (Map<String, Object>) ((List) new ObjectMapper()
                .readValue(url, Map.class)
                .get("databases-for-elasticsearch"))
                .get(0);
        return new CfService(serviceData);
    }


    @Test
    public void validCFService_accept_serviceAccepted() throws IOException {
        assertThat(elasticsearchCfEnvProcessor.accept(initCFService())).isTrue();
    }


    @Test
    public void validCFService_propertiesProcessed_CorrectElasticsearchSetup() throws IOException {
        Map<String, Object> properties = new HashMap<>();
        elasticsearchCfEnvProcessor.process(initCFService().getCredentials(), properties);
        assertThat(properties.size()).isEqualTo(5);
        assertThat(properties.get("elasticsearch.username")).isEqualTo("elasticsearch_username");
        assertThat(properties.get("elasticsearch.password")).isEqualTo("elasticsearch_password");
        assertThat(properties.get("elasticsearch.host")).isEqualTo("elasticsearch_host");
        assertThat(properties.get("elasticsearch.port")).isEqualTo(30001);
    }


    @Test
    public void validCFService_getProperties_correctServiceNameAndPrefixes() {
        assertThat(elasticsearchCfEnvProcessor.getProperties().getServiceName()).isEqualTo("elasticsearch");
        assertThat(elasticsearchCfEnvProcessor.getProperties().getPropertyPrefixes()).isEqualTo("elasticsearch");
    }
}
