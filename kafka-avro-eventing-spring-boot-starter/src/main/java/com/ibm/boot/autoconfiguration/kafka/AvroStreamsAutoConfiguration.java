package com.ibm.boot.autoconfiguration.kafka;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.LoggingProducerListener;
import org.springframework.kafka.support.ProducerListener;

import java.util.Map;

@Configuration
@Import({AvroBeanPostProcessor.class})
@EnableConfigurationProperties({IBMKafkaProperties.class, KafkaProperties.class})
//@ConditionalOnProperty(name = "ibm.spring.kafka.schemaLocation")
public class AvroStreamsAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(AvroStreamsAutoConfiguration.class);
    private final KafkaProperties kafkaProperties;

    public AvroStreamsAutoConfiguration(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public AvroRecordMessageConverter avroRecordMessageConverter() {
        return new AvroRecordMessageConverter();
    }

    @Bean
//    @ConditionalOnMissingBean(ProducerListener.class)
    public ProducerListener<Object, Object> kafkaProducerListener() {
        return new LoggingProducerListener<>();
    }

    @Bean
    @ConditionalOnMissingBean(KafkaOperations.class)
    public KafkaTemplate<?, ?> kafkaTemplate(ProducerFactory<Object, Object> kafkaProducerFactory,
                                             ProducerListener<Object, Object> kafkaProducerListener,
                                             AvroRecordMessageConverter avroRecordMessageConverter) {
        KafkaTemplate<Object, Object> kafkaTemplate = new KafkaTemplate<>(kafkaProducerFactory);
        kafkaTemplate.setMessageConverter(avroRecordMessageConverter);
        kafkaTemplate.setProducerListener(kafkaProducerListener);
        kafkaTemplate.setDefaultTopic(this.kafkaProperties.getTemplate().getDefaultTopic());
        return kafkaTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(ConsumerFactory.class)
    public DefaultKafkaConsumerFactory<Object, Object> kafkaConsumerFactory() {
        Map<String, Object> consumerProperties = kafkaProperties.buildConsumerProperties();
        Deserializer<String> keyDeserializer = new StringDeserializer();
        Deserializer<Object> valueDerializer = new AvroDeserializer();

        DefaultKafkaConsumerFactory<Object, Object> defaultKafkaConsumerFactory
                = new DefaultKafkaConsumerFactory(consumerProperties, keyDeserializer, valueDerializer);
        return defaultKafkaConsumerFactory;
    }

    @Bean
    @ConditionalOnMissingBean(ProducerFactory.class)
    public DefaultKafkaProducerFactory<Object, Object> kafkaProducerFactory() {
        Map<String, Object> producerProperties = kafkaProperties.buildProducerProperties();
        Serializer<String> keySerializer = new StringSerializer();
        Serializer<Object> valueSerializer = new AvroSerializer();

        DefaultKafkaProducerFactory<Object, Object> defaultKafkaProducerFactory
                = new DefaultKafkaProducerFactory(producerProperties, keySerializer, valueSerializer);
        return defaultKafkaProducerFactory;
    }

}
