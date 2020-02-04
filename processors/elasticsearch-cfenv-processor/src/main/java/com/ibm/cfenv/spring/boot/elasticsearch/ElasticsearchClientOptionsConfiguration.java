package com.ibm.cfenv.spring.boot.elasticsearch;

import java.util.Map;

import javax.net.ssl.SSLContext;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;


@Configuration
public class ElasticsearchClientOptionsConfiguration {

    @Value("${elasticsearch.username}")
    String username;
    
    @Value("${elasticsearch.password}")
    String password;
    
    @Value("${elasticsearch.host}")
    String host;
    
    @Value("${elasticsearch.port}")
    String port;
    
    Map<String, SSLContext> sslContexts;
    
    @Bean
    public ElasticsearchSSLContextBeanCustomizer elasticsearchSSLContextBeanCustomizer(Map<String, SSLContext> sslContexts) {
        this.sslContexts = sslContexts;
        return new ElasticsearchSSLContextBeanCustomizer(sslContexts);
    }

    @Bean
    @ConditionalOnMissingBean
    public ClientConfiguration defaultClientConfiguration() {
        try {
            SSLContext sslContext = sslContexts.get("elasticsearch");
            return ClientConfiguration.builder()
                    .connectedTo(host + ":" + port)
                    .usingSsl(sslContext)
                    .withBasicAuth(username, password)
                    .build();
        } catch (final Exception e) {
            throw new BeanCreationException(e.getMessage(), e);
        }
    }
}
