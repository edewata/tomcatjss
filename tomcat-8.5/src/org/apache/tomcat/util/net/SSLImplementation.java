package org.apache.tomcat.util.net;

import javax.net.ssl.SSLSession;

public class SSLImplementation {

    public SSLSupport getSSLSupport(SSLSession session) {
        return null;
    }

    public SSLUtil getSSLUtil(SSLHostConfigCertificate certificate) {
        return null;
    }

    public boolean isAlpnSupported() {
        return false;
    }
}
