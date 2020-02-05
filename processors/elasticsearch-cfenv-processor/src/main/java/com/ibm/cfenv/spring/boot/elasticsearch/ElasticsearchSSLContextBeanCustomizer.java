package com.ibm.cfenv.spring.boot.elasticsearch;


import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.client.ClientConfiguration;

import com.ibm.beancustomizer.config.BeanCustomizer;

public class ElasticsearchSSLContextBeanCustomizer implements BeanCustomizer<ClientConfiguration> {

    private final Map<String, SSLContext> sslContexts;
    
    @Value("${elasticsearch.username}")
    String username;
    
    @Value("${elasticsearch.password}")
    String password;
    
    @Value("${elasticsearch.host}")
    String host;
    
    @Value("${elasticsearch.port}")
    String port;
    
    @Autowired(required = false)
    public ElasticsearchSSLContextBeanCustomizer(Map<String, SSLContext> sslContexts) {
        if (sslContexts == null) {
            this.sslContexts = new HashMap<>();
        } else {
            this.sslContexts = sslContexts;
        }
    }
    
    @Override
    public boolean accepts(Object bean, String beanName) {
        return bean instanceof ClientConfiguration;
    }

    @Override
    public ClientConfiguration postProcessAfterInit(ClientConfiguration original) {
        return original;
    }

    @Override
    public ClientConfiguration postProcessBeforeInit(ClientConfiguration original) {
        SSLContext sslContext = sslContexts.get("elasticsearch");
        if (sslContext != null) {
            original = ClientConfiguration.builder()
                    .connectedTo(original.getEndpoints().get(0))
                    .usingSsl(sslContext)
                    .withDefaultHeaders(original.getDefaultHeaders())
                    .withConnectTimeout(original.getConnectTimeout())
                    .withSocketTimeout(original.getSocketTimeout())
                    .build();
        }
        return original;
    }
}
