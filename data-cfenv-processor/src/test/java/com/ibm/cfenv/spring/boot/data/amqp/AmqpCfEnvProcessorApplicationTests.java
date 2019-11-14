package com.ibm.cfenv.spring.boot.data.amqp;

import io.pivotal.cfenv.test.CfEnvTestUtils;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@RunWith(SpringRunner.class)
public class AmqpCfEnvProcessorApplicationTests {

    @BeforeAll
    public void initRabbitService() throws URISyntaxException, IOException {
        URL url = AmqpCfEnvProcessorTest.class.getClassLoader().getResource("./messages-rabbit-vcap-services.json");
        StringBuilder contentBuilder = new StringBuilder();
        Files.lines(Paths.get(url.toURI()), StandardCharsets.UTF_8).forEach(s -> contentBuilder.append(s));
        String json = contentBuilder.toString();
        CfEnvTestUtils.mockVcapServicesFromString(json);
    }

    @Test
    public void contextLoads() {
        ConfigurableApplicationContext run = SpringApplication.run(new Class[]{AmqpOptionsBeanCustomizer.class},
                new String[]{});
        run.start();
    }

}