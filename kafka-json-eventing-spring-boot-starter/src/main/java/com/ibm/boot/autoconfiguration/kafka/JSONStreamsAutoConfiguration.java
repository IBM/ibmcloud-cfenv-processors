package com.ibm.boot.autoconfiguration.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({JsonBeanPostProcessor.class})
@EnableConfigurationProperties({IBMKafkaProperties.class})
@ConditionalOnProperty(name = "ibm.spring.kafka.subpackages")
public class JSONStreamsAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(JSONStreamsAutoConfiguration.class);

    @Bean
    public JSONPropertiesMapper jsonPropertiesMapper(IBMKafkaProperties ibmKafkaProperties) {
        return new JSONPropertiesMapper(ibmKafkaProperties.getSubpackages());
    }
}
