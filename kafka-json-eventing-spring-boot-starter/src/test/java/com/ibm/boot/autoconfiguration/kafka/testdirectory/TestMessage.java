package com.ibm.boot.autoconfiguration.kafka.testdirectory;

public class TestMessage {
    private String name;

    public TestMessage() {
    }

    public TestMessage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
