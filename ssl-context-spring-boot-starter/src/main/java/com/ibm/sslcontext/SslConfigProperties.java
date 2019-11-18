package com.ibm.sslcontext;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties("sslcontext")
public class SslConfigProperties {
    private Map<String, SSLContext> contexts;

    public Map<String, SSLContext> getContexts() {
        return contexts;
    }

    public void setContexts(Map<String, SSLContext> contexts) {
        this.contexts = contexts;
    }

    public static final class SSLContext {
        private String trustedCert;

        public String getTrustedCert() {
            return trustedCert;
        }

        public void setTrustedCert(String trustedCert) {
            this.trustedCert = trustedCert;
        }
    }
}