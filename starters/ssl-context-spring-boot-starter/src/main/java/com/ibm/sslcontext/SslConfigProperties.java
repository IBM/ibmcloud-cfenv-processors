package com.ibm.sslcontext;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "sslcontext")
public class SslConfigProperties {
    private Map<String, SSLConfig> contexts = new HashMap<>();

    public Map<String, SSLConfig> getContexts() {
        return contexts;
    }

    public void setContexts(Map<String, SSLConfig> contexts) {
        this.contexts = contexts;
    }

    public static final class SSLConfig {
        private String trustedCert;

        public String getTrustedCert() {
            return trustedCert;
        }

        public void setTrustedCert(String trustedCert) {
            this.trustedCert = trustedCert;
        }
    }
}