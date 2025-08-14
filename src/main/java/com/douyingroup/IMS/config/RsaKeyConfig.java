package com.douyingroup.IMS.config;   // adjust to YOUR package

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class RsaKeyConfig {

    @Bean
    public KeyPair rsaKeyPair(@Value("${jwt.private-key}") Resource priv,
                              @Value("${jwt.public-key}")  Resource pub) throws Exception {

        KeyFactory kf = KeyFactory.getInstance("RSA");

        // ----- private key -----
        PrivateKey privateKey;
        try (InputStream is = priv.getInputStream()) {
            String pem = new String(is.readAllBytes())
                    .replaceAll("-----\\w+ PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            privateKey = kf.generatePrivate(
                    new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pem)));
        }

        // ----- public key -----
        PublicKey publicKey;
        try (InputStream is = pub.getInputStream()) {
            String pem = new String(is.readAllBytes())
                    .replaceAll("-----\\w+ PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            publicKey = kf.generatePublic(
                    new X509EncodedKeySpec(Base64.getDecoder().decode(pem)));
        }

        return new KeyPair(publicKey, privateKey);
    }
}
