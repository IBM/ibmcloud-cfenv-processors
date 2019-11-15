package com.ibm.cfenv.spring.boot.data.mongodb;

import com.mongodb.MongoClientOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import javax.net.ssl.SSLContext;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoClientOptionsSSLContextBeanCustomizerTest {
    SSLContext sslContext = Mockito.mock(SSLContext.class);
    MongoClientOptionsSSLContextBeanCustomizer mongoClientOptionsCustomizer;
    Map<String, SSLContext> sslContexts = new HashMap<>();

    @BeforeEach
    public void init() {
        sslContexts.put("mongodb", sslContext);
        mongoClientOptionsCustomizer = new MongoClientOptionsSSLContextBeanCustomizer(sslContexts);
    }

    @Test
    public void postProcessBeforeInit() {
        MongoClientOptions mongoClientOptions = mongoClientOptionsCustomizer.postProcessBeforeInit(MongoClientOptions.builder().build());
        assertThat(mongoClientOptions.isSslEnabled()).isTrue();
        assertThat(mongoClientOptions.getSslContext()).isEqualTo(sslContext);
    }

    @Test
    public void postWithNoValidContext() {
        sslContexts.clear();
        MongoClientOptions expectedMongoClientOptions = MongoClientOptions.builder().build();
        assertThat(mongoClientOptionsCustomizer.postProcessBeforeInit(expectedMongoClientOptions))
                .isEqualTo(expectedMongoClientOptions);
    }

    @Test
    public void accepts() {
        assertThat(mongoClientOptionsCustomizer.accepts("", null)).isFalse();
        assertThat(mongoClientOptionsCustomizer.accepts(MongoClientOptions.builder().build(), null)).isTrue();
    }
}