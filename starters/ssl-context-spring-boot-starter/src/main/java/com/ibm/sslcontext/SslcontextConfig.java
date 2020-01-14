package com.ibm.sslcontext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties(SslConfigProperties.class)
public class SslcontextConfig {
    private static final Logger logger = LoggerFactory.getLogger(SslcontextConfig.class);

    @Autowired
    SslConfigProperties sslConfigProperties;

    @Bean
    public Map<String, SSLContext> contextMap() {
        logger.debug("Starting to configure ssl certs for = [{}]", sslConfigProperties.getContexts().keySet());

        Map<String, SSLContext> contextMap = new HashMap<>();
        if (!sslConfigProperties.getContexts().isEmpty()) {
            contextMap = sslConfigProperties.getContexts().entrySet().stream()
                    .filter(entry -> {
                        String cert = entry.getValue().getTrustedCert();
                        boolean empty = StringUtils.isEmpty(cert);
                        logger.info("The SSLContext for cert [{}] is empty = [{}]", entry.getKey(), empty);
                        return !empty;
                    })
                    .collect(Collectors.toMap((Function<? super Map.Entry<String, SslConfigProperties.SSLContext>, String>) entry -> entry.getKey(), (Function<? super Map.Entry<String, SslConfigProperties.SSLContext>, SSLContext>) entry -> {
                        try {
                            logger.info("Configuring ssl context for key = [{}] with value =[{}]", entry.getKey(), entry.getValue() == null ? null : entry.getValue().getTrustedCert());
                            String trustedCert = entry.getValue().getTrustedCert();
                            Base64TrustingTrustManager tm = new Base64TrustingTrustManager(trustedCert);
                            SSLContext ctx = SSLContext.getInstance("TLS");
                            ctx.init(null, new TrustManager[]{tm}, null);
                            return ctx;
                        } catch (Exception e) {
                            logger.error("Error configuring ssl context for key = [{}] with value =[{}] exception = [{}]", entry.getKey(), entry.getValue(), e);
                            throw new RuntimeException("Unable to create SSLContext using supplied cert for context " + entry.getKey(), e);
                        }
                    }));
        }
        return contextMap;
    }
}
