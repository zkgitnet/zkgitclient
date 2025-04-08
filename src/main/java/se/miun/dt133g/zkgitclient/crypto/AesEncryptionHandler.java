package se.miun.dt133g.zkgitclient.crypto;

import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.Utils;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.Cipher;
import java.util.Base64;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * A singleton class that handles AES encryption and decryption using GCM mode.
 * Implements the EncryptionHandler interface.
 * @author Leif Rogell
 */
public final class AesEncryptionHandler implements EncryptionHandler {

    private static AesEncryptionHandler INSTANCE;
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
    private AesEncryptionHandler() { }

    /**
     * Returns the singleton instance of the AesEncryptionHandler class.
     * @return the singleton instance of AesEncryptionHandler
     */
    public static AesEncryptionHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AesEncryptionHandler();
        }
        return INSTANCE;
    }

    /**
     * Encrypts the input string using AES GCM encryption.
     * The encrypted output is stored as a Base64-encoded string.
     */
    @Override
    public void encrypt() {
        try {
            LOGGER.fine("Encrypting: " + input);
            Cipher cipher = Cipher.getInstance(AppConfig.CRYPTO_AES_GCM);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(aesKey, AppConfig.CRYPTO_AES),
                        new GCMParameterSpec(AppConfig.CRYPTO_AES_TAG_LENGTH, iv));
            this.output = Base64.getEncoder().encodeToString(cipher.doFinal(input.getBytes()));
            LOGGER.finest("Output: " + output);
        } catch (Exception e) {
            this.output = e.getMessage();
            LOGGER.severe(e.getMessage());
        }
    }

    /**
     * Decrypts the input string using AES GCM decryption.
     * The input is expected to be a Base64-encoded string.
     */
    @Override
    public void decrypt() {
        try {
            LOGGER.fine("Decrypting: " + input);
            Cipher cipher = Cipher.getInstance(AppConfig.CRYPTO_AES_GCM);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(aesKey, AppConfig.CRYPTO_AES),
                        new GCMParameterSpec(AppConfig.CRYPTO_AES_TAG_LENGTH, iv));
            this.output = new String(cipher.doFinal(Base64.getDecoder().decode(input)));
            LOGGER.finest("Output: " + output);
        } catch (Exception e) {
            this.output = e.getMessage();
            LOGGER.severe(e.getMessage());
        }
    }

    /**
     * Sets the input string for encryption or decryption.
     * @param input the input string to be encrypted or decrypted
     */
    @Override
    public void setInput(final String input) {
        this.input = input;
    }

    /**
     * Sets the AES key for encryption or decryption.
     * @param aesKey the AES key as a byte array
     */
    @Override
    public void setAesKey(final byte[] aesKey) {
        this.aesKey = aesKey;
        LOGGER.finest("aesKey: " + utils.hexToBase64(utils.bytesToHex(aesKey)));
    }

    /**
     * Sets the RSA key (although it is not used in AES encryption directly).
     * @param rsaKey the RSA key as a string
     */
    @Override
    public void setRsaKey(final String rsaKey) {
        this.rsaKey = rsaKey;
    }

    /**
     * Sets the initialization vector (IV) for AES encryption or decryption.
     * @param iv the initialization vector as a byte array
     */
    @Override
    public void setIv(final byte[] iv) {
        this.iv = iv;
        LOGGER.finest(Arrays.toString(utils.byteArrayToIntArray(iv)));
    }

    /**
     * Retrieves the output of the encryption or decryption process.
     * @return the output as a string
     */
    @Override
    public String getOutput() {
        return output;
    }
}
