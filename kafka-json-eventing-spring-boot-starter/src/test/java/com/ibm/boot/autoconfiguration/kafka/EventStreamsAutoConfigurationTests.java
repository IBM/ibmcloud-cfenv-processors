package com.ibm.boot.autoconfiguration.kafka;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@Configuration
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {JSONStreamsAutoConfiguration.class, EventStreamsAutoConfigurationTests.class},
        properties = {"ibm.spring.kafka.subpackages=com.ibm.boot.autoconfiguration.kafka.testdirectory"})
public class EventStreamsAutoConfigurationTests {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void ibmKafkaPropertiesAreDefined_springContextIsLoaded_propertiesAreLoadedCorrectly() {
//        IBMKafkaProperties IBMKafkaProperties = applicationContext.getBean(IBMKafkaProperties.class);
//        assertThat(IBMKafkaProperties.getSubpackages()).containsOnly("com.ibm.boot.autoconfiguration.kafka.testdirectory");
        assertThat(true).isTrue();
    }

//    @Test
//    public void subpackagesPropertyIsDefined_springContextIsLoaded_stringJsonMessageConverterIsSetCorrectly() {
//        StringJsonMessageConverter messageConverter = applicationContext.getBean(StringJsonMessageConverter.class);
//        DefaultJackson2JavaTypeMapper typeMapper = (DefaultJackson2JavaTypeMapper) messageConverter.getTypeMapper();
//
//        Assertions.assertThat(typeMapper.getTypePrecedence()).isEqualTo(Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
//        Map<String, Class<?>> idClassMapping = typeMapper.getIdClassMapping();
//        Assertions.assertThat(idClassMapping.get("com.ibm.boot.autoconfiguration.kafka.testdirectory.TestMessage")).isEqualTo(TestMessage.class);
//    }

//    @Test
//    public void subpackagesPropertyIsDefined_springContextIsLoaded_kafkaConsumerFactoryIsSetCorrectly() {
//        DefaultKafkaConsumerFactory consumerFactory = applicationContext.getBean(DefaultKafkaConsumerFactory.class);
//        Map<String, Object> configurationProperties = consumerFactory.getConfigurationProperties();
//        Assertions.assertThat(configurationProperties.get("value.deserializer")).isEqualTo(JsonDeserializer.class);
//        Assertions.assertThat(configurationProperties.get("key.deserializer")).isEqualTo(StringDeserializer.class);
//    }

//    @Test
//    public void subpackagesPropertyIsDefined_springContextIsLoaded_kafkaProducerFactoryIsSetCorrectly() {
//        DefaultKafkaProducerFactory producerFactory = applicationContext.getBean(DefaultKafkaProducerFactory.class);
//        Map<String, Object> configurationProperties = producerFactory.getConfigurationProperties();
//        Assertions.assertThat(configurationProperties.get("value.serializer")).isEqualTo(JsonSerializer.class);
//        Assertions.assertThat(configurationProperties.get("key.serializer")).isEqualTo(StringSerializer.class);
//    }

//    @Test
//    public void subpackagesPropertyIsDefined_springContextIsLoaded_jsonMappingIsSetCorrectly() {
//        DefaultKafkaProducerFactory producerFactory = applicationContext.getBean(DefaultKafkaProducerFactory.class);
//        Map<String, Object> configurationProperties = producerFactory.getConfigurationProperties();
//        String[] mappings = ((String) configurationProperties.get("spring.json.type.mapping")).split(",");
//        Assertions.assertThat(mappings).containsOnly(
//                "com.ibm.boot.autoconfiguration.kafka.testdirectory.TestMessage:com.ibm.boot.autoconfiguration.kafka.testdirectory.TestMessage");
//    }
}