package com.ibm.boot.autoconfiguration.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ibm.spring.kafka")
public class IBMKafkaProperties {
    private String[] subpackages = {};

    public String[] getSubpackages() {
        return subpackages;
    }

    public void setSubpackages(String[] subpackages) {
        if (subpackages != null) {
            this.subpackages = subpackages;
        }
    }
}
