package com.ibm.cfenv.spring.boot.data.mongodb;

import com.ibm.beancustomizer.config.BeanCustomizer;
import com.ibm.beancustomizer.config.ExtensibleTypedBeanProcessor;
import com.ibm.sslcontext.SslcontextConfig;
import com.mongodb.MongoClientOptions;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoClientOptionsFactoryBean;

import javax.net.ssl.SSLContext;

/**
 * Expects cfenv.processor.icdmongo.enabled=true
 * cfenv.processor.icdmongo.sslcontext=name-of-sslcontext-bean
 */
@Configuration
@ConditionalOnProperty(name = "cfenv.processor.icdmongo.enabled", havingValue = "true")
@AutoConfigureAfter({ExtensibleTypedBeanProcessor.class, SslcontextConfig.class})
public class MongoClientOptionsCustomizer implements BeanCustomizer<MongoClientOptions> {

    private final SSLContext sslContext;

    public MongoClientOptionsCustomizer(@Value("${cfenv.processor.icdmongo.sslcontext}") String ctxName,
                                        ApplicationContext applicationContext) {
        this.sslContext = applicationContext.getBean(ctxName, SSLContext.class);
    }

    @Override
    public MongoClientOptions postProcessBeforeInit(MongoClientOptions original) {
        return MongoClientOptions.builder(original)
                .sslContext(sslContext)
                .sslEnabled(true)
                .build();
    }

    @Override
    public MongoClientOptions postProcessAfterInit(MongoClientOptions original) {
        return original;
    }

    @Override
    public boolean accepts(Object bean, String beanName) {
        return false;
    }

    @Bean
    @ConditionalOnMissingBean
    public MongoClientOptions defaultMongoClientOptions() {
        try {
            MongoClientOptionsFactoryBean bean = new MongoClientOptionsFactoryBean();
            bean.afterPropertiesSet();
            MongoClientOptions mco = bean.getObject();
            return mco;
        } catch (Exception e) {
            throw new BeanCreationException(e.getMessage(), e);
        }
    }

}