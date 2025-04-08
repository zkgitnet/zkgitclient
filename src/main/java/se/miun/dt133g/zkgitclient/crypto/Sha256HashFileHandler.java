package se.miun.dt133g.zkgitclient.crypto;

import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.Utils;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

/**
 * A handler for generating SHA-256 hash values of file contents.
 * This class reads a file's content, computes its SHA-256 hash, and returns the result as a hexadecimal string.
 * It implements the EncryptionHandler interface but only provides functionality for generating the hash (encryption)
 * and does not support decryption.
 * @author Leif Rogell
 */
public final class Sha256HashFileHandler implements EncryptionHandler {

    private static Sha256HashFileHandler INSTANCE;
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
    private Sha256HashFileHandler() { }

    /**
     * Returns the singleton instance of the Sha256HashFileHandler.
     * Ensures that only one instance of the Sha256HashFileHandler class is used throughout the application.
     * @return the singleton instance of Sha256HashFileHandler.
     */
    public static Sha256HashFileHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Sha256HashFileHandler();
        }
        return INSTANCE;
    }

    /**
     * Generates a SHA-256 hash of the file content.
     * This method computes the SHA-256 hash of the file specified by the
     * input path and stores the result in the output field as a hexadecimal string.
     */
    @Override
    public void encrypt() {
        output = generateSha256HexString(input);
    }

    /**
     * Decrypt operation is not supported for SHA-256 hashing of files.
     * This method is provided to fulfill the EncryptionHandler interface,
     * but it does not perform any operation.
     */
    @Override
    public void decrypt() { }

    /**
     * Generates a SHA-256 hash of the file content specified by the input file
     * path and returns it as a hexadecimal string.
     * @param input the path to the file to be hashed.
     * @return the SHA-256 hash in hexadecimal string format.
     */
    private String generateSha256HexString(final String input) {
        try {
            File file = new File(input);
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            MessageDigest md = MessageDigest.getInstance(AppConfig.CRYPTO_SHA_256);
            byte[] digest = md.digest(fileBytes);
            return utils.bytesToHex(digest);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the input file path for hash generation.
     * @param input the file path to be hashed.
     */
    @Override
    public void setInput(final String input) {
        this.input = input;
    }

    /**
     * Sets the AES key for this handler (unused in SHA-256 hashing of files).
     * @param aesKey the AES key.
     */
    @Override
    public void setAesKey(final byte[] aesKey) {
        this.aesKey = aesKey;
    }

    /**
     * Sets the RSA key for this handler (unused in SHA-256 hashing of files).
     * @param rsaKey the RSA key.
     */
    @Override
    public void setRsaKey(final String rsaKey) {
        this.rsaKey = rsaKey;
    }

    /**
     * Sets the AES initialization vector (IV) for this handler (unused in SHA-256 hashing of files).
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
