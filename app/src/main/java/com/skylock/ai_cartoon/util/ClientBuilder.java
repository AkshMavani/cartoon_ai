package com.skylock.ai_cartoon.util;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;

public final class ClientBuilder {

    public static final ClientBuilder INSTANCE = new ClientBuilder();

    private ClientBuilder() {}

    public OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {
            X509TrustManager trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {}

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {}

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
            SSLSocketFactory socketFactory = sslContext.getSocketFactory();

            // Connection specs — support TLS 1.0, 1.1, 1.2, 1.3
            ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
                    .tlsVersions(
                            TlsVersion.TLS_1_3,
                            TlsVersion.TLS_1_2,
                            TlsVersion.TLS_1_1,
                            TlsVersion.TLS_1_0
                    )
                    .allEnabledCipherSuites()
                    .build();

            return new OkHttpClient.Builder()
                    .sslSocketFactory(socketFactory, trustManager)
                    .hostnameVerifier((hostname, session) -> true)
                    // ✅ Fix: Connection pool — prevents stale connections
                    .connectionPool(new ConnectionPool(5, 30, TimeUnit.SECONDS))
                    // ✅ Fix: Support all connection specs including cleartext
                    .connectionSpecs(Arrays.asList(
                            spec,
                            ConnectionSpec.CLEARTEXT
                    ))
                    // ✅ Fix: Retry on connection failure
                    .retryOnConnectionFailure(true)
                    // ✅ Fix: Follow redirects
                    .followRedirects(true)
                    .followSslRedirects(true)
                    // ✅ Fix: Timeouts
                    .connectTimeout(60L, TimeUnit.SECONDS)
                    .readTimeout(60L, TimeUnit.SECONDS)
                    .writeTimeout(60L, TimeUnit.SECONDS)
                    // ✅ Fix: Keep-alive / ping interval
                    .pingInterval(20L, TimeUnit.SECONDS);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create OkHttpClient", e);
        }
    }
}