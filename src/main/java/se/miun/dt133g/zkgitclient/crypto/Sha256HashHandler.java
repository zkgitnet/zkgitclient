package se.miun.dt133g.zkgitclient.crypto;

import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.Utils;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

/**
 * A handler for generating SHA-256 hash values for input data.
 * This class handles the creation of a SHA-256 hash of the provided input string. It implements the EncryptionHandler
 * interface but only provides functionality for generating the hash (encryption) and does not support decryption.
 * @author Leif Rogell
 */
public final class Sha256HashHandler implements EncryptionHandler {

    private static Sha256HashHandler INSTANCE;
    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private Utils utils = Utils.getInstance();

    private String input;
    private String output;
    private String rsaKey;
    private byte[] aesKey;
    private byte[] iv;

    /**
     * Private constructor to prevent instantiation.
     */
    private Sha256HashHandler() { }

    /**
     * Returns the singleton instance of the Sha256HashHandler.
     * Ensures that only one instance of the Sha256HashHandler class is used throughout the application.
     * @return the singleton instance of Sha256HashHandler.
     */
    public static Sha256HashHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Sha256HashHandler();
        }
        return INSTANCE;
    }

    /**
     * Generates a SHA-256 hash of the input string.
     * This method computes the SHA-256 hash of the input string and stores
     * the result in the output field as a hexadecimal string.
     */
    @Override
    public void encrypt() {
        output = generateSha256HexString(input);
    }

    /**
     * Decrypt operation is not supported for SHA-256 hashing.
     * This method is provided to fulfill the EncryptionHandler interface, but it does not perform any operation.
     */
    @Override
    public void decrypt() { }

    /**
     * Generates a SHA-256 hash of the input string and returns it as a hexadecimal string.
     * @param input the string to be hashed.
     * @return the SHA-256 hash in hexadecimal string format.
     */
    private String generateSha256HexString(final String input) {
        try {
            MessageDigest md = MessageDigest.getInstance(AppConfig.CRYPTO_SHA_256);
            byte[] digest = md.digest(input.getBytes());
            return utils.bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the input string for hash generation.
     * @param input the input string to be hashed.
     */
    @Override
    public void setInput(final String input) {
        this.input = input;
    }

    /**
     * Sets the AES key for this handler (unused in SHA-256 hashing).
     * @param aesKey the AES key.
     */
    @Override
    public void setAesKey(final byte[] aesKey) {
        this.aesKey = aesKey;
    }

    /**
     * Sets the RSA key for this handler (unused in SHA-256 hashing).
     * @param rsaKey the RSA key.
     */
    @Override
    public void setRsaKey(final String rsaKey) {
        this.rsaKey = rsaKey;
    }

    /**
     * Sets the AES initialization vector (IV) for this handler (unused in SHA-256 hashing).
     * @param iv the initialization vector (IV).
     */
    @Override
    public void setIv(final byte[] iv) {
        this.iv = iv;
    }

    /**
     * Retrieves the output of the hash generation process.
     * @return the output string, which contains the SHA-256 hash in hexadecimal format.
     */
    @Override
    public String getOutput() {
        return output;
    }
}
