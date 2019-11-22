package com.ibm.cfenv.spring.boot.data.mongodb;

import com.ibm.beancustomizer.config.BeanCustomizer;
import com.mongodb.MongoClientOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.util.HashMap;
import java.util.Map;

/**
 * Expects cfenv.processor.icdmongo.enabled=true
 * cfenv.processor.icdmongo.sslcontext=name-of-sslcontext-bean
 */
@Configuration
public class MongoClientOptionsSSLContextBeanCustomizer implements BeanCustomizer<MongoClientOptions> {

    private final Map<String, SSLContext> sslContexts;

    @Autowired(required = false)
    public MongoClientOptionsSSLContextBeanCustomizer(Map<String, SSLContext> sslContexts) {
        if (sslContexts == null) {
            this.sslContexts = new HashMap<>();
        } else {
            this.sslContexts = sslContexts;
        }
    }

    @Override
    public MongoClientOptions postProcessBeforeInit(MongoClientOptions mongoClientOptions) {
        SSLContext sslContext = sslContexts.get("mongodb");
        if (sslContext != null) {
            mongoClientOptions = MongoClientOptions.builder(mongoClientOptions)
                    .sslContext(sslContext)
                    .sslEnabled(true)
                    .build();
        }
        return mongoClientOptions;
    }

    @Override
    public MongoClientOptions postProcessAfterInit(MongoClientOptions original) {
        return original;
    }

    @Override
    public boolean accepts(Object bean, String beanName) {
        return bean instanceof MongoClientOptions;
    }
}