package com.ibm.cfenv.spring.boot.elasticsearch;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.env.Environment;

import io.pivotal.cfenv.test.AbstractCfEnvTests;

public class ElasticsearchTest extends AbstractCfEnvTests {

    @Test
    public void vcapServicesSetAsEnvVarInPom_appIsLoaded_requiredElasticsearchPropertiesSetCorrectly() {
        Environment environment = getEnvironment();
        assertThat(environment.getProperty("elasticsearch.username")).isEqualTo("elasticsearch_username");
        assertThat(environment.getProperty("elasticsearch.password")).isEqualTo("elasticsearch_password");
        assertThat(environment.getProperty("elasticsearch.host")).isEqualTo("elasticsearch_host");
        assertThat(environment.getProperty("elasticsearch.port")).isEqualTo("30001");
    }

    public Environment getEnvironment() {
        return new SpringApplicationBuilder(TestApp.class).web(WebApplicationType.NONE)
                .bannerMode(Banner.Mode.OFF)
                .run()
                .getEnvironment();
    }

    @SpringBootApplication
    static class TestApp {
    }
}
