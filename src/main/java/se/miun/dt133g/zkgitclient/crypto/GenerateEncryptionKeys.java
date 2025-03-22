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
            LOGGER.severe("Could not generate encryption keys: " + e.getMessage());
        }
    }

    public String generatePrivateRsaJwk() throws Exception {
        LOGGER.finest("Generating private RSA key");
        return generateRsaJwk(true, "decrypt");
    }

    public String generatePublicRsaJwk() throws Exception {
        LOGGER.finest("Generating public RSA key");
        return generateRsaJwk(false, "encrypt");
    }


    public int[] generateSalt() {
        LOGGER.finest("Generating password salt");
        byte[] saltBytes = new byte[AppConfig.CRYPTO_SALT_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(saltBytes);
        return byteArrayToIntArray(saltBytes);
    }

    public int[] generateIv() {
        LOGGER.finest("Generating AES-IV");
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
        LOGGER.finest("Generating new TOTP secret");
        
        SecureRandom random = new SecureRandom();
        StringBuilder secret = new StringBuilder(AppConfig.CRYPTO_TOTP_SECRET_LENGTH);

        for (int i = 0; i < AppConfig.CRYPTO_TOTP_SECRET_LENGTH; i++) {
            int index = random.nextInt(AppConfig.CRYPTO_BASE32_CHARACTERS.length());
            secret.append(AppConfig.CRYPTO_BASE32_CHARACTERS.charAt(index));
        }

        return secret.toString();
    }
    
    private void generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(AppConfig.CRYPTO_RSA);
        keyPairGenerator.initialize(AppConfig.CRYPTO_RSA_KEY_LENGTH);
        keyPair = keyPairGenerator.generateKeyPair();
    }

    private String generateRsaJwk(boolean includePrivate, String keyOp) throws Exception {
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
