package com.ibm.cfenv.spring.boot.elasticsearch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.http.HttpHeaders;

import javax.net.ssl.SSLContext;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ElasticsearchSSLContextBeanCustomizerTest {
    SSLContext sslContext = Mockito.mock(SSLContext.class);
    ElasticsearchSSLContextBeanCustomizer elasticsearchCustomizer;
    Map<String, SSLContext> sslContexts = new HashMap<>();
    ClientConfiguration clientConfiguration;

    @BeforeEach
    public void init() {
        sslContexts.put("elasticsearch", sslContext);
        elasticsearchCustomizer = new ElasticsearchSSLContextBeanCustomizer(sslContexts);
        
        HttpHeaders headers = new HttpHeaders();         
        headers.setBasicAuth("username","password");

        clientConfiguration = ClientConfiguration.builder().connectedTo("localhost:9200").withDefaultHeaders(headers).build();
    }

    @Test
    public void postProcessBeforeInit() {
        ClientConfiguration clientConfig = elasticsearchCustomizer.postProcessBeforeInit(clientConfiguration);
        assertThat(clientConfig.getSslContext().get()).isEqualTo(sslContext);
    }

    @Test
    public void accepts() {
        assertThat(elasticsearchCustomizer.accepts("", null)).isFalse();
        assertThat(elasticsearchCustomizer.accepts(clientConfiguration, null)).isTrue();
    }
}