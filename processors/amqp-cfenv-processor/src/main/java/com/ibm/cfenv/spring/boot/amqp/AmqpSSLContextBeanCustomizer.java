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
            Method enableHostnameVerification = null;
            try {
                enableHostnameVerification = ConnectionFactory.class.getMethod("enableHostnameVerification");
                enableHostnameVerification.invoke(connectionFactory);
            } catch (NoSuchMethodException e) {
                logger.debug("enableHostnameVerification method doesn't exist in com.rabbitmq.client.ConnectionFactory, ignoring the method invocation");
            } catch (SecurityException e) {
                logger.warn("unable to reflectively invoke enableHostnameVerification");
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                logger.warn("unable to invoke enableHostnameVerification due to potential spring boot rabbit version mismatch");
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