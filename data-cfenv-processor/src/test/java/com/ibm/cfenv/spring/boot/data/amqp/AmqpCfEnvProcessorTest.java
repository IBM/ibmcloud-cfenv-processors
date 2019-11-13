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
        assertThat(properties.get("spring.rabbitmq.password")).isEqualTo("245bb3b8448f443332070054f027c285ffe1dcf92228422606135f041c7ccfef");
        assertThat(properties.get("spring.rabbitmq.username")).isEqualTo("ibm_cloud_375f3e0f_02c3_4f9d_8a38_445d340129fb");
    }

    @Test
    public void validCFService_propertiesProcessed_CorrectSslSetup() {
        CfCredentials cfCredentials = cfService.getCredentials();
        Map<String, Object> properties = new HashMap<>();
        amqpCfEnvProcessor.process(cfCredentials, properties);

        assertThat(properties.get("spring.rabbitmq.ssl.enabled")).isNull();
        assertThat(properties.get("sslcontext.contexts.amqp.trustedcert")).isEqualTo("LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUREekNDQWZlZ0F3SUJBZ0lKQU5FSDU4eTIva3pITUEwR0NTcUdTSWIzRFFFQkN3VUFNQjR4SERBYUJnTlYKQkFNTUUwbENUU0JEYkc5MVpDQkVZWFJoWW1GelpYTXdIaGNOTVRnd05qSTFNVFF5T1RBd1doY05Namd3TmpJeQpNVFF5T1RBd1dqQWVNUnd3R2dZRFZRUUREQk5KUWswZ1EyeHZkV1FnUkdGMFlXSmhjMlZ6TUlJQklqQU5CZ2txCmhraUc5dzBCQVFFRkFBT0NBUThBTUlJQkNnS0NBUUVBOGxwYVFHemNGZEdxZU1sbXFqZmZNUHBJUWhxcGQ4cUoKUHIzYklrclhKYlRjSko5dUlja1NVY0NqdzRaL3JTZzhublQxM1NDY09sKzF0bys3a2RNaVU4cU9XS2ljZVlaNQp5K3laWWZDa0dhaVpWZmF6UUJtNDV6QnRGV3YrQUIvOGhmQ1RkTkY3Vlk0c3BhQTNvQkUyYVM3T0FOTlNSWlNLCnB3eTI0SVVnVWNJTEpXK21jdlc4MFZ4K0dYUmZEOVl0dDZQUkpnQmhZdVVCcGd6dm5nbUNNR0JuK2wyS05pU2YKd2VvdllEQ0Q2Vm5nbDIrNlc5UUZBRnRXWFdnRjNpRFFENW5sL240bXJpcE1TWDZVRy9uNjY1N3U3VERkZ2t2QQoxZUtJMkZMellLcG9LQmU1cmNuck03bkhnTmMvbkNkRXM1SmVjSGIxZEh2MVFmUG02cHpJeHdJREFRQUJvMUF3ClRqQWRCZ05WSFE0RUZnUVVLMytYWm8xd3lLcytERW9ZWGJIcnV3U3BYamd3SHdZRFZSMGpCQmd3Rm9BVUszK1gKWm8xd3lLcytERW9ZWGJIcnV3U3BYamd3REFZRFZSMFRCQVV3QXdFQi96QU5CZ2txaGtpRzl3MEJBUXNGQUFPQwpBUUVBSmY1ZHZselVwcWFpeDI2cUpFdXFGRzBJUDU3UVFJNVRDUko2WHQvc3VwUkhvNjNlRHZLdzh6Ujd0bFdRCmxWNVAwTjJ4d3VTbDlacUFKdDcvay8zWmVCK25Zd1BveU8zS3ZLdkFUdW5SdmxQQm40RldWWGVhUHNHKzdmaFMKcXNlam1reW9uWXc3N0hSekdPekpINFpnOFVONm1mcGJhV1NzeWFFeHZxa25DcDlTb1RRUDNENjdBeldxYjF6WQpkb3FxZ0dJWjJueENrcDUvRlh4Ri9UTWI1NXZ0ZVRRd2ZnQnk2MGpWVmtiRjdlVk9XQ3YwS2FOSFBGNWhycWJOCmkrM1hqSjcvcGVGM3hNdlRNb3kzNURjVDNFMlplU1Zqb3VaczE1Tzkwa0kzazJkYVMyT0hKQUJXMHZTajRuTHoKK1BRenAvQjljUW1PTzhkQ2UwNDlRM29hVUE9PQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0tCgo=");

        assertThat(properties.get("sslcontext.enabled")).isNull();
        assertThat(properties.get("cfenv.processor.icdamqp.enabled")).isNull();
        assertThat(properties.get("cfenv.processor.icdamqp.sslcontext")).isNull();

    }

    @Test
    public void validCFService_getProperties_correctServiceNameAndPrefixes() {
        CfEnvProcessorProperties processorProperties = amqpCfEnvProcessor.getProperties();
        assertThat(processorProperties.getServiceName()).isEqualTo("rabbitMQ");
        assertThat(processorProperties.getPropertyPrefixes()).isEqualTo("cfenv.processor.icdamqp,sslcontext,spring.rabbitmq");
    }
}