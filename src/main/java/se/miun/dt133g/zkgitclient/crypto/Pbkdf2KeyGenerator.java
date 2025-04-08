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

/**
 * A key generator that implements PBKDF2 (Password-Based Key Derivation Function 2) for password hashing.
 * This class generates a hashed key using the PBKDF2 algorithm and the user's credentials.
 * It implements the {@link EncryptionHandler} interface and provides methods to hash passwords
 * using a defined number of iterations and key length.
 * @author Leif Rogell
 */
public final class Pbkdf2KeyGenerator implements EncryptionHandler {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private static Pbkdf2KeyGenerator INSTANCE;
    private UserCredentials credentials = UserCredentials.getInstance();

    private String input;
    private String output;
    private String rsaKey;
    private byte[] aesKey;
    private byte[] iv;

    /**
     * Private constructor to prevent instantiation.
     */
    private Pbkdf2KeyGenerator() { }

    /**
     * Returns the singleton instance of Pbkdf2KeyGenerator.
     * This method ensures that only one instance of Pbkdf2KeyGenerator is used throughout the application.
     * @return the singleton instance of Pbkdf2KeyGenerator.
     */
    public static Pbkdf2KeyGenerator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Pbkdf2KeyGenerator();
        }
        return INSTANCE;
    }

    /**
     * Encrypts by generating a PBKDF2 hash from the user's password.
     * This method generates a PBKDF2 hash using the password and salt defined in the {@link UserCredentials} object.
     * The hash is stored in the user's credentials.
     */
    @Override
    public void encrypt() {
        try {
            credentials.setPbkdf2Hash(generatePbkdf2Hash(credentials.getPassword()));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            output = e.getMessage();
        }
    }

    /**
     * Decrypts, but no action is performed for decryption in this class.
     * This method does not implement any decryption logic as PBKDF2 is used for hashing, not encryption.
     */
    @Override
    public void decrypt() {

    }

    /**
     * Generates a PBKDF2 hash from the given password.
     * This method uses the PBKDF2 algorithm with the configured number of iterations and key length
     * to generate a hash for the provided password.
     * @param password the password to be hashed.
     * @return the PBKDF2 hash of the password.
     * @throws NoSuchAlgorithmException if the PBKDF2 algorithm is not available.
     * @throws InvalidKeySpecException if the key specification is invalid.
     */
    private byte[] generatePbkdf2Hash(final char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password, credentials.getSalt(),
                                      AppConfig.CRYPTO_PBKDF2_ITERATIONS,
                                      AppConfig.CRYPTO_PBKDF2_KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(AppConfig.CRYPTO_PBKDF2_METHOD);
        return factory.generateSecret(spec).getEncoded();
    }

    /**
     * Sets the input for this handler.
     * This method stores the provided input, although it is not used in the current implementation.
     * @param input the input string.
     */
    @Override
    public void setInput(final String input) {
        this.input = input;
    }

    /**
     * Sets the AES key for this handler.
     * This method stores the provided AES key, although it is not used in the current implementation.
     * @param aesKey the AES key.
     */
    @Override
    public void setAesKey(final byte[] aesKey) {
         this.aesKey = aesKey;
    }

    /**
     * Sets the RSA key for this handler.
     * This method stores the provided RSA key, although it is not used in the current implementation.
     * @param rsaKey the RSA key.
     */
    @Override
    public void setRsaKey(final String rsaKey) {
        this.rsaKey = rsaKey;
    }

    /**
     * Sets the AES initialization vector (IV) for this handler.
     * This method stores the provided IV, although it is not used in the current implementation.
     * @param iv the initialization vector (IV).
     */
    @Override
    public void setIv(final byte[] iv) {
        this.iv = iv;
    }

    /**
     * Retrieves the output of this handler.
     * This method returns the result of the encryption, typically an error message if the encryption fails.
     * @return the output string, which contains the result of the encryption or any error messages.
     */
    @Override
    public String getOutput() {
        return output;
    }
}
