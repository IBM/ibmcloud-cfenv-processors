package com.ibm.cfenv.spring.boot.amqp;

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
    public void initSingleRabbitService() throws URISyntaxException, IOException {
        URL url = AmqpCfEnvProcessorTest.class.getClassLoader().getResource("./messages-rabbit-vcap-services.json");
        StringBuilder contentBuilder = new StringBuilder();
        Files.lines(Paths.get(url.toURI()), StandardCharsets.UTF_8).forEach(s -> contentBuilder.append(s));
        String json = contentBuilder.toString();
        CfEnvTestUtils.mockVcapServicesFromString(json);

        CfEnv cfEnv = new CfEnv();
        cfService = cfEnv.findServiceByLabel("messages-for-rabbitmq");
    }

    @Test
    public void validCFService_accept_serviceAccepted() {
        assertThat(amqpCfEnvProcessor.accept(cfService)).isEqualTo(true);
    }

    @Test
    public void validCFService_propertiesProcessed_correctHostPortUsernameAndPassword() {
        CfCredentials cfCredentials = cfService.getCredentials();
        Map<String, Object> properties = new HashMap<>();
        amqpCfEnvProcessor.process(cfCredentials, properties);

        assertThat(properties.get("spring.rabbitmq.host")).isEqualTo("3633348c-ceb4-4c9d-ac40-b87081574342.blijti4d0v0nkr55oei0.databases.appdomain.cloud");
        assertThat(properties.get("spring.rabbitmq.port")).isEqualTo(31610);
        assertThat(properties.get("spring.rabbitmq.password")).isEqualTo("some_password");
        assertThat(properties.get("spring.rabbitmq.username")).isEqualTo("some_username");
    }

    @Test
    public void validCFService_propertiesProcessed_CorrectSslSetup() {
        CfCredentials cfCredentials = cfService.getCredentials();
        Map<String, Object> properties = new HashMap<>();
        amqpCfEnvProcessor.process(cfCredentials, properties);

        assertThat(properties.get("sslcontext.contexts.amqp.trustedcert")).isEqualTo("base_64_cert");

        assertThat(properties.get("sslcontext.enabled")).isNull();
        assertThat(properties.get("spring.rabbitmq.ssl.enabled")).isNull();
        assertThat(properties.get("cfenv.processor.icdamqp.enabled")).isNull();
        assertThat(properties.get("cfenv.processor.icdamqp.sslcontext")).isNull();

    }

    @Test
    public void validCFService_getProperties_correctServiceNameAndPrefixes() {
        CfEnvProcessorProperties processorProperties = amqpCfEnvProcessor.getProperties();
        assertThat(processorProperties.getServiceName()).isEqualTo("rabbitMQ");
        assertThat(processorProperties.getPropertyPrefixes()).isEqualTo("sslcontext,spring.rabbitmq");
    }
}