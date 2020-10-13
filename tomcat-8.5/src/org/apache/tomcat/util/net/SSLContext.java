package org.apache.tomcat.util.net;

import java.security.KeyManagementException;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;

public interface SSLContext {

    public void init(KeyManager[] kms, TrustManager[] tms,
            SecureRandom sr) throws KeyManagementException;

    public SSLEngine createSSLEngine();
}
