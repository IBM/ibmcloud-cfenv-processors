package com.ibm.cfenv.spring.boot.amqp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.util.Map;

@Configuration
public class AmqpSSLContextConfiguration {

    @Bean
    public AmqpSSLContextBeanCustomizer beanCustomizer(Map<String, SSLContext> sslContexts) {
        return new AmqpSSLContextBeanCustomizer(sslContexts);
    }
}