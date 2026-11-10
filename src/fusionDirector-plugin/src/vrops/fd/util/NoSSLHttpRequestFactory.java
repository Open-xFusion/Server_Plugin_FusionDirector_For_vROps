/*
 * Copyright (c) xFusion Digital Technologies Co., Ltd. 2019-2021. All rights reserved.
 */

package vrops.fd.util;

import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

/**
 * NoSSLHttpRequestFactory
 *
 * @since 2019-02-18
 */
public class NoSSLHttpRequestFactory extends SimpleClientHttpRequestFactory {
    private static final Logger LOGGER = Logger.getLogger(NoSSLHttpRequestFactory.class.getSimpleName());

    static {
        try {
            SSLUtil.turnOffSslChecking();
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            LOGGER.warning(e.getMessage());
        }
    }

    @Override
    protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
        if (connection instanceof HttpsURLConnection) {
            ((HttpsURLConnection) connection)
                    .setHostnameVerifier(
                            new HostnameVerifier() {
                                public boolean verify(String s, SSLSession sslSession) {
                                    return true;
                                }
                            });
        }
        super.prepareConnection(connection, httpMethod);
    }

    static class SSLUtil {
        private static final TrustManager[] UNQUESTIONING_TRUST_MANAGER =
                new TrustManager[] {
                    new X509ExtendedTrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType)
                                throws CertificateException {

                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType)
                                throws CertificateException {

                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket)
                                throws CertificateException {

                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket)
                                throws CertificateException {

                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
                                throws CertificateException {

                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
                                throws CertificateException {

                        }
                    }
                };

        private SSLUtil() {
            throw new UnsupportedOperationException("Do not instantiate libraries.");
        }

        /**
         * turnOffSslChecking 关闭ssl检查·
         *
         * @throws NoSuchAlgorithmException 密钥算法异常
         * @throws KeyManagementException 密钥管理异常
         */
        public static void turnOffSslChecking() throws NoSuchAlgorithmException, KeyManagementException {
            final SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, UNQUESTIONING_TRUST_MANAGER, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }

        /**
         * turnOnSslChecking 开启ssl检查·
         *
         * @throws KeyManagementException 秘钥管理异常
         * @throws NoSuchAlgorithmException 加密算法异常
         */
        public static void turnOnSslChecking() throws KeyManagementException, NoSuchAlgorithmException {
            SSLContext.getInstance("SSL").init(null, null, null);
        }
    }
}
