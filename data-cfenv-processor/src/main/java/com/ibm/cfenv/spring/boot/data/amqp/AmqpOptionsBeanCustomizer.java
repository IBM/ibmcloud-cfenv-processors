package com.ibm.cfenv.spring.boot.data.amqp;

import com.ibm.beancustomizer.config.BeanCustomizer;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;

import javax.net.ssl.SSLContext;
import java.util.Map;

public class AmqpOptionsBeanCustomizer implements BeanCustomizer {

    private final Map<String, SSLContext> sslContexts;

    public AmqpOptionsBeanCustomizer(Map<String, SSLContext> sslContexts) {
        this.sslContexts = sslContexts;
    }

    @Override
    public Class getType() {
        return CachingConnectionFactory.class;
    }

    @Override
    public Object postProcessBeforeInit(Object original) {
        Object result = original;
        if (sslContexts.containsKey("amqp")) {
            SSLContext sslContext = sslContexts.get("amqp");
            CachingConnectionFactory cachingConnectionFactory = (CachingConnectionFactory) original;
            ConnectionFactory connectionFactory = cachingConnectionFactory.getRabbitConnectionFactory();
            connectionFactory.useSslProtocol(sslContext);
            connectionFactory.enableHostnameVerification();
        }
        return result;
    }

    @Override
    public Object postProcessAfterInit(Object original) {
        return original;
    }
}