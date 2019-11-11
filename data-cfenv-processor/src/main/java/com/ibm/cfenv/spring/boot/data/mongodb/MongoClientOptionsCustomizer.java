package com.ibm.cfenv.spring.boot.data.mongodb;

import com.mongodb.MongoClientOptions;
import org.ozzy.beancustomizer.config.BeanCustomizer;
import org.ozzy.beancustomizer.config.ExtensibleTypedBeanProcessor;
import org.ozzy.sslcontext.config.SslcontextConfig;
import org.springframework.beans.FatalBeanException;
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
public class MongoClientOptionsCustomizer implements BeanCustomizer {

    private final SSLContext sslContext;

    public MongoClientOptionsCustomizer(@Value("${cfenv.processor.icdmongo.sslcontext}") String ctxName,
                                        ApplicationContext applicationContext) {
        this.sslContext = applicationContext.getBean(ctxName, SSLContext.class);
    }

    @Override
    public Class getType() {
        return MongoClientOptions.class;
    }

    @Override
    public Object postProcessBeforeInit(Object original) {
        Object result = original;
        if (original instanceof MongoClientOptions) {
            try {
                MongoClientOptions o = (MongoClientOptions) original;
                result = MongoClientOptions.builder(o)
                        .sslEnabled(true)
                        .sslContext(sslContext)
                        .build();
            } catch (Exception e) {
                throw new FatalBeanException("Unable to add SSL to MongoOptions bean", e);
            }
        }
        return result;
    }

    @Override
    public Object postProcessAfterInit(Object original) {
        return original;
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