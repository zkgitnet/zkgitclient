package se.miun.dt133g.zkgitclient.crypto;

import se.miun.dt133g.zkgitclient.user.UserCredentials;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.io.IOException;
import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.math.BigInteger;
import java.util.Base64;
import java.util.Map;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class RsaSignatureHandler implements EncryptionHandler {

    private static RsaSignatureHandler INSTANCE;
    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private UserCredentials credentials = UserCredentials.getInstance();

    private String input;
    private String output;
    private String rsaKey;
    private byte[] aesKey;
    private byte[] iv;

    private RsaSignatureHandler() { }

    public static RsaSignatureHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RsaSignatureHandler();
        }
        return INSTANCE;
    }

    @Override public void encrypt() {
        try {
            PrivateKey privateKey = loadPrivateKey();
            Signature signature = Signature.getInstance(AppConfig.CRYPTO_RSA_SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(input.getBytes());

            byte[] signatureBytes = signature.sign();
            output = Base64.getUrlEncoder().encodeToString(signatureBytes);

        } catch (Exception e) {
            output = e.getMessage();
        }
    }

    @Override public void decrypt() {

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
