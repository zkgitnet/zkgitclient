package se.miun.dt133g.zkgitclient.crypto;

import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.JWEAlgorithm;
import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.RSAPrivateKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.security.Security;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.logging.Logger;

/**
 * A utility class for generating various encryption-related keys and secrets.
 * This class generates RSA key pairs, RSA JSON Web Keys (JWKs), password salts,
 * AES initialization vectors (IV), and TOTP secrets.
 * @author Leif Rogell
 */
public final class GenerateEncryptionKeys {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private KeyPair keyPair;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Constructor that initializes the RSA key pair generation.
     * This constructor generates a new RSA key pair upon initialization.
     */
    public GenerateEncryptionKeys() {
        try {
            generateKeyPair();
        } catch (Exception e) {
            LOGGER.severe("Could not generate encryption keys: " + e.getMessage());
        }
    }

    /**
     * Generates a private RSA JSON Web Key (JWK).
     * This method generates a private RSA key and returns it in JWK format for decryption operations.
     * @return the private RSA JWK as a string.
     * @throws Exception if the key generation fails.
     */
    public String generatePrivateRsaJwk() throws Exception {
        LOGGER.finest("Generating private RSA key");
        return generateRsaJwk(true, "decrypt");
    }

    /**
     * Generates a public RSA JSON Web Key (JWK).
     * This method generates a public RSA key and returns it in JWK format for encryption operations.
     * @return the public RSA JWK as a string.
     * @throws Exception if the key generation fails.
     */
    public String generatePublicRsaJwk() throws Exception {
        LOGGER.finest("Generating public RSA key");
        return generateRsaJwk(false, "encrypt");
    }

    /**
     * Generates a random salt for password hashing.
     * This method generates a random salt of a specified length for cryptographic operations like password hashing.
     * @return an integer array representing the generated salt.
     */
    public int[] generateSalt() {
        LOGGER.finest("Generating password salt");
        byte[] saltBytes = new byte[AppConfig.CRYPTO_SALT_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(saltBytes);
        return byteArrayToIntArray(saltBytes);
    }

    /**
     * Generates a random AES initialization vector (IV).
     * This method generates a random IV of a specified length for AES encryption operations.
     * @return an integer array representing the generated IV.
     */
    public int[] generateIv() {
        LOGGER.finest("Generating AES-IV");
        byte[] ivBytes = new byte[AppConfig.CRYPTO_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(ivBytes);
        return byteArrayToIntArray(ivBytes);
    }

    /**
     * Converts a byte array to an integer array.
     * This method converts the byte array into an integer array for cryptographic processing.
     * @param byteArray the byte array to convert.
     * @return the converted integer array.
     */
    private int[] byteArrayToIntArray(final byte[] byteArray) {
        int[] intArray = new int[byteArray.length];
        for (int i = 0; i < byteArray.length; i++) {
            intArray[i] = byteArray[i] & AppConfig.HEX_FF;
        }
        return intArray;
    }

    /**
     * Generates a new TOTP (Time-based One-Time Password) secret.
     * This method generates a new TOTP secret using a secure random number generator.
     * @return the generated TOTP secret as a string.
     */
    public String generateTotpSecret() {
        LOGGER.finest("Generating new TOTP secret");

        SecureRandom random = new SecureRandom();
        StringBuilder secret = new StringBuilder(AppConfig.CRYPTO_TOTP_SECRET_LENGTH);

        for (int i = 0; i < AppConfig.CRYPTO_TOTP_SECRET_LENGTH; i++) {
            int index = random.nextInt(AppConfig.CRYPTO_BASE32_CHARACTERS.length());
            secret.append(AppConfig.CRYPTO_BASE32_CHARACTERS.charAt(index));
        }

        return secret.toString();
    }

    /**
     * Generates an RSA key pair using the specified algorithm and key length.
     * This method generates an RSA key pair (private and public keys) for cryptographic operations.
     * @throws Exception if the key pair generation fails.
     */
    private void generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(AppConfig.CRYPTO_RSA);
        keyPairGenerator.initialize(AppConfig.CRYPTO_RSA_KEY_LENGTH);
        keyPair = keyPairGenerator.generateKeyPair();
    }

    /**
     * Generates an RSA JWK (JSON Web Key) for encryption or decryption.
     * This method generates an RSA JWK based on the public and private keys of the generated RSA key pair.
     * It returns the key in JSON format.
     * @param includePrivate whether to include the private key in the JWK.
     * @param keyOp the operation (e.g., "encrypt" or "decrypt") for which the key will be used.
     * @return the RSA JWK as a JSON string.
     * @throws Exception if the key generation or JWK creation fails.
     */
    private String generateRsaJwk(final boolean includePrivate,
                                  final String keyOp) throws Exception {
        if (keyPair == null) {
            throw new IllegalStateException("RSA key pair not generated.");
        }

        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAKey.Builder jwkBuilder = new RSAKey.Builder(publicKey)
            .algorithm(JWEAlgorithm.RSA_OAEP_512)
            .keyUse(KeyUse.ENCRYPTION);

        if (includePrivate) {
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            jwkBuilder.privateKey(privateKey);
        }

        RSAKey jwk = jwkBuilder.build();
        String jsonString = jwk.toJSONString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonString);

        if (jsonNode.isObject()) {
            ObjectNode objectNode = (ObjectNode) jsonNode;

            ArrayNode keyOpsArray = mapper.createArrayNode();
            keyOpsArray.add(keyOp);
            objectNode.set("key_ops", keyOpsArray);

            objectNode.put("ext", true);
            objectNode.remove("use");
        }

        return mapper.writeValueAsString(jsonNode);
    }
}
