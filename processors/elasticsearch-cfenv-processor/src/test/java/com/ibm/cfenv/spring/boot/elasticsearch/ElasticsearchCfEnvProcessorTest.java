package com.ibm.cfenv.spring.boot.elasticsearch;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.pivotal.cfenv.core.CfService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.fasterxml.jackson.databind.ObjectMapper;

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
        assertThat(properties.size()).isEqualTo(8);
        assertThat(properties.get("ibm.cfenv.processor.elasticsearch.username")).isEqualTo("elasticsearch_username");
        assertThat(properties.get("ibm.cfenv.processor.elasticsearch.password")).isEqualTo("elasticsearch_password");
        assertThat(properties.get("ibm.cfenv.processor.elasticsearch.host")).isEqualTo("elasticsearch_host");
        assertThat(properties.get("ibm.cfenv.processor.elasticsearch.port")).isEqualTo(30001);
        assertThat(properties.get("sslcontext.contexts.elasticsearch.certificate")).isEqualTo("LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUREekNDQWZlZ0F3SUJBZ0lKQU5FSDU4eTIva3pITUEwR0NTcUdTSWIzRFFFQkN3VUFNQjR4SERBYUJnTlYKQkFNTUUwbENUU0JEYkc5MVpDQkVZWFJoWW1GelpYTXdIaGNOTVRnd05qSTFNVFF5T1RBd1doY05Namd3TmpJeQpNVFF5T1RBd1dqQWVNUnd3R2dZRFZRUUREQk5KUWswZ1EyeHZkV1FnUkdGMFlXSmhjMlZ6TUlJQklqQU5CZ2txCmhraUc5dzBCQVFFRkFBT0NBUThBTUlJQkNnS0NBUUVBOGxwYVFHemNGZEdxZU1sbXFqZmZNUHBJUWhxcGQ4cUoKUHIzYklrclhKYlRjSko5dUlja1NVY0NqdzRaL3JTZzhublQxM1NDY09sKzF0bys3a2RNaVU4cU9XS2ljZVlaNQp5K3laWWZDa0dhaVpWZmF6UUJtNDV6QnRGV3YrQUIvOGhmQ1RkTkY3Vlk0c3BhQTNvQkUyYVM3T0FOTlNSWlNLCnB3eTI0SVVnVWNJTEpXK21jdlc4MFZ4K0dYUmZEOVl0dDZQUkpnQmhZdVVCcGd6dm5nbUNNR0JuK2wyS05pU2YKd2VvdllEQ0Q2Vm5nbDIrNlc5UUZBRnRXWFdnRjNpRFFENW5sL240bXJpcE1TWDZVRy9uNjY1N3U3VERkZ2t2QQoxZUtJMkZMellLcG9LQmU1cmNuck03bkhnTmMvbkNkRXM1SmVjSGIxZEh2MVFmUG02cHpJeHdJREFRQUJvMUF3ClRqQWRCZ05WSFE0RUZnUVVLMytYWm8xd3lLcytERW9ZWGJIcnV3U3BYamd3SHdZRFZSMGpCQmd3Rm9BVUszK1gKWm8xd3lLcytERW9ZWGJIcnV3U3BYamd3REFZRFZSMFRCQVV3QXdFQi96QU5CZ2txaGtpRzl3MEJBUXNGQUFPQwpBUUVBSmY1ZHZselVwcWFpeDI2cUpFdXFGRzBJUDU3UVFJNVRDUko2WHQvc3VwUkhvNjNlRHZLdzh6Ujd0bFdRCmxWNVAwTjJ4d3VTbDlacUFKdDcvay8zWmVCK25Zd1BveU8zS3ZLdkFUdW5SdmxQQm40RldWWGVhUHNHKzdmaFMKcXNlam1reW9uWXc3N0hSekdPekpINFpnOFVONm1mcGJhV1NzeWFFeHZxa25DcDlTb1RRUDNENjdBeldxYjF6WQpkb3FxZ0dJWjJueENrcDUvRlh4Ri9UTWI1NXZ0ZVRRd2ZnQnk2MGpWVmtiRjdlVk9XQ3YwS2FOSFBGNWhycWJOCmkrM1hqSjcvcGVGM3hNdlRNb3kzNURjVDNFMlplU1Zqb3VaczE1Tzkwa0kzazJkYVMyT0hKQUJXMHZTajRuTHoKK1BRenAvQjljUW1PTzhkQ2UwNDlRM29hVUE9PQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0tCgo=");
    }


    @Test
    public void validCFService_getProperties_correctServiceNameAndPrefixes() {
        assertThat(elasticsearchCfEnvProcessor.getProperties().getServiceName()).isEqualTo("elasticsearch");
        assertThat(elasticsearchCfEnvProcessor.getProperties().getPropertyPrefixes()).isEqualTo("sslcontext,elasticsearch");
    }
}
