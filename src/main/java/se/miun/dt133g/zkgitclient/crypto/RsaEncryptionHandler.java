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

/**
 * A handler for RSA encryption and decryption using the OAEP (Optimal Asymmetric Encryption Padding) scheme.
 * This class handles encryption and decryption operations using RSA keys,
 * which are provided in JWK (JSON Web Key) format.
 * It supports both encryption with a public key and decryption with a private key.
 * @author Leif Rogell
 */
public final class RsaEncryptionHandler implements EncryptionHandler {

    private static RsaEncryptionHandler INSTANCE;
    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());

    private String input;
    private String output;
    private String rsaKey;
    private byte[] aesKey;
    private byte[] iv;

    /**
     * Private constructor to prevent instantiation.
     */
    private RsaEncryptionHandler() { }

    /**
     * Returns the singleton instance of the RsaEncryptionHandler.
     * This method ensures that only one instance of RsaEncryptionHandler is used throughout the application.
     * @return the singleton instance of RsaEncryptionHandler.
     */
    public static RsaEncryptionHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RsaEncryptionHandler();
        }
        return INSTANCE;
    }

    /**
     * Encrypts the input string using RSA public key encryption with OAEP padding.
     * This method loads the public key from the provided JWK, initializes the cipher, and performs encryption.
     * The result is encoded in Base64 and stored in the output field.
     */
    @Override
    public void encrypt() {
        try {
            PublicKey publicKey = loadPublicKey();
            Cipher cipher = Cipher.getInstance(AppConfig.CRYPTO_RSA_OAEP);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey,
                        new OAEPParameterSpec(AppConfig.CRYPTO_SHA_512,
                                              AppConfig.CRYPTO_MGF1,
                                              MGF1ParameterSpec.SHA512,
                                              PSource.PSpecified.DEFAULT));

            byte[] encryptedBytes = cipher.doFinal(input.getBytes());
            output = Base64.getEncoder().encodeToString(encryptedBytes);

        } catch (Exception e) {
            output = e.getMessage();
        }
    }

    /**
     * Decrypts the input string using RSA private key decryption with OAEP padding.
     * This method loads the private key from the provided JWK, initializes the cipher, and performs decryption.
     * The result is stored as a string in the output field.
     */
    @Override
    public void decrypt() {
        try {
            PrivateKey privateKey = loadPrivateKey();
            Cipher cipher = Cipher.getInstance(AppConfig.CRYPTO_RSA_OAEP);
            cipher.init(Cipher.DECRYPT_MODE, privateKey,
                        new OAEPParameterSpec(AppConfig.CRYPTO_SHA_512,
                                              AppConfig.CRYPTO_MGF1,
                                              MGF1ParameterSpec.SHA512,
                                              PSource.PSpecified.DEFAULT));

            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(input));
            output = new String(decryptedBytes);
        } catch (Exception e) {
            output = AppConfig.ERROR_DECRYPTION;
        }
    }

    /**
     * Loads and constructs a private key from the provided JWK string.
     * This method decodes the components of the RSA private key from the JWK and constructs an RSAPrivateCrtKeySpec,
     * which is then used to generate the private key.
     * @return the RSA private key.
     * @throws Exception if the JWK cannot be parsed or the key cannot be generated.
     */
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

        RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(modulus,
                                                                publicExponent,
                                                                privateExponent,
                                                                primeP,
                                                                primeQ,
                                                                primeExponentP,
                                                                primeExponentQ,
                                                                crtCoefficient);

        KeyFactory keyFactory = KeyFactory.getInstance(AppConfig.CRYPTO_RSA);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * Loads and constructs a public key from the provided JWK string.
     * This method decodes the components of the RSA public key from the JWK and constructs an RSAPublicKeySpec,
     * which is then used to generate the public key.
     * @return the RSA public key.
     * @throws Exception if the JWK cannot be parsed or the key cannot be generated.
     */
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

    /**
     * Decodes a Base64 encoded string into a BigInteger.
     * This method is used to decode the Base64 encoded components of the RSA keys into BigInteger values.
     * @param base64 the Base64 encoded string.
     * @return the decoded BigInteger.
     */
    private BigInteger decodeBase64ToBigInt(final String base64) {
        return new BigInteger(1, Base64.getUrlDecoder().decode(base64));
    }

    /**
     * Sets the input string for encryption or decryption.
     * @param input the input string to be encrypted or decrypted.
     */
    @Override
    public void setInput(final String input) {
        this.input = input;
    }

    /**
     * Sets the AES key for this handler (unused in RSA encryption).
     * @param aesKey the AES key.
     */
    @Override
    public void setAesKey(final byte[] aesKey) {
        this.aesKey = aesKey;
    }

    /**
     * Sets the RSA key (either public or private) for this handler.
     * @param rsaKey the RSA key in JWK format.
     */
    @Override
    public void setRsaKey(final String rsaKey) {
        this.rsaKey = rsaKey;
    }

    /**
     * Sets the AES initialization vector (IV) for this handler (unused in RSA encryption).
     * @param iv the initialization vector (IV).
     */
    @Override
    public void setIv(final byte[] iv) {
        this.iv = iv;
    }

    /**
     * Retrieves the output of the encryption or decryption process.
     * @return the output string, which contains the result of encryption or decryption.
     */
    @Override
    public String getOutput() {
        return output;
    }
}
