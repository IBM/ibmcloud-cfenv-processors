/*
 *
 *    Copyright 2019 IBM Corporation.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */
package com.ibm.boot.autoconfiguration.kafka;

import org.junit.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonBeanPostProcessorTest {

    JSONPropertiesMapper propertiesMapper = Mockito.mock(JSONPropertiesMapper.class);
    IBMKafkaProperties ibmKafkaProperties = Mockito.mock(IBMKafkaProperties.class);
    JsonBeanPostProcessor jsonBeanPostProcessor = new JsonBeanPostProcessor(propertiesMapper, ibmKafkaProperties);

    @Test
    public void producer() {
//        Mockito.when(propertiesMapper.getJsonMapping()).thenReturn("yes");
//        DefaultKafkaProducerFactory<String, Object> bean = (DefaultKafkaProducerFactory<String, Object>) jsonBeanPostProcessor.postProcessAfterInitialization(new DefaultKafkaProducerFactory<>(new HashMap<>()), null);
//        Map<String, Object> configurationProperties = bean.getConfigurationProperties();
//        assertThat(configurationProperties.get("spring.json.type.mapping")).isEqualTo("yes");
//        assertThat(configurationProperties.get(VALUE_SERIALIZER_CLASS_CONFIG)).isEqualTo(JsonSerializer.class);
//        assertThat(configurationProperties.get(KEY_SERIALIZER_CLASS_CONFIG)).isEqualTo(StringSerializer.class);
    }

    @Test
    public void consumer() {
//        DefaultKafkaConsumerFactory<String, Object> bean = (DefaultKafkaConsumerFactory<String, Object>) jsonBeanPostProcessor.postProcessAfterInitialization(new DefaultKafkaConsumerFactory<>(new HashMap<>()), null);
//        Map<String, Object> configurationProperties = bean.getConfigurationProperties();
//        assertThat(configurationProperties.get(VALUE_DESERIALIZER_CLASS_CONFIG)).isEqualTo(JsonDeserializer.class);
//        assertThat(configurationProperties.get(KEY_DESERIALIZER_CLASS_CONFIG)).isEqualTo(StringDeserializer.class);
    }

    @Test
    public void doNothing() {
        Mockito.when(ibmKafkaProperties.getSubpackages()).thenReturn(new String[0]);
        assertThat(jsonBeanPostProcessor.postProcessAfterInitialization("string", "")).isEqualTo("string");
        assertThat(jsonBeanPostProcessor.postProcessBeforeInitialization("string", "")).isEqualTo("string");
    }
}