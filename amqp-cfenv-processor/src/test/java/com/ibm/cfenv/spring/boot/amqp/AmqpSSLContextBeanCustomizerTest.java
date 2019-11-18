package com.ibm.cfenv.spring.boot.amqp;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.util.HashMap;

class AmqpSSLContextBeanCustomizerTest {
    @Test
    public void instantiatedWithANullValue_preAndPostInitIsCalled_originalValuesAreReturned() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        Assertions.assertThat(new AmqpSSLContextBeanCustomizer(null).postProcessAfterInit(cachingConnectionFactory)).isEqualTo(cachingConnectionFactory);
        Assertions.assertThat(new AmqpSSLContextBeanCustomizer(null).postProcessBeforeInit(cachingConnectionFactory)).isEqualTo(cachingConnectionFactory);
    }

    @Test
    public void instantiatedWithAnyValue_postInitIsCalled_originalValuesAreReturned() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        Assertions.assertThat(new AmqpSSLContextBeanCustomizer(new HashMap<>()).postProcessAfterInit(cachingConnectionFactory)).isEqualTo(cachingConnectionFactory);
    }

    @Test
    public void instantiatedWithValidSSLContextMap_preInitIsCalled_configuredSSLContextIsSet() {
        SSLSocketFactory expectedSSLSocketFactory = Mockito.mock(SSLSocketFactory.class);
        SSLContext sslContext = Mockito.mock(SSLContext.class);
        Mockito.when(sslContext.getSocketFactory())
                .thenReturn(expectedSSLSocketFactory);
        HashMap<String, SSLContext> sslContexts = new HashMap<>();
        sslContexts.put("amqp", sslContext);
        SocketFactory actualSocketFactory = new AmqpSSLContextBeanCustomizer(sslContexts)
                .postProcessBeforeInit(new CachingConnectionFactory())
                .getRabbitConnectionFactory()
                .getSocketFactory();
        Assertions.assertThat(actualSocketFactory).isEqualTo(expectedSSLSocketFactory);
    }

    @Test
    public void usingBothValidAndInvalidBeans_acceptIsCalledCorrectly() {
        AmqpSSLContextBeanCustomizer amqpSSLContextBeanCustomizer = new AmqpSSLContextBeanCustomizer(null);
        Assertions.assertThat(amqpSSLContextBeanCustomizer.accepts("", null)).isEqualTo(false);
        Assertions.assertThat(amqpSSLContextBeanCustomizer.accepts(new CachingConnectionFactory(), null)).isEqualTo(true);
    }
}