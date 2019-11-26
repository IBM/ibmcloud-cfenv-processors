package com.ibm.boot.autoconfiguration.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.concurrent.CountDownLatch;

@Configuration
public class AvroStreamsTestConfiguration {
    @Bean
    public Receiver receiver() {
        return new Receiver();
    }

    public static final class Receiver {
        private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);

        private CountDownLatch latch = new CountDownLatch(1);

        public CountDownLatch getLatch() {
            return latch;
        }

        @KafkaHandler
        @KafkaListener(topics = "target-topic", groupId = "avro")
        public void receive(User user) {
            LOGGER.info("received user='{}'", user.toString());
            latch.countDown();
        }
    }
}

