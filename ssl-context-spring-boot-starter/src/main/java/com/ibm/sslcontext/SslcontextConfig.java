package com.ibm.sslcontext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Configuration
@Import(SslConfigProperties.class)
public class SslcontextConfig {

    private static final Logger LOG = Logger.getLogger(SslcontextConfig.class.getName());

    @Autowired
    SslConfigProperties sslConfigProperties;

    @Bean
    public Map<String, SSLContext> contextMap() {
        return sslConfigProperties.getContexts().entrySet().stream()
                .collect(Collectors.toMap((Function<? super Map.Entry<String, SslConfigProperties.SSLContext>, String>) entry -> entry.getKey(), (Function<? super Map.Entry<String, SslConfigProperties.SSLContext>, SSLContext>) entry -> {
                    try {
                        LOG.log(Level.INFO, "Configuring ssl context for " + entry.getKey());
                        String trustedCert = entry.getValue().getTrustedCert();
                        Base64TrustingTrustManager tm = new Base64TrustingTrustManager(trustedCert);
                        SSLContext ctx = SSLContext.getInstance("TLS");
                        ctx.init(null, new TrustManager[]{tm}, null);
                        return ctx;
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, "Error creating SSLContxt for context id " + entry.getKey(), e);
                        throw new RuntimeException("Unable to create SSLContext using supplied cert for context " + entry.getKey(), e);
                    }
                }));
    }
}
