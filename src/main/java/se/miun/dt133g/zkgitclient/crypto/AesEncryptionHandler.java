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

public final class AesEncryptionHandler implements EncryptionHandler {

    private static AesEncryptionHandler INSTANCE;
    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private Utils utils = Utils.getInstance();

    private String input;
    private String output;
    private String rsaKey;
    private byte[] aesKey;
    private byte[] iv;

    private AesEncryptionHandler() { }

    public static AesEncryptionHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AesEncryptionHandler();
        }
        return INSTANCE;
    }

    @Override public void encrypt() {
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

    @Override public void decrypt() {
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

    @Override public void setInput(final String input) {
        this.input = input;
    }

    @Override public void setAesKey(final byte[] aesKey) {
        this.aesKey = aesKey;
        LOGGER.finest("aesKey: " + utils.hexToBase64(utils.bytesToHex(aesKey)));
    }

    @Override public void setRsaKey(final String rsaKey) {
        this.rsaKey = rsaKey;
    }

    @Override public void setIv(final byte[] iv) {
        this.iv = iv;
        LOGGER.finest(Arrays.toString(utils.byteArrayToIntArray(iv)));
    }

    @Override public String getOutput() {
        return output;
    }
}
