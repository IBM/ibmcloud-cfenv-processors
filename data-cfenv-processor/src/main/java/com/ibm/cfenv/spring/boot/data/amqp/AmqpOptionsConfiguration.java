package com.ibm.cfenv.spring.boot.data.amqp;

import com.ibm.beancustomizer.config.BeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.util.Map;

/**
 * Expects cfenv.processor.icdmongo.enabled=true
 * cfenv.processor.icdmongo.sslcontext=name-of-sslcontext-bean
 */
@Configuration
//@ConditionalOnProperty(name = "cfenv.processor.icdamqp.enabled", havingValue = "true")
//@AutoConfigureAfter({SslcontextConfig.class})
public class AmqpOptionsConfiguration {

    @Bean
    public BeanCustomizer beanCustomizer(Map<String, SSLContext> sslContexts) {
        return new AmqpOptionsBeanCustomizer(sslContexts);
    }
}