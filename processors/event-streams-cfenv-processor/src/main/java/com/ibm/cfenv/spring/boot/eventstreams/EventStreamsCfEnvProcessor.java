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

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.core.CfService;
import io.pivotal.cfenv.spring.boot.CfEnvProcessor;
import io.pivotal.cfenv.spring.boot.CfEnvProcessorProperties;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class EventStreamsCfEnvProcessor implements CfEnvProcessor {
    private static final Logger LOG = Logger.getLogger(EventStreamsCfEnvProcessor.class.getName());

    @Override
    public boolean accept(CfService service) {
        boolean match = service.existsByLabelStartsWith("messagehub");
        LOG.info("Match [" + match + "] to service " + service.toString());
        return match;
    }

    @Override
    public void process(CfCredentials cfCredentials, Map<String, Object> properties) {
        List<String> kafka_brokers_sasl = (List<String>) cfCredentials.getMap().get("kafka_brokers_sasl");
        String brokers = kafka_brokers_sasl.stream().collect(Collectors.joining(","));
        String password = cfCredentials.getPassword();

        properties.put("ibm.spring.event-streams.password", password);
        properties.put("spring.kafka.bootstrap-servers", brokers);
    }

    @Override
    public CfEnvProcessorProperties getProperties() {
        return CfEnvProcessorProperties.builder()
                .serviceName("event-streams")
                .propertyPrefixes("ibm.spring.event-streams,spring.kafka")
                .build();
    }
}
