/*
 * Copyright (c) xFusion Digital Technologies Co., Ltd. 2019-2021. All rights reserved.
 */

package vrops.fd.util;

import sun.security.x509.X509CertImpl;

import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

/**
 * SSLHttpRequestFactory
 *
 * @since 2019-02-18
 */
public class SSLHttpRequestFactory extends SimpleClientHttpRequestFactory {
    private static final Logger logger = Logger.getLogger(SSLHttpRequestFactory.class.getSimpleName());

    private ThumbprintTrustManager[] trustManagers = new ThumbprintTrustManager[] {new ThumbprintTrustManager()};

    /**
     * SSL请求工厂构造函数
     *
     * @param certPath 证书路径
     * @throws Exception 异常
     */
    public SSLHttpRequestFactory(String certPath) throws Exception {
        try (InputStream inputStream = new FileInputStream(certPath)) {
            String thumbPrint = ThumbprintTrustManager.getThumbprint(new X509CertImpl(inputStream));
            ThumbprintTrustManager manager = new ThumbprintTrustManager();
            manager.addThumbprint(thumbPrint);
            trustManagers = new ThumbprintTrustManager[]{manager};
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

    @Override
    protected HttpURLConnection openConnection(URL url, Proxy proxy) throws IOException {
        HttpURLConnection httpURLConnection = super.openConnection(url, proxy);
        if (httpURLConnection instanceof HttpsURLConnection) {
            try {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustManagers, new java.security.SecureRandom());
                SSLSocketFactory ssf = sslContext.getSocketFactory();
                ((HttpsURLConnection) httpURLConnection)
                        .setHostnameVerifier(
                                new HostnameVerifier() {
                                    public boolean verify(String s, SSLSession sslSession) {
                                        return true;
                                    }
                                });
                ((HttpsURLConnection) httpURLConnection).setSSLSocketFactory(ssf);
            } catch (NoSuchAlgorithmException | KeyManagementException ex) {
                logger.warning(ex.getMessage());
            }
        }
        return httpURLConnection;
    }
}
