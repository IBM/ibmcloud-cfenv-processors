package com.ibm.boot.autoconfiguration.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ibm.spring.kafka")
public class IBMKafkaProperties {
    private String schemaLocation = null;

    public String getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }
}
