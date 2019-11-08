package com.ibm.cfenv.spring.boot.data;

import com.mongodb.MongoClientOptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.FatalBeanException;
import org.springframework.context.ApplicationContext;

import javax.net.ssl.SSLContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoClientOptionsCustomizerTest {
    SSLContext sslContext = Mockito.mock(SSLContext.class);
    ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
    MongoClientOptionsCustomizer mongoClientOptionsCustomizer;

    @BeforeAll
    public void init() {
        when(applicationContext.getBean("mongo", SSLContext.class)).thenReturn(sslContext);
        mongoClientOptionsCustomizer = new MongoClientOptionsCustomizer("mongo", applicationContext);
    }

    @Test
    void postProcessBeforeInit() {
        MongoClientOptions mongoClientOptions = MongoClientOptions.builder().build();
        MongoClientOptions actual = (MongoClientOptions) mongoClientOptionsCustomizer.postProcessBeforeInit(mongoClientOptions);

        assertThat(actual.isSslEnabled()).isEqualTo(true);
        assertThat(actual.getSslContext()).isEqualTo(sslContext);
    }

    @Test
    public void exception() {
        MongoClientOptions mongoClientOptions = Mockito.mock(MongoClientOptions.class);
        when(mongoClientOptions.getDescription())
                .thenAnswer(invocation -> {
                    throw new Exception();
                });
        assertThrows(FatalBeanException.class, () -> mongoClientOptionsCustomizer.postProcessBeforeInit(mongoClientOptions));
    }
}