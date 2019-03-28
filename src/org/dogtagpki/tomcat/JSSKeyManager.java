/* BEGIN COPYRIGHT BLOCK
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Copyright (C) 2017 Red Hat, Inc.
 * All rights reserved.
 * END COPYRIGHT BLOCK */

package org.dogtagpki.tomcat;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedKeyManager;

import org.mozilla.jss.CryptoManager;
import org.mozilla.jss.crypto.ObjectNotFoundException;
import org.mozilla.jss.netscape.security.util.Utils;
import org.mozilla.jss.pkcs11.PK11PrivKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.security.x509.X509CertImpl;

public class JSSKeyManager extends X509ExtendedKeyManager {

    final static Logger logger = LoggerFactory.getLogger(JSSKeyManager.class);

    String serverAlias;

    public JSSKeyManager(String alias) {
        this.serverAlias = alias;
    }

    @Override
    public String chooseClientAlias(String[] keyTypes, Principal[] issuers, Socket socket) {
        logger.debug("JSSKeyManager: chooseClientAlias()");

        logger.debug("JSSKeyManager: key types:");
        for (String keyType : keyTypes) {
            logger.debug("JSSKeyManager: - " + keyType);
        }

        if (issuers != null) {
            logger.debug("JSSKeyManager: issuers:");
            for (Principal issuer : issuers) {
                logger.debug("JSSKeyManager: - " + issuer.getName());
            }
        }

        return null;  // not implemented
    }

    @Override
    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        logger.debug("JSSKeyManager: chooseServerAlias()");
        logger.debug("JSSKeyManager: key type: " + keyType);

        if (issuers != null) {
            logger.debug("JSSKeyManager: issuers:");
            for (Principal issuer : issuers) {
                logger.debug("JSSKeyManager: - " + issuer.getName());
            }
        }

        return serverAlias;
    }

    @Override
    public String chooseEngineServerAlias(String keyType, Principal[] issuers, SSLEngine engine) {
        logger.debug("JSSKeyManager: chooseEngineServerAlias()");
        logger.debug("JSSKeyManager: key type: " + keyType);

        if (issuers != null) {
            logger.debug("JSSKeyManager: issuers:");
            for (Principal issuer : issuers) {
                logger.debug("JSSKeyManager: - " + issuer.getName());
            }
        }

        return serverAlias;
    }

    @Override
    public X509Certificate[] getCertificateChain(String alias) {

        logger.debug("JSSKeyManager: getCertificateChain(" + alias + ")");

        try {
            CryptoManager cm = CryptoManager.getInstance();
            org.mozilla.jss.crypto.X509Certificate cert = cm.findCertByNickname(alias);

            org.mozilla.jss.crypto.X509Certificate[] chain = cm.buildCertificateChain(cert);
            logger.debug("JSSKeyManager: cert chain:");

            Collection<X509Certificate> list = new ArrayList<>();
            for (org.mozilla.jss.crypto.X509Certificate c : chain) {
                logger.debug("JSSKeyManager: - " + c.getSubjectDN());
                list.add(new X509CertImpl(c.getEncoded()));
            }

            return list.toArray(new X509Certificate[list.size()]);

        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String[] getClientAliases(String keyType, Principal[] issuers) {
        logger.debug("JSSKeyManager: getClientAliases()");
        logger.debug("JSSKeyManager: key type: " + keyType);

        if (issuers != null) {
            logger.debug("JSSKeyManager: issuers:");
            for (Principal issuer : issuers) {
                logger.debug("JSSKeyManager: - " + issuer.getName());
            }
        }

        return null;  // not implemented
    }

    @Override
    public PrivateKey getPrivateKey(String alias) {

        logger.debug("JSSKeyManager: getPrivateKey(" + alias + ")");

        try {
            CryptoManager cm = CryptoManager.getInstance();
            org.mozilla.jss.crypto.X509Certificate cert = cm.findCertByNickname(alias);
            PrivateKey privateKey = cm.findPrivKeyByCert(cert);

            logger.debug("JSSKeyManager: class: " + privateKey.getClass().getName());

            if (privateKey instanceof PK11PrivKey) {
                PK11PrivKey pk11PrivKey = (PK11PrivKey) privateKey;

                String keyID = Utils.HexEncode(pk11PrivKey.getUniqueID());
                logger.debug("JSSKeyManager: key ID: " + keyID);
            }

            return privateKey;

        } catch (ObjectNotFoundException e) {
            logger.debug("JSSKeyManager: key not found: " + alias);
            return null;

        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String[] getServerAliases(String keyType, Principal[] issuers) {
        logger.debug("JSSKeyManager: getServerAliases()");
        logger.debug("JSSKeyManager: key type: " + keyType);

        if (issuers != null) {
            logger.debug("JSSKeyManager: issuers:");
            for (Principal issuer : issuers) {
                logger.debug("JSSKeyManager: - " + issuer.getName());
            }
        }

        return new String[] { serverAlias };
    }
}
