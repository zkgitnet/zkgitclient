package se.miun.dt133g.zkgitclient.crypto;

import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.Utils;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.Arrays;
import java.util.logging.Logger;
import java.security.SecureRandom;

/**
 * A handler for generating and managing AES initialization vectors (IVs).
 * This class is responsible for generating a random AES IV during encryption. It implements the
 * {@link EncryptionHandler} interface, allowing the management of IVs for cryptographic operations.
 * @author Leif Rogell
 */
public final class IvHandler implements EncryptionHandler {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private static IvHandler INSTANCE;
    private Utils utils = Utils.getInstance();

    private String input;
    private String output;
    private String rsaKey;
    private byte[] aesKey;
    private byte[] iv;

    /**
     * Private constructor to prevent instantiation.
     */
    private IvHandler() { }

    /**
     * Returns the singleton instance of IvHandler.
     * This method ensures that only one instance of IvHandler is used throughout the application.
     * @return the singleton instance of IvHandler.
     */
    public static IvHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new IvHandler();
        }
        return INSTANCE;
    }

    /**
     * Encrypts by generating a random AES initialization vector (IV).
     * This method generates a random 12-byte IV using a secure random generator and stores it as the output.
     */
    @Override
    public void encrypt() {
        this.iv = new byte[AppConfig.CRYPTO_IV_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(this.iv);
        output = Arrays.toString(this.iv).replace(" ", "");
    }

    /**
     * Decrypts, but no action is performed for decryption in this class.
     * This method does not implement any decryption logic as IV generation is a one-way operation.
     */
    @Override
    public void decrypt() { }

    /**
     * Sets the input for this handler.
     * This method stores the provided input, but it is not used in the current implementation.
     * @param input the input string.
     */
    @Override
    public void setInput(final String input) {
        this.input = input;
    }

    /**
     * Sets the AES key for this handler.
     * This method stores the provided AES key, although it is not currently used in the implementation.
     * @param aesKey the AES key.
     */
    @Override
    public void setAesKey(final byte[] aesKey) {
        this.aesKey = aesKey;
    }

    /**
     * Sets the RSA key for this handler.
     * This method stores the provided RSA key, although it is not currently used in the implementation.
     * @param rsaKey the RSA key.
     */
    @Override
    public void setRsaKey(final String rsaKey) {
        this.rsaKey = rsaKey;
    }

    /**
     * Sets the AES initialization vector (IV) for this handler.
     * This method stores the provided IV, which can be used in cryptographic operations if needed.
     * @param iv the initialization vector (IV).
     */
    @Override
    public void setIv(final byte[] iv) {
        this.iv = iv;
    }

    /**
     * Retrieves the output of this handler.
     * This method returns the generated IV as a string.
     * @return the output string representing the generated IV.
     */
    @Override
    public String getOutput() {
        return output;
    }
}
