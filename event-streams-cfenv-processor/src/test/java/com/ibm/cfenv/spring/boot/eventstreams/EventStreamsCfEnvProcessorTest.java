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
package com.ibm.cfenv.spring.boot.eventstreams;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.cfenv.core.CfService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventStreamsCfEnvProcessorTest {
    EventStreamsCfEnvProcessor eventStreamsCfEnvProcessor = new EventStreamsCfEnvProcessor();
    private CfService service;

    @BeforeAll
    public void initService() throws IOException {
        URL url = EventStreamsCfEnvProcessor.class.getClassLoader().getResource("./vcap.json");
        Map<String, Object> serviceData = (Map<String, Object>) ((List) new ObjectMapper()
                .readValue(url, Map.class)
                .get("messagehub"))
                .get(0);
        service = new CfService(serviceData);
    }

    @Test
    public void accept() {
        assertThat(eventStreamsCfEnvProcessor.accept(service)).isTrue();
    }

    @Test
    public void process() {
        Map<String, Object> properties = new HashMap<>();
        eventStreamsCfEnvProcessor.process(service.getCredentials(), properties);
        assertThat(properties.size()).isEqualTo(2);
        assertThat(properties.get("ibm.spring.event-streams.password")).isEqualTo("event-streams-password");
        assertThat(properties.get("spring.kafka.bootstrap-servers").toString().split(","))
                .containsOnly("broker-1-d2zv4nll2lkn9z93.kafka.svc01.us-south.eventstreams.cloud.ibm.com:9093",
                        "broker-5-d2zv4nll2lkn9z93.kafka.svc01.us-south.eventstreams.cloud.ibm.com:9093",
                        "broker-4-d2zv4nll2lkn9z93.kafka.svc01.us-south.eventstreams.cloud.ibm.com:9093",
                        "broker-2-d2zv4nll2lkn9z93.kafka.svc01.us-south.eventstreams.cloud.ibm.com:9093",
                        "broker-3-d2zv4nll2lkn9z93.kafka.svc01.us-south.eventstreams.cloud.ibm.com:9093",
                        "broker-0-d2zv4nll2lkn9z93.kafka.svc01.us-south.eventstreams.cloud.ibm.com:9093");
    }

    @Test
    public void getProperties() {
        assertThat(eventStreamsCfEnvProcessor.getProperties().getServiceName()).isEqualTo("event-streams");
        assertThat(eventStreamsCfEnvProcessor.getProperties().getPropertyPrefixes()).isEqualTo("ibm.spring.event-streams,spring.kafka");

    }
}