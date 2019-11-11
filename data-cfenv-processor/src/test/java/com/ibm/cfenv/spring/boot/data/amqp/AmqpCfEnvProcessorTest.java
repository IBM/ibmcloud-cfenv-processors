package com.ibm.cfenv.spring.boot.data.amqp;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.core.CfEnv;
import io.pivotal.cfenv.core.CfService;
import io.pivotal.cfenv.spring.boot.CfEnvProcessorProperties;
import io.pivotal.cfenv.test.CfEnvTestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AmqpCfEnvProcessorTest {
    AmqpCfEnvProcessor amqpCfEnvProcessor = new AmqpCfEnvProcessor();
    CfService cfService;

    @BeforeAll
    public void initRabbitService() throws URISyntaxException, IOException {
        URL url = AmqpCfEnvProcessorTest.class.getClassLoader().getResource("./messages-rabbit-vcap-services.json");
        StringBuilder contentBuilder = new StringBuilder();
        Files.lines(Paths.get(url.toURI()), StandardCharsets.UTF_8).forEach(s -> contentBuilder.append(s));
        String json = contentBuilder.toString();
        CfEnvTestUtils.mockVcapServicesFromString(json);

        CfEnv cfEnv = new CfEnv();
        cfService = cfEnv.findServiceByLabel("messages-for-rabbitmq");
    }

    @Test
    public void doStuff() {
        assertThat(amqpCfEnvProcessor.accept(cfService)).isEqualTo(true);
    }

    @Test
    public void doMoreStuff() {
        CfCredentials cfCredentials = cfService.getCredentials();
        Map<String, Object> properties = new HashMap<>();
        amqpCfEnvProcessor.process(cfCredentials, properties);

        assertThat(properties.get("spring.rabbitmq.host")).isEqualTo("3633348c-ceb4-4c9d-ac40-b87081574342.blijti4d0v0nkr55oei0.databases.appdomain.cloud");
        assertThat(properties.get("spring.rabbitmq.port")).isEqualTo(31610);
        assertThat(properties.get("spring.rabbitmq.password")).isEqualTo("245bb3b8448f443332070054f027c285ffe1dcf92228422606135f041c7ccfef");
        assertThat(properties.get("spring.rabbitmq.username")).isEqualTo("ibm_cloud_375f3e0f_02c3_4f9d_8a38_445d340129fb");
        assertThat(properties.get("spring.rabbitmq.ssl.enabled")).isEqualTo("true");

        assertThat(properties.get("sslcontext.enabled")).isEqualTo("3633348c-ceb4-4c9d-ac40-b87081574342.blijti4d0v0nkr55oei0.databases.appdomain.cloud");
        assertThat(properties.get("sslcontext.contexts.amqp.trustedcert")).isEqualTo(31610);
        assertThat(properties.get("cfenv.processor.icdamqp.enabled")).isEqualTo(true);
        assertThat(properties.get("cfenv.processor.icdamqp.sslcontext")).isEqualTo("amqp");

    }

    @Test
    public void doMoreAndMoreStuff() {
        CfEnvProcessorProperties processorProperties = amqpCfEnvProcessor.getProperties();
        assertThat(processorProperties.getServiceName()).isEqualTo("");
        assertThat(processorProperties.getPropertyPrefixes()).isEqualTo("");
    }
}