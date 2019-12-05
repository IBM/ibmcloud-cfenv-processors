package com.ibm.boot.autoconfiguration.kafka;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.converter.Jackson2JavaTypeMapper;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;


public class JsonBeanPostProcessor implements BeanPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(JsonBeanPostProcessor.class);

    private final JSONPropertiesMapper jsonPropertiesMapper;
    private final IBMKafkaProperties ibmKafkaProperties;

    public JsonBeanPostProcessor(JSONPropertiesMapper jsonPropertiesMapper, IBMKafkaProperties ibmKafkaProperties) {
        this.jsonPropertiesMapper = jsonPropertiesMapper;
        this.ibmKafkaProperties = ibmKafkaProperties;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        String[] trustedPackages = ibmKafkaProperties.getSubpackages();
        Map<String, Class<?>> mappings = jsonPropertiesMapper.getClassMapping();

        Object result = bean;
        if (trustedPackages.length > 0) {

            if (bean instanceof DefaultKafkaProducerFactory) {
                DefaultKafkaProducerFactory producerFactory = (DefaultKafkaProducerFactory) bean;
                Map<String, Object> configs = new HashMap<>(producerFactory.getConfigurationProperties());
                configs.remove(VALUE_SERIALIZER_CLASS_CONFIG);
                configs.remove(KEY_SERIALIZER_CLASS_CONFIG);

                DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
                typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
                typeMapper.addTrustedPackages(trustedPackages);
                typeMapper.setIdClassMapping(mappings);

                JsonSerializer<Object> valueSerializer = new JsonSerializer<>();
                valueSerializer.setTypeMapper(typeMapper);

                logger.info("Configuring a DefaultKafkaProducerFactory with configs = [{}]", configs);
                logger.info("trustedPackages = [{}] mappings = [{}]", trustedPackages, mappings);
                result = new DefaultKafkaProducerFactory(configs, new StringSerializer(), valueSerializer);
            } else if (bean instanceof DefaultKafkaConsumerFactory) {
                DefaultKafkaConsumerFactory consumerFactory = (DefaultKafkaConsumerFactory) bean;
                Map<String, Object> configs = new HashMap<>(consumerFactory.getConfigurationProperties());
                configs.remove(VALUE_DESERIALIZER_CLASS_CONFIG);
                configs.remove(KEY_DESERIALIZER_CLASS_CONFIG);

                DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
                typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
                typeMapper.addTrustedPackages(trustedPackages);
                typeMapper.setIdClassMapping(mappings);

                JsonDeserializer<Object> valueDeserializer = new JsonDeserializer<>();
                valueDeserializer.addTrustedPackages(trustedPackages);
                valueDeserializer.setTypeMapper(typeMapper);

                logger.info("Configuring a DefaultKafkaConsumerFactory with configs = [{}]", configs);
                logger.info("trustedPackages = [{}] mappings = [{}]", trustedPackages, mappings);
                result = new DefaultKafkaConsumerFactory(configs, new StringDeserializer(), valueDeserializer);
            }
        }
        return result;
    }
}
