package org.apache.tomcat.util.net;

import java.util.List;
import java.util.Set;

import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;

import org.apache.juli.logging.Log;

public class SSLUtilBase implements SSLUtil {

    protected SSLHostConfigCertificate certificate;

    protected SSLUtilBase(SSLHostConfigCertificate certificate) {
    }

    public KeyManager[] getKeyManagers() throws Exception {
        return null;
    }

    public TrustManager[] getTrustManagers() throws Exception {
        return null;
    }

    protected Set<String> getImplementedProtocols() {
        return null;
    }

    protected Set<String> getImplementedCiphers() {
        return null;
    }

    protected Log getLog() {
        return null;
    }

    protected boolean isTls13RenegAuthAvailable() {
        return false;
    }

    protected SSLContext createSSLContextInternal(List<String> negotiableProtocols) throws Exception {
        return null;
    }
}
