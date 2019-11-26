package com.ibm.boot.autoconfiguration.kafka;


import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AvroSerializerTest {
    AvroSerializer avroSerializer = new AvroSerializer();

    @Test
    public void serializeCorrect() {
        Map<String, Object> configs = new HashMap<>();
        configs.put("", "");
        avroSerializer.configure(configs, false);
        User user = new User();
        user.setName("George Foster");
        byte[] b1 = avroSerializer.serialize(null, null, user);
        assertThat(b1).isNotNull();
        byte[] b2 = avroSerializer.serialize(null, null, user);
        assertThat(b1).containsOnly(b2);
    }
}