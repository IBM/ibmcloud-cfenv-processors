package com.ibm.sslcontext;


import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertThrows;

class Base64TrustingTrustManagerTest {
    @Test
    public void validSelfsignedCert_instantiatingTrustManager_illegalArgumentExceptionThrown() throws IOException, GeneralSecurityException, URISyntaxException {
        String filename = "selfSignedHarmlessKeystore.jks";
        URI url = this.getClass().getClassLoader().getResource(filename).toURI();
        File file = new File(url);
        char[] pwdArray = "password".toCharArray();
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(new FileInputStream(file), pwdArray);
        Certificate certificate = keyStore.getCertificate("selfsigned");
        byte[] encoded = certificate.getEncoded();
        String validCert = Base64.getEncoder().encodeToString(encoded);
        new Base64TrustingTrustManager(validCert);
    }

    @Test()
    public void invalidCert_instantiatingTrustManager_illegalArgumentExceptionThrown() {
        assertThrows(IllegalArgumentException.class,
                () -> new Base64TrustingTrustManager(null),
                "Cert cannot be null");
    }
}