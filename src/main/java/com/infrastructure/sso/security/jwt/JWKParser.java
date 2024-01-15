//package com.infrastructure.sso.security.jwt;
//
///**
// * @author SaKondri
// */
//
//import org.keycloak.common.crypto.CryptoIntegration;
//import org.keycloak.common.util.Base64Url;
//import org.keycloak.crypto.KeyType;
//import org.keycloak.jose.jwk.ECPublicJWK;
//import org.keycloak.jose.jwk.JWK;
//import org.keycloak.jose.jwk.RSAPublicJWK;
//import org.keycloak.util.JsonSerialization;
//
//import java.math.BigInteger;
//import java.security.KeyFactory;
//import java.security.PublicKey;
//import java.security.spec.ECParameterSpec;
//import java.security.spec.ECPoint;
//import java.security.spec.ECPublicKeySpec;
//import java.security.spec.RSAPublicKeySpec;
//
///**
// * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
// */
//public class JWKParser {
//
//    private JWK jwk;
//
//    private JWKParser() {
//    }
//
//    public JWKParser(JWK jwk) {
//        this.jwk = jwk;
//    }
//
//    public static JWKParser create() {
//        return new JWKParser();
//    }
//
//    public static JWKParser create(JWK jwk) {
//        return new JWKParser(jwk);
//    }
//
//    public JWKParser parse(String jwk) {
//        try {
//            this.jwk = JsonSerialization.mapper.readValue(jwk, JWK.class);
//            return this;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public JWK getJwk() {
//        return jwk;
//    }
//
//    public PublicKey toPublicKey() {
//        String keyType = jwk.getKeyType();
//        if (keyType.equals(KeyType.RSA)) {
//            return createRSAPublicKey();
//        } else if (keyType.equals(KeyType.EC)) {
//            return createECPublicKey();
//
//        } else {
//            throw new RuntimeException("Unsupported keyType " + keyType);
//        }
//    }
//
//    private PublicKey createECPublicKey() {
//        /* Check if jwk.getOtherClaims return an empty map */
//        if (jwk.getOtherClaims().size() == 0) {
//            throw new RuntimeException("JWK Otherclaims map is empty.");
//        }
//
//        /* Try retrieving the necessary fields */
//        String crv = (String) jwk.getOtherClaims().get(ECPublicJWK.CRV);
//        String xStr = (String) jwk.getOtherClaims().get(ECPublicJWK.X);
//        String yStr = (String) jwk.getOtherClaims().get(ECPublicJWK.Y);
//
//        /* Check if the retrieving of necessary fields success */
//        if (crv == null || xStr == null || yStr == null) {
//            throw new RuntimeException("Fail to retrieve ECPublicJWK.CRV, ECPublicJWK.X or ECPublicJWK.Y field.");
//        }
//
//        BigInteger x = new BigInteger(1, Base64Url.decode(xStr));
//        BigInteger y = new BigInteger(1, Base64Url.decode(yStr));
//
//        String name;
//        switch (crv) {
//            case "P-256" :
//                name = "secp256r1";
//                break;
//            case "P-384" :
//                name = "secp384r1";
//                break;
//            case "P-521" :
//                name = "secp521r1";
//                break;
//            default :
//                throw new RuntimeException("Unsupported curve");
//        }
//
//        try {
//
//            ECPoint point = new ECPoint(x, y);
//            ECParameterSpec params = CryptoIntegration.getProvider().createECParams(name);
//            ECPublicKeySpec pubKeySpec = new ECPublicKeySpec(point, params);
//
//            KeyFactory kf = CryptoIntegration.getProvider().getKeyFactory("ECDSA");
//            return kf.generatePublic(pubKeySpec);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private PublicKey createRSAPublicKey() {
//        BigInteger modulus = new BigInteger(1, Base64Url.decode(jwk.getOtherClaims().get(RSAPublicJWK.MODULUS).toString()));
//        BigInteger publicExponent = new BigInteger(1, Base64Url.decode(jwk.getOtherClaims().get(RSAPublicJWK.PUBLIC_EXPONENT).toString()));
//
//        try {
//            KeyFactory kf = KeyFactory.getInstance("RSA");
//            return kf.generatePublic(new RSAPublicKeySpec(modulus, publicExponent));
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public boolean isKeyTypeSupported(String keyType) {
//        return (RSAPublicJWK.RSA.equals(keyType) || ECPublicJWK.EC.equals(keyType));
//    }
//
//}