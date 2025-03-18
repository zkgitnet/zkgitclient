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

public final class GenerateEncryptionKeys {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private KeyPair keyPair;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public GenerateEncryptionKeys() {
        try {
            generateKeyPair();
        } catch (Exception e) {
        }
    }

    private void generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(AppConfig.CRYPTO_RSA);
        keyPairGenerator.initialize(AppConfig.CRYPTO_RSA_KEY_LENGTH);
        keyPair = keyPairGenerator.generateKeyPair();
    }

    public String generatePrivateRsaJwk() throws Exception {
        if (keyPair == null) {
            throw new IllegalStateException("Key pair not generated.");
        }

        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        RSAKey jwk = new RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .algorithm(JWEAlgorithm.RSA_OAEP_512)
            .keyUse(KeyUse.ENCRYPTION)
            .build();

        String jsonString = jwk.toJSONString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonString);

        if (jsonNode.isObject()) {
            ObjectNode objectNode = (ObjectNode) jsonNode;

            ArrayNode keyOpsArray = mapper.createArrayNode();
            keyOpsArray.add("decrypt");
            objectNode.set("key_ops", keyOpsArray);

            objectNode.put("ext", true);
            objectNode.remove("use");
        }

        return mapper.writeValueAsString(jsonNode);
    }

    public String generatePublicRsaJwk() throws Exception {
        if (keyPair == null) {
            throw new IllegalStateException("Key pair not generated.");
        }

        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        RSAKey jwk = new RSAKey.Builder(publicKey)
            .algorithm(JWEAlgorithm.RSA_OAEP_512)
            .keyUse(KeyUse.ENCRYPTION)
            .build();

        String jsonString = jwk.toJSONString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonString);

        if (jsonNode.isObject()) {
            ObjectNode objectNode = (ObjectNode) jsonNode;

            ArrayNode keyOpsArray = mapper.createArrayNode();
            keyOpsArray.add("encrypt");
            objectNode.set("key_ops", keyOpsArray);

            objectNode.put("ext", true);
            objectNode.remove("use");
        }

        return mapper.writeValueAsString(jsonNode);
    }

    public int[] generateSalt() {
        byte[] saltBytes = new byte[AppConfig.CRYPTO_SALT_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(saltBytes);
        return byteArrayToIntArray(saltBytes);
    }

    public int[] generateIv() {
        byte[] ivBytes = new byte[AppConfig.CRYPTO_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(ivBytes);
        return byteArrayToIntArray(ivBytes);
    }

    private int[] byteArrayToIntArray(final byte[] byteArray) {
        int[] intArray = new int[byteArray.length];
        for (int i = 0; i < byteArray.length; i++) {
            intArray[i] = byteArray[i] & AppConfig.HEX_FF;
        }
        return intArray;
    }

    public String generateTotpSecret() {
        SecureRandom random = new SecureRandom();
        StringBuilder secret = new StringBuilder(AppConfig.CRYPTO_TOTP_SECRET_LENGTH);

        for (int i = 0; i < AppConfig.CRYPTO_TOTP_SECRET_LENGTH; i++) {
            int index = random.nextInt(AppConfig.CRYPTO_BASE32_CHARACTERS.length());
            secret.append(AppConfig.CRYPTO_BASE32_CHARACTERS.charAt(index));
        }

        return secret.toString();
    }
}
