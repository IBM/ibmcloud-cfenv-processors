package com.ibm.sslcontext;


import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.UUID;

public class Base64TrustingTrustManager implements X509TrustManager {
  private KeyStore ks;
  private X509Certificate cert;
  private X509TrustManager trustManager;

  private void setTrustManager() throws IOException, GeneralSecurityException {
    TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    tmf.init(ks);
    for (TrustManager tm : tmf.getTrustManagers()) {
      if (tm instanceof X509TrustManager) {
        trustManager = (X509TrustManager) tm;
        break;
      }
    }
    if (trustManager == null) {
      throw new GeneralSecurityException("No X509TrustManager found");
    }
  }

  public Base64TrustingTrustManager(String cert) throws IOException, GeneralSecurityException {
    ks = KeyStore.getInstance(KeyStore.getDefaultType());
    // Note: KeyStore requires it be loaded even if you don't load anything into it:
    ks.load(null);
    setTrustManager();
    addCert(Base64.getDecoder().decode(cert));
  }

  private void addCert(byte[] certbytes) throws IOException, GeneralSecurityException {
    CertificateFactory cf = CertificateFactory.getInstance("X509");
    InputStream is = new ByteArrayInputStream(certbytes);
    cert = (X509Certificate) cf.generateCertificate(is);
    ks.setCertificateEntry(UUID.randomUUID().toString(), cert);
    setTrustManager();
  }

  public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
  }

  public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    trustManager.checkServerTrusted(chain, authType);
  }

  public X509Certificate[] getAcceptedIssuers() {
    return new X509Certificate[] { cert };
  }

}
