package com.ibm.boot.autoconfiguration.kafka;

import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

public class AvroBeanPostProcessor implements BeanPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(AvroBeanPostProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DefaultKafkaProducerFactory) {
            logger.info("we have a DefaultKafkaProducerFactory");
            DefaultKafkaProducerFactory producerFactory = (DefaultKafkaProducerFactory) bean;
            Map<String, Object> configs = new HashMap<>(producerFactory.getConfigurationProperties());
            configs.put(VALUE_SERIALIZER_CLASS_CONFIG, AvroSerializer.class);
            configs.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            return new DefaultKafkaProducerFactory<String, Object>(configs);
        }

        if (bean instanceof DefaultKafkaConsumerFactory) {
            logger.info("we have DefaultKafkaConsumerFactory");
            DefaultKafkaConsumerFactory producerFactory = (DefaultKafkaConsumerFactory) bean;
            Map<String, Object> configs = new HashMap<>(producerFactory.getConfigurationProperties());
            configs.put(VALUE_SERIALIZER_CLASS_CONFIG, AvroDeserializer.class);
            configs.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            return new DefaultKafkaConsumerFactory<String, String>(configs);
        }

        return bean;
    }
}
