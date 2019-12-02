package com.ibm.boot.autoconfiguration.kafka;


import org.apache.avro.specific.SpecificRecordBase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

class AvroDeserializerTest {
    AvroDeserializer avroDeserializer = new AvroDeserializer();

    @Test
    public void noheader_deserializeCorrectly() {
        List<Class<? extends SpecificRecordBase>> classes = Arrays.asList(User.class);
        Map<String, Object> configs = new HashMap<>();
        configs.put("classes", classes);
        avroDeserializer.configure(configs, false);

        User expectedUser = new User();
        expectedUser.setName(UUID.randomUUID().toString());
        User actualUser = (User) avroDeserializer.deserialize(null, new AvroSerializer().serialize(null, null, expectedUser));
        Assertions.assertThat(expectedUser).isEqualTo(actualUser);
    }

//    @Test
    public void headerValue_deserializeCorrectly() {
        AvroSerializer avroSerializer = new AvroSerializer();
        User expectedUser = new User();
        expectedUser.setName(UUID.randomUUID().toString());
        User actualUser = (User) avroDeserializer.deserialize(null, avroSerializer.serialize(null, null, expectedUser));
        Assertions.assertThat(expectedUser).isEqualTo(actualUser);
    }

}