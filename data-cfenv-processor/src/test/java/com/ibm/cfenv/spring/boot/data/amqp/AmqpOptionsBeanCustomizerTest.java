package com.ibm.cfenv.spring.boot.data.amqp;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

class AmqpOptionsBeanCustomizerTest {
    @Test
    public void instantiatedWithANullValue_preAndPostInitIsCalled_originalValuesAreReturned() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        assertThat(new AmqpOptionsBeanCustomizer(null).postProcessAfterInit(cachingConnectionFactory)).isEqualTo(cachingConnectionFactory);
        assertThat(new AmqpOptionsBeanCustomizer(null).postProcessBeforeInit(cachingConnectionFactory)).isEqualTo(cachingConnectionFactory);
    }

    @Test
    public void instantiatedWithAnyValue_postInitIsCalled_originalValuesAreReturned() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        assertThat(new AmqpOptionsBeanCustomizer(new HashMap<>()).postProcessAfterInit(cachingConnectionFactory)).isEqualTo(cachingConnectionFactory);
    }

    @Test
    public void instantiatedWithValidSSLContextMap_preInitIsCalled_configuredSSLContextIsSet() {
        SSLSocketFactory expectedSSLSocketFactory = Mockito.mock(SSLSocketFactory.class);
        SSLContext sslContext = Mockito.mock(SSLContext.class);
        Mockito.when(sslContext.getSocketFactory())
                .thenReturn(expectedSSLSocketFactory);
        HashMap<String, SSLContext> sslContexts = new HashMap<>();
        sslContexts.put("amqp", sslContext);
        SocketFactory actualSocketFactory = new AmqpOptionsBeanCustomizer(sslContexts)
                .postProcessBeforeInit(new CachingConnectionFactory())
                .getRabbitConnectionFactory()
                .getSocketFactory();
        assertThat(actualSocketFactory).isEqualTo(expectedSSLSocketFactory);
    }

    @Test
    public void usingBothValidAndInvalidBeans_acceptIsCalledCorrectly() {
        AmqpOptionsBeanCustomizer amqpOptionsBeanCustomizer = new AmqpOptionsBeanCustomizer(null);
        assertThat(amqpOptionsBeanCustomizer.accepts("", null)).isEqualTo(false);
        assertThat(amqpOptionsBeanCustomizer.accepts(new CachingConnectionFactory(), null)).isEqualTo(true);
    }
}