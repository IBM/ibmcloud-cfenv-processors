package com.ibm.cfenv.spring.boot.amqp;

import com.ibm.beancustomizer.config.BeanCustomizer;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.net.ssl.SSLContext;
import java.util.HashMap;
import java.util.Map;

public class AmqpSSLContextBeanCustomizer implements BeanCustomizer<CachingConnectionFactory> {

    private final Map<String, SSLContext> sslContexts;

    @Autowired(required = false)
    public AmqpSSLContextBeanCustomizer(Map<String, SSLContext> sslContexts) {
        if (sslContexts == null) {
            this.sslContexts = new HashMap<>();
        } else {
            this.sslContexts = sslContexts;
        }
    }

    @Override
    public CachingConnectionFactory postProcessBeforeInit(CachingConnectionFactory original) {
        CachingConnectionFactory cachingConnectionFactory = original;
        if (sslContexts.containsKey("amqp")) {
            SSLContext sslContext = sslContexts.get("amqp");
            ConnectionFactory connectionFactory = cachingConnectionFactory.getRabbitConnectionFactory();
            connectionFactory.useSslProtocol(sslContext);
            connectionFactory.enableHostnameVerification();
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