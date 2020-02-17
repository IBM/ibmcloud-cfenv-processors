package com.ibm.cfenv.spring.boot.amqp;

import com.ibm.beancustomizer.config.BeanCustomizer;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.net.ssl.SSLContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AmqpSSLContextBeanCustomizer implements BeanCustomizer<CachingConnectionFactory> {
    private static final Logger logger = LoggerFactory.getLogger(AmqpSSLContextBeanCustomizer.class);

    private final Map<String, SSLContext> sslContexts;

    @Autowired(required = false)
    public AmqpSSLContextBeanCustomizer(Map<String, SSLContext> sslContexts) {
        if (sslContexts == null) {
            this.sslContexts = new HashMap<>();
        } else {
            this.sslContexts = sslContexts;
        }
        logger.info("AmqpSSLContextBeanCustomizer will attempt to configure with these contexts = {}", this.sslContexts.keySet());
    }

    @Override
    public CachingConnectionFactory postProcessBeforeInit(CachingConnectionFactory cachingConnectionFactory) {
        SSLContext sslContext = sslContexts.get("amqp");
        if (sslContext == null) {
            logger.warn("SSL configuration for amqp is null");
        } else {
            logger.info("Configuring the SSL configuration for amqp");
            ConnectionFactory connectionFactory = cachingConnectionFactory.getRabbitConnectionFactory();
            connectionFactory.useSslProtocol(sslContext);
            try {
                Method enableHostnameVerification = ConnectionFactory.class.getMethod("enableHostnameVerification");
                if (enableHostnameVerification != null) {
                    enableHostnameVerification.invoke(connectionFactory);
                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                logger.info("enableHostnameVerification method doesn't exist in com.rabbitmq.client.ConnectionFactory, skipping the method invocation");
            }
        }
        return cachingConnectionFactory;
    }

    @Override
    public CachingConnectionFactory postProcessAfterInit(CachingConnectionFactory original) {
        return original;
    }

    @Override
    public boolean accepts(Object bean, String beanName) {
        return bean instanceof CachingConnectionFactory;
    }

}