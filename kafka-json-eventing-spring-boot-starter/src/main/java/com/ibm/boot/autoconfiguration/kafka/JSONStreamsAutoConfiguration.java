package com.ibm.boot.autoconfiguration.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.converter.Jackson2JavaTypeMapper;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Import({JsonBeanPostProcessor.class})
@EnableConfigurationProperties({IBMKafkaProperties.class})
@ConditionalOnProperty(name = "ibm.spring.kafka.subpackages")
public class JSONStreamsAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(JSONStreamsAutoConfiguration.class);

    @Bean
    public StringJsonMessageConverter stringJsonMessageConverter(IBMKafkaProperties IBMKafkaProperties,
                                                                 JSONPropertiesMapper JSONPropertiesMapper) {
        String[] trustedPackages = IBMKafkaProperties.getSubpackages();
        Map<String, Class<?>> mappings = JSONPropertiesMapper.getClassMapping();

        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
        typeMapper.addTrustedPackages(trustedPackages);
        typeMapper.setIdClassMapping(mappings);

        StringJsonMessageConverter converter = new StringJsonMessageConverter();
        converter.setTypeMapper(typeMapper);

        logger.info("converter trustedPackages = [{}] mappings = [{}]", trustedPackages, mappings);
        return converter;
    }

    @Bean
    public JSONPropertiesMapper jsonPropertiesMapper(IBMKafkaProperties IBMKafkaProperties) {
        return new JSONPropertiesMapper(IBMKafkaProperties.getSubpackages());
    }

    @Bean
    @ConditionalOnMissingBean(ConsumerFactory.class)
    public ConsumerFactory<?, ?> kafkaConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(new HashMap<>());
    }

    @Bean
    @ConditionalOnMissingBean(ProducerFactory.class)
    public ProducerFactory<?, ?> kafkaProducerFactory() {
        DefaultKafkaProducerFactory<?, ?> factory = new DefaultKafkaProducerFactory<>(new HashMap<>());
        return factory;
    }

}
