/*
 *   Copyright 2019 IBM Corporation.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.ibm.boot.autoconfiguration.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SimpleKafkaHeaderMapper;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.Assert;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AvroRecordMessageConverter implements RecordMessageConverter {
    private final SimpleKafkaHeaderMapper simpleKafkaHeaderMapper = new SimpleKafkaHeaderMapper();

    @Override
    public Message<?> toMessage(ConsumerRecord<?, ?> record, Acknowledgment acknowledgment, Consumer<?, ?> consumer, Type payloadType) {
//        KafkaMessageHeaders kafkaMessageHeaders = new KafkaMessageHeaders(true, true);
        MessageHeaders messageHeaders = new MessageHeaders(null);
        Map<String, Object> rawHeaders = new HashMap<>();
        rawHeaders.put("avro-classname", payloadType.getTypeName());
        String ttName = record.timestampType() != null ? record.timestampType().name() : null;
        commonHeaders(acknowledgment, consumer, rawHeaders, record.key(), record.topic(), record.partition(),
                record.offset(), ttName, record.timestamp());
        return MessageBuilder.withPayload(record.value())
                .build();
    }

    @Override
    public ProducerRecord fromMessage(Message<?> message, String defaultTopic) {
        MessageHeaders headers = message.getHeaders();
        Object topicHeader = headers.get(KafkaHeaders.TOPIC);
        String topic = defaultTopic;
        if (topicHeader instanceof byte[]) {
            topic = new String(((byte[]) topicHeader), StandardCharsets.UTF_8);
        } else if (topicHeader instanceof String) {
            topic = (String) topicHeader;
        } else if (topicHeader == null) {
            Assert.state(defaultTopic != null, "With no topic header, a defaultTopic is required");
        } else {
            throw new IllegalStateException(KafkaHeaders.TOPIC + " must be a String or byte[], not "
                    + topicHeader.getClass());
        }

        Integer partition = headers.get(KafkaHeaders.PARTITION_ID, Integer.class);
        Object key = headers.get(KafkaHeaders.MESSAGE_KEY);
        Object payload = message.getPayload();
        Long timestamp = headers.get(KafkaHeaders.TIMESTAMP, Long.class);
        RecordHeaders recordHeaders = new RecordHeaders();
        simpleKafkaHeaderMapper.fromHeaders(headers, recordHeaders);
        return new ProducerRecord(topic, partition, timestamp, key, payload, recordHeaders);

    }
}
