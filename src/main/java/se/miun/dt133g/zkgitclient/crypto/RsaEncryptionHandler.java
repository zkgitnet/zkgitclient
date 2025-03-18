package se.miun.dt133g.zkgitclient.crypto;

import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.math.BigInteger;
import java.util.Base64;
import java.util.Map;
import java.util.logging.Logger;

public final class RsaEncryptionHandler implements EncryptionHandler {

    private static RsaEncryptionHandler INSTANCE;
    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());

    private String input;
    private String output;
    private String rsaKey;
    private byte[] aesKey;
    private byte[] iv;

    private RsaEncryptionHandler() { }

    public static RsaEncryptionHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RsaEncryptionHandler();
        }
        return INSTANCE;
    }

    @Override public void encrypt() {
        try {
            PublicKey publicKey = loadPublicKey();
            Cipher cipher = Cipher.getInstance(AppConfig.CRYPTO_RSA_OAEP);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey, new OAEPParameterSpec(
                                                                              AppConfig.CRYPTO_SHA_512, AppConfig.CRYPTO_MGF1, MGF1ParameterSpec.SHA512, PSource.PSpecified.DEFAULT
                                                                              ));

            byte[] encryptedBytes = cipher.doFinal(input.getBytes());
            output = Base64.getEncoder().encodeToString(encryptedBytes);

        } catch (Exception e) {
            output = e.getMessage();
        }
    }

    @Override public void decrypt() {
        try {
            PrivateKey privateKey = loadPrivateKey();
            Cipher cipher = Cipher.getInstance(AppConfig.CRYPTO_RSA_OAEP);
            cipher.init(Cipher.DECRYPT_MODE, privateKey, new OAEPParameterSpec(
                                                                               AppConfig.CRYPTO_SHA_512, AppConfig.CRYPTO_MGF1, MGF1ParameterSpec.SHA512, PSource.PSpecified.DEFAULT
                                                                               ));

            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(input));
            output = new String(decryptedBytes);
        } catch (Exception e) {
            output = AppConfig.ERROR_DECRYPTION;
        }
    }

    private PrivateKey loadPrivateKey() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> jwk = mapper.readValue(rsaKey, Map.class);

        BigInteger modulus = decodeBase64ToBigInt(jwk.get(AppConfig.RSA_N));
        BigInteger privateExponent = decodeBase64ToBigInt(jwk.get(AppConfig.RSA_D));
        BigInteger publicExponent = decodeBase64ToBigInt(jwk.get(AppConfig.RSA_E));
        BigInteger primeP = decodeBase64ToBigInt(jwk.get(AppConfig.RSA_P));
        BigInteger primeQ = decodeBase64ToBigInt(jwk.get(AppConfig.RSA_Q));
        BigInteger primeExponentP = decodeBase64ToBigInt(jwk.get(AppConfig.RSA_DP));
        BigInteger primeExponentQ = decodeBase64ToBigInt(jwk.get(AppConfig.RSA_DQ));
        BigInteger crtCoefficient = decodeBase64ToBigInt(jwk.get(AppConfig.RSA_QI));

        RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(
                                                                modulus, publicExponent, privateExponent, primeP, primeQ, primeExponentP, primeExponentQ, crtCoefficient);

        KeyFactory keyFactory = KeyFactory.getInstance(AppConfig.CRYPTO_RSA);
        return keyFactory.generatePrivate(keySpec);
    }

    private PublicKey loadPublicKey() throws Exception {
        try {
            Map<String, String> jwkMap = new ObjectMapper().readValue(rsaKey, Map.class);

            BigInteger modulus = decodeBase64ToBigInt(jwkMap.get(AppConfig.RSA_N));
            BigInteger publicExponent = decodeBase64ToBigInt(jwkMap.get(AppConfig.RSA_E));

            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, publicExponent);
            KeyFactory keyFactory = KeyFactory.getInstance(AppConfig.CRYPTO_RSA);

            return keyFactory.generatePublic(keySpec);
        } catch (IOException e) {
            throw new Exception("Error parsing JWK to PublicKey: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new Exception("Error generating PublicKey: " + e.getMessage(), e);
        }
    }

    private BigInteger decodeBase64ToBigInt(final String base64) {
        return new BigInteger(1, Base64.getUrlDecoder().decode(base64));
    }

    @Override public void setInput(final String input) {
        this.input = input;
    }

    @Override public void setAesKey(final byte[] aesKey) {
        this.aesKey = aesKey;
    }

    @Override public void setRsaKey(final String rsaKey) {
        this.rsaKey = rsaKey;
    }

    @Override public void setIv(final byte[] iv) {
        this.iv = iv;
    }

    @Override public String getOutput() {
        return output;
    }
}
