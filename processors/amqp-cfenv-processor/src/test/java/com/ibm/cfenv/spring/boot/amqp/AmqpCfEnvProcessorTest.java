package com.ibm.cfenv.spring.boot.amqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.core.CfService;
import io.pivotal.cfenv.spring.boot.CfEnvProcessorProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AmqpCfEnvProcessorTest {
    AmqpCfEnvProcessor amqpCfEnvProcessor = new AmqpCfEnvProcessor();
    CfService service;
    List<CfService> cfServices;

    @BeforeAll
    public void initSingleRabbitService() throws IOException {
        URL url = AmqpCfEnvProcessorTest.class.getClassLoader().getResource("./messages-rabbit-vcap-services.json");
        Map<String, Object> serviceData = (Map<String, Object>) ((List) new ObjectMapper()
                .readValue(url, Map.class)
                .get("messages-for-rabbitmq"))
                .get(0);
        service = new CfService(serviceData);
    }

    @BeforeAll
    public void initSingleRabbitInAListOfServices() throws IOException {
        URL url = AmqpCfEnvProcessorTest.class.getClassLoader().getResource("./multiple-bindings-vcap-services.json");
        Map<String, List<Map<String, Object>>> map = new ObjectMapper().readValue(url, Map.class);
        cfServices = map.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> serviceData = entry.getValue().get(0);
                    return new CfService(serviceData);
                })
                .filter(cfService -> amqpCfEnvProcessor.accept(cfService))
                .collect(Collectors.toList());
    }


    @Test
    public void multipleServiceInstances_accept_onlyRabbitIsAccepted() {
        cfServices.stream().forEach(cfService -> {
            boolean actual = amqpCfEnvProcessor.accept(cfService);
            if (cfService.getLabel().equalsIgnoreCase("messages-for-rabbitmq")) {
                assertThat(actual).isTrue();
            } else {
                assertThat(actual).isFalse();
            }
        });
    }

    @Test
    public void multipleServiceInstances_accept_rabbitIsProcessedCorrectly() {
        CfCredentials service = cfServices.stream()
                .filter(cfService -> amqpCfEnvProcessor.accept(cfService))
                .collect(Collectors.toList())
                .get(0)
                .getCredentials();
        Map<String, Object> properties = new HashMap<>();
        amqpCfEnvProcessor.process(service, properties);

        assertThat(cfServices.size()).isEqualTo(1);
        assertThat(properties.get("sslcontext.contexts.amqp.trustedcert")).isEqualTo("MIIDfDCCAmSgAwIBAgIJANbFzLaShHP1MA0GCSqGSIb3DQEBCwUAMGwxEDAOBgNVBAYTB1Vua25vd24xEDAOBgNVBAgTB1Vua25vd24xEDAOBgNVBAcTB1Vua25vd24xEDAOBgNVBAoTB1Vua25vd24xEDAOBgNVBAsTB1Vua25vd24xEDAOBgNVBAMTB1Vua25vd24wHhcNMTkxMTIxMDAzNDAxWhcNMjAwMjE5MDAzNDAxWjBsMRAwDgYDVQQGEwdVbmtub3duMRAwDgYDVQQIEwdVbmtub3duMRAwDgYDVQQHEwdVbmtub3duMRAwDgYDVQQKEwdVbmtub3duMRAwDgYDVQQLEwdVbmtub3duMRAwDgYDVQQDEwdVbmtub3duMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArH6mfoPuQoKKT/7V7e6LYuF08rE2IjksKWG8YhPgjDmxkRlweneRVrN93NJ8l0C9H3st7Val+fDbpECQe17FACde8fE4zYcJcPptanpNO4c5UCGkLmh/33fasMpoJyDJobseXaeRnI8poXik7yY1N2v5DvfhkB9BxlHcqpPVi4oaAfXdBkSdHSxtK5Z/1mRqHww80KakX2VX0mXL8b9pAlf3j8RA91T93bbRTpg6pNihpy0UScRhMTDZB8Ps5RE6e/AnYLOOp4dbelAh2HG0pcNHHwkdAh+kae61MNnIuPSgqgmVQY6dlmpRSa7X2B0q/QFoXdW4CgALZaH+G93mzQIDAQABoyEwHzAdBgNVHQ4EFgQUK/xh6cLkXsqdijElSEzHiCGU4yIwDQYJKoZIhvcNAQELBQADggEBAIqRZkEmpy9GlQ/ucNH8bIw0KY2Mz1ddkSohg+yuKBYgP/h1uku5aWgJZdWOKjrhm1e2RryUFgaqylkgBc1AQWEa54TbDMNgmYEmLaKZp+C+6nYHx2UyMToAa7F/exLLcuqmspcmfuS/zMZkHEuyHQS561PuzfHnRlfzdykZPfe2wInSUkWY825OyYjWL6mEAkiYMRYLu7r5+Y7aLqLMIU8GMc7sYUuRDigKLAoaVH2rs0ARzPepHGmHZ2AJ9Z+h3IfQzgBGW+JlwINymGLfsDBqVnlM8HYGPydThGCFOVPTzUH5GFAv6z3EmtWrEOTfSRnvlfFqgPnbbvFoKvhDeGE=");
    }

    @Test
    public void validCFService_accept_serviceAccepted() {
        assertThat(amqpCfEnvProcessor.accept(service)).isEqualTo(true);
    }

    @Test
    public void validCFService_propertiesProcessed_correctHostPortUsernameAndPassword() {
        Map<String, Object> properties = new HashMap<>();
        amqpCfEnvProcessor.process(service.getCredentials(), properties);

        assertThat(properties.get("spring.rabbitmq.host")).isEqualTo("3633348c-ceb4-4c9d-ac40-b87081574342.blijti4d0v0nkr55oei0.databases.appdomain.cloud");
        assertThat(properties.get("spring.rabbitmq.port")).isEqualTo(31610);
        assertThat(properties.get("spring.rabbitmq.password")).isEqualTo("some_password");
        assertThat(properties.get("spring.rabbitmq.username")).isEqualTo("some_username");
    }

    @Test
    public void validCFService_propertiesProcessed_CorrectSslSetup() {
        CfCredentials cfCredentials = service.getCredentials();
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

    @Test
    public void nullCertificate_propertiesProcessed_invalidStateThrown() {
        HashMap<String, Object> host = new HashMap<>();
        host.put("port", 1);
        List<Map<String, Object>> hosts = Arrays.asList(host);
        Map<String, Object> amqps = new HashMap<>();
        amqps.put("hosts", hosts);
        amqps.put("certificate", new HashMap<String, String>());
        amqps.put("authentication", new HashMap<String, String>());
        Map<String, Object> connection = new HashMap<>();
        connection.put("amqps", amqps);
        Map<String, Object> credentialsData = new HashMap<>();
        credentialsData.put("connection", connection);
        CfCredentials cfCredentials = new CfCredentials(credentialsData);
        HashMap<String, Object> properties = new HashMap<>();

        Assertions.assertThrows(IllegalStateException.class, () -> amqpCfEnvProcessor.process(cfCredentials, properties));
    }

}