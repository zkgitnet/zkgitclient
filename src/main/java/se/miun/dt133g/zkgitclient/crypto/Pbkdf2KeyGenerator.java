package se.miun.dt133g.zkgitclient.crypto;

import se.miun.dt133g.zkgitclient.user.UserCredentials;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.logging.Logger;

public final class Pbkdf2KeyGenerator implements EncryptionHandler {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private static Pbkdf2KeyGenerator INSTANCE;
    private UserCredentials credentials = UserCredentials.getInstance();

    private String input;
    private String output;
    private String rsaKey;
    private byte[] aesKey;
    private byte[] iv;

    private Pbkdf2KeyGenerator() { }

    public static Pbkdf2KeyGenerator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Pbkdf2KeyGenerator();
        }
        return INSTANCE;
    }

    @Override public void encrypt() {
        try {
            credentials.setPbkdf2Hash(generatePbkdf2Hash(credentials.getPassword()));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            output = e.getMessage();
        }
    }

    @Override public void decrypt() {

    }

    private byte[] generatePbkdf2Hash(final char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password, credentials.getSalt(), AppConfig.CRYPTO_PBKDF2_ITERATIONS, AppConfig.CRYPTO_PBKDF2_KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(AppConfig.CRYPTO_PBKDF2_METHOD);
        return factory.generateSecret(spec).getEncoded();
    }

    @Override public void setInput(final String input) {
        this.input = input;
    }

    @Override public void setAesKey(final byte[] aesKey) {
         this.aesKey = aesKey;
    }

    @Override public void setRsaKey(final String rsaKey) {
        this.rsaKey = rsaKey;
    }

    @Override public void setIv(final byte[] iv) {
        this.iv = iv;
    }

    @Override public String getOutput() {
        return output;
    }
}
