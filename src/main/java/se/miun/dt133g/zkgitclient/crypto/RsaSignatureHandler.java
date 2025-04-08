package se.miun.dt133g.zkgitclient.crypto;

import se.miun.dt133g.zkgitclient.user.UserCredentials;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.math.BigInteger;
import java.util.Base64;
import java.util.Map;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A handler for generating RSA signatures using a private key.
 * This class handles the signing of data using an RSA private key. It supports the generation of digital signatures
 * using the RSA algorithm with a specified signing algorithm.
 * @author Leif Rogell
 */
public final class RsaSignatureHandler implements EncryptionHandler {

    private static RsaSignatureHandler INSTANCE;
    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private UserCredentials credentials = UserCredentials.getInstance();

    private String input;
    private String output;
    private String rsaKey;
    private byte[] aesKey;
    private byte[] iv;

    /**
     * Private constructor to prevent instantiation.
     */
    private RsaSignatureHandler() { }

    /**
     * Returns the singleton instance of the RsaSignatureHandler.
     * This method ensures that only one instance of RsaSignatureHandler is used throughout the application.
     * @return the singleton instance of RsaSignatureHandler.
     */
    public static RsaSignatureHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RsaSignatureHandler();
        }
        return INSTANCE;
    }

    /**
     * Generates a signature for the input data using the RSA private key.
     * This method loads the private key from the provided JWK, initializes the Signature object,
     * and signs the input data.
     * The resulting signature is encoded in Base64 URL format and stored in the output field.
     */
    @Override
    public void encrypt() {
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

    /**
     * Decrypt operation is not supported for RSA signatures.
     * This method is provided to fulfill the EncryptionHandler interface,
     * but it does not perform any operation.
     */
    @Override
    public void decrypt() {

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
     * which is then used to generate the public key. This method is not used in the signature generation process.
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
     * Decodes a Base64 URL encoded string into a BigInteger.
     * This method is used to decode the Base64 URL encoded components of the RSA keys into BigInteger values.
     * @param base64 the Base64 URL encoded string.
     * @return the decoded BigInteger.
     */
    private BigInteger decodeBase64ToBigInt(final String base64) {
        return new BigInteger(1, Base64.getUrlDecoder().decode(base64));
    }

    /**
     * Sets the input string for signature generation.
     * @param input the input string to be signed.
     */
    @Override
    public void setInput(final String input) {
        this.input = input;
    }

    /**
     * Sets the AES key for this handler (unused in RSA signature generation).
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
     * Sets the AES initialization vector (IV) for this handler (unused in RSA signature generation).
     * @param iv the initialization vector (IV).
     */
    @Override
    public void setIv(final byte[] iv) {
        this.iv = iv;
    }

    /**
     * Retrieves the output of the signature generation process.
     * @return the output string, which contains the generated signature in Base64 URL format.
     */
    @Override
    public String getOutput() {
        return output;
    }
}
