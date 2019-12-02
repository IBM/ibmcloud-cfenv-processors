package com.ibm.boot.autoconfiguration.kafka;


import com.ibm.boot.autoconfiguration.kafka.testdirectory.TestMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.test.assertj.KafkaConditions;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.ContainerTestUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

//@Configuration
//@RunWith(SpringRunner.class)
//@EnableAutoConfiguration
//@SpringBootTest(
//        classes = {JSONStreamsAutoConfiguration.class},
//        properties = {
//                "ibm.spring.kafka.subpackages=com.ibm.boot.autoconfiguration.kafka.testdirectory"})
public class SpringKafkaTests {

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, true, 2,
            new String[]{"test"});

    private final BlockingQueue<ConsumerRecord<String, TestMessage>> records = new LinkedBlockingQueue<>();

    private KafkaMessageListenerContainer<String, TestMessage> container;

    @Autowired
    DefaultKafkaProducerFactory defaultKafkaProducerFactory;

    @Autowired
    DefaultKafkaConsumerFactory defaultKafkaConsumerFactory;

    @Autowired
    StringJsonMessageConverter stringJsonMessageConverter;

    @Autowired
    JsonBeanPostProcessor jsonBeanPostProcessor;

//    @Bean
//    public DefaultKafkaConsumerFactory defaultKafkaConsumerFactory(JsonBeanPostProcessor jsonBeanPostProcessor) {
//        Map<String, Object> consumerProperties = KafkaTestUtils.consumerProps(
//                "sender",
//                "false",
//                embeddedKafka.getEmbeddedKafka());
//        DefaultKafkaConsumerFactory defaultKafkaConsumerFactory = new DefaultKafkaConsumerFactory(consumerProperties);
//        return (DefaultKafkaConsumerFactory) jsonBeanPostProcessor.postProcessAfterInitialization(defaultKafkaConsumerFactory, null);
//    }

//    @Bean
//    public DefaultKafkaProducerFactory defaultKafkaProducerFactory(JsonBeanPostProcessor jsonBeanPostProcessor) {
//        Map<String, Object> producerProperties = KafkaTestUtils.producerProps(embeddedKafka.getEmbeddedKafka());
//        DefaultKafkaProducerFactory producerFactory = new DefaultKafkaProducerFactory(producerProperties);
//        return (DefaultKafkaProducerFactory) jsonBeanPostProcessor.postProcessAfterInitialization(producerFactory, null);
//    }

//    @Bean
//    public JsonBeanPostProcessor jsonBeanPostProcessor(JSONPropertiesMapper jsonPropertiesMapper) {
//        return new JsonBeanPostProcessor(jsonPropertiesMapper);
//    }


    @Before
    public void setUpConsumerTemplate() {
        ContainerProperties containerProperties = new ContainerProperties(new String[]{"test"});
        containerProperties.setGroupId("consumer");
        container = new KafkaMessageListenerContainer<>(defaultKafkaConsumerFactory, containerProperties);
        container.setupMessageListener((MessageListener<String, TestMessage>) record -> records.add(record));
        container.start();

        int partitionsPerTopic = embeddedKafka.getEmbeddedKafka().getPartitionsPerTopic() * new String[]{"test"}.length;
        ContainerTestUtils.waitForAssignment(container, partitionsPerTopic);
    }

    @After
    public void tearDown() {
        container.stop();
        records.clear();
    }

//    @Test
    public void setupConsumerAndProducer_sendTestMessage_testMessageIsSerializedCorrectly() throws InterruptedException {
        sendTestMessage("test message");

        ConsumerRecord<String, TestMessage> received = records.poll(10, TimeUnit.SECONDS);
        assertThat(received).has(KafkaConditions.key(null));
        TestMessage testMessage = received.value();
        assertThat(testMessage.getName()).isEqualTo("test message");
    }

    private void sendTestMessage(String testMessage) {
//        Object o = defaultKafkaProducerFactory.getConfigurationProperties().get("spring.json.type.mapping");
//        Map<String, Object> producerProperties = KafkaTestUtils.producerProps(embeddedKafka.getEmbeddedKafka());
//        producerProperties.put("value.serializer", JsonSerializer.class);
//        producerProperties.put("key.serializer", StringSerializer.class);
//        producerProperties.put("spring.json.type.mapping", o);
//        ProducerFactory<String, TestMessage> producerFactory = new DefaultKafkaProducerFactory<String, TestMessage>(producerProperties);

        KafkaTemplate kafkaTemplate = new KafkaTemplate(defaultKafkaProducerFactory);
        kafkaTemplate.setDefaultTopic(new String[]{"test"}[0]);
        kafkaTemplate.setMessageConverter(stringJsonMessageConverter);
        kafkaTemplate.send("test", new TestMessage(testMessage));
    }
}
