package com.ibm.cfenv.spring.boot.elasticsearch;

import java.util.Map;
import javax.net.ssl.SSLContext;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.elasticsearch.ReactiveRestClientProperties;
import org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientBuilderCustomizer;
import org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.client.reactive.ReactiveRestClients;
import org.springframework.http.HttpHeaders;

@Configuration
@EnableConfigurationProperties({ RestClientProperties.class, ReactiveRestClientProperties.class })
public class ElasticsearchClientOptionsConfiguration {

    @Autowired
    RestClientProperties restClientProperties;
    
    @Autowired
    ReactiveRestClientProperties reactiveRestClientProperties;
    
    @Autowired
    private Environment env;
    
    Map<String, SSLContext> sslContexts;
    
    @Bean
    public ElasticsearchSSLContextBeanCustomizer elasticsearchSSLContextBeanCustomizer(Map<String, SSLContext> sslContexts) {
        this.sslContexts = sslContexts;
        return new ElasticsearchSSLContextBeanCustomizer(sslContexts);
    }

    @Configuration
    @ConditionalOnProperty(prefix = "spring.elasticsearch.rest", name = "uris")
    public class CustomRestClientBuilderCustomizer implements RestClientBuilderCustomizer {

        @Override
        public void customize(RestClientBuilder builder) {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            Credentials credentials = new UsernamePasswordCredentials(restClientProperties.getUsername(), restClientProperties.getPassword());
            credentialsProvider.setCredentials(AuthScope.ANY, credentials);
            builder.setHttpClientConfigCallback(httpClientBuilder -> {
                httpClientBuilder
                    .setDefaultCredentialsProvider(credentialsProvider)
                    .setSSLContext(sslContexts.get("elasticsearch"));
                return httpClientBuilder;
            });
            builder.setRequestConfigCallback(requestConfigBuilder -> {
                requestConfigBuilder
                    .setConnectTimeout((int) restClientProperties.getConnectionTimeout().toMillis())
                    .setSocketTimeout((int) restClientProperties.getReadTimeout().toMillis());
                return requestConfigBuilder;
            });
        }
    }
    
    @Bean
    @ConditionalOnProperty(prefix = "spring.data.elasticsearch.client.reactive", name = "endpoints")
    @ConditionalOnMissingBean
    public ClientConfiguration defaultClientConfiguration() {
        String endpoints = env.getProperty("spring.data.elasticsearch.client.reactive.endpoints");
        String username = env.getProperty("spring.data.elasticsearch.client.reactive.username");
        String password = env.getProperty("spring.data.elasticsearch.client.reactive.password");
        try {
            HttpHeaders headers = new HttpHeaders();         
            headers.setBasicAuth(username, password);
            
            ClientConfiguration.MaybeSecureClientConfigurationBuilder builder = ClientConfiguration.builder().connectedTo(endpoints);
            
            PropertyMapper map = PropertyMapper.get();
            map.from(reactiveRestClientProperties.getConnectionTimeout()).whenNonNull().to(builder::withConnectTimeout);
            map.from(reactiveRestClientProperties.getSocketTimeout()).whenNonNull().to(builder::withSocketTimeout);
            
            return builder
                    .withDefaultHeaders(headers)
                    .build();
        } catch (final Exception e) {
            throw new BeanCreationException(e.getMessage(), e);
        }
    }
    
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.data.elasticsearch.client.reactive", name = "endpoints")
    public ReactiveElasticsearchClient reactiveElasticsearchClient(ClientConfiguration clientConfiguration) {
        return ReactiveRestClients.create(clientConfiguration);
    }
}
