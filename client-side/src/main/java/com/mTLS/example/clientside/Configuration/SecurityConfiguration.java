package com.mTLS.example.clientside.Configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.SslProvider;

import javax.net.ssl.KeyManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;

@Configuration
public class SecurityConfiguration {
    @Value("${swapy.client.ssl.key-store}")
    public String keyStorePath;

    @Value("${swapy.client.ssl.key-store-password}")
    public String keyStorePassword;


    //build ssl Context and setup Key store for one way auth

    public SslContext buildSSLContext(){

        SslContext sslContext = null;
        try(FileInputStream keyStoreFileInputStream = new FileInputStream(ResourceUtils.getFile(keyStorePath));
//            FileInputStream trustStoreFileInputStream = new FileInputStream(ResourceUtils.getFile(trustStorePath));
        ) {

            KeyStore keyStore = KeyStore.getInstance("jks");
            keyStore.load(keyStoreFileInputStream, keyStorePassword.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

//            KeyStore trustStore = KeyStore.getInstance("jks");
//            trustStore.load(trustStoreFileInputStream, trustStorePassword.toCharArray());
//            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
//            trustManagerFactory.init(trustStore);

            sslContext = SslContextBuilder.forClient()
                    .keyManager(keyManagerFactory)
//                    .trustManager(trustManagerFactory)
                    .build();
        } catch(Exception e){
            System.out.println("error occ while creating sslContext");
            e.printStackTrace();
        }

        return sslContext;
    }







    /**
     * The bean is supposed to be used while making an REST call to
     * the server.
     *
     * This bean would have the {@link this #buildSslContext()}
     * keys and certificate that would be exchanged during the handshake
     *
     * @return webClient - to be wired where the server call expecting a 2-way SSL handshake
     */
    @Bean
    public WebClient webClient() {
        SslProvider sslProvider = SslProvider.builder()
                .sslContext(buildSSLContext()).build();
       HttpClient httpClient = HttpClient.create()
                .secure(sslProvider);
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }



}
