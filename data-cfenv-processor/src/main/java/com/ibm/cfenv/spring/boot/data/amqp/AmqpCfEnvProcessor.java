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
package com.ibm.cfenv.spring.boot.data.amqp;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.core.CfService;
import io.pivotal.cfenv.spring.boot.CfEnvProcessor;
import io.pivotal.cfenv.spring.boot.CfEnvProcessorProperties;

import java.util.List;
import java.util.Map;

public class AmqpCfEnvProcessor implements CfEnvProcessor {

    @Override
    public boolean accept(CfService service) {
        return service.existsByLabelStartsWith("messages-for-rabbitmq");
    }

    @Override
    public void process(CfCredentials cfCredentials, Map<String, Object> properties) {
        Map<String, Object> credentialsData = cfCredentials.getMap();
        Map<String, Object> connection = (Map<String, Object>) credentialsData.get("connection");
        Map<String, Object> amqps = (Map<String, Object>) connection.get("amqps");
        List<Map<String, Object>> hosts = (List<Map<String, Object>>) amqps.get("hosts");
        Map<String, Object> host = hosts.get(0);
        String hostname = (String) host.get("hostname");
        int port = (int) host.get("port");
        properties.put("spring.rabbitmq.host", hostname);
        properties.put("spring.rabbitmq.port", port);

        Map<String, String> authentication = (Map<String, String>) amqps.get("authentication");
        String password = authentication.get("password");
        String username = authentication.get("username");
        properties.put("spring.rabbitmq.password", password);
        properties.put("spring.rabbitmq.username", username);

        Map<String, String> certificate = (Map<String, String>) amqps.get("certificate");
        String certificate_base64 = certificate.get("certificate_base64");
    }

    @Override
    public CfEnvProcessorProperties getProperties() {
        return null;
    }
}
