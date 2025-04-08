package se.miun.dt133g.zkgitclient.crypto;

import se.miun.dt133g.zkgitclient.user.CurrentUserRepo;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.Utils;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.file.Paths;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * A singleton class that handles AES stream encryption and decryption.
 * This class provides methods for encrypting and decrypting streams using AES encryption in GCM mode.
 * It processes the data in chunks, ensuring that large files can be handled efficiently.
 * @author Leif Rogell
 */
public final class AesStreamEncryptionHandler implements StreamEncryptionHandler {

    private static AesStreamEncryptionHandler INSTANCE;
    private Utils utils = Utils.getInstance();
    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());

    private CurrentUserRepo currentRepo = CurrentUserRepo.getInstance();
    private byte[] aesKey;
    private byte[] iv;

    /**
     * Private constructor to prevent instatiation.
     */
    private AesStreamEncryptionHandler() { }

    /**
     * Returns the singleton instance of the AesStreamEncryptionHandler.
     * @return the singleton instance of AesStreamEncryptionHandler.
     */
    public static synchronized AesStreamEncryptionHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AesStreamEncryptionHandler();
        }
        return INSTANCE;
    }

    /**
     * Encrypts the input stream and writes the result to the output stream.
     * This method reads data from the input stream, encrypts it using AES GCM, and writes the encrypted data
     * to the output stream.
     * @param inputStream the input stream to be encrypted.
     * @param outputStream the output stream to write the encrypted data.
     */
    @Override
    public void encryptStream(final InputStream inputStream,
                              final OutputStream outputStream) {
        processStream(Cipher.ENCRYPT_MODE, inputStream, outputStream);
    }

    /**
     * Decrypts the input stream and writes the result to the output stream.
     * This method reads encrypted data from the input stream, decrypts it using AES GCM, and writes the decrypted
     * data to the output stream.
     * @param inputStream the input stream containing encrypted data.
     * @param outputStream the output stream to write the decrypted data.
     */
    @Override
    public void decryptStream(final InputStream inputStream,
                              final OutputStream outputStream) {
        processStream(Cipher.DECRYPT_MODE, inputStream, outputStream);
    }

    /**
     * Processes the input stream for encryption or decryption.
     * This method is used by both the encrypt and decrypt methods to handle the stream processing logic.
     * @param mode the cipher mode (encryption or decryption).
     * @param inputStream the input stream to process.
     * @param outputStream the output stream to write the result.
     */
    private void processStream(final int mode,
                               final InputStream inputStream,
                               final OutputStream outputStream) {
        try {
            String operation = (mode == Cipher.ENCRYPT_MODE) ? "encryption" : "decryption";
            LOGGER.fine("Starting " + operation + " of repo");
            LOGGER.finest("iv: " + Arrays.toString(utils.byteArrayToIntArray(iv)));
            LOGGER.finest("aesKey: " + utils.hexToBase64(utils.bytesToHex(aesKey)));

            Cipher cipher = Cipher.getInstance(AppConfig.CRYPTO_AES_GCM);
            SecretKey secretKey = new SecretKeySpec(aesKey, AppConfig.CRYPTO_AES);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(AppConfig.CRYPTO_AES_TAG_LENGTH, iv);
            cipher.init(mode, secretKey, gcmSpec);

            byte[] buffer = new byte[AppConfig.ONE_KB];
            int bytesRead;

            if (mode == Cipher.ENCRYPT_MODE) {
                try (CipherOutputStream cipherOut = new CipherOutputStream(outputStream, cipher)) {
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        cipherOut.write(buffer, 0, bytesRead);
                    }
                }
            } else {
                LOGGER.fine("Preparing to read encrypted input");

                EncryptionHandler sha256Handler = EncryptionFactory.getEncryptionHandler(AppConfig.CRYPTO_SHA_256);
                File file = Paths.get(System.getProperty(AppConfig.JAVA_TMP),
                                      sha256Handler.getOutput()).toFile();

                FileInputStream fis = new FileInputStream(file);

                LOGGER.fine("Initiating cipherIn");
                try (CipherInputStream cipherIn = new CipherInputStream(fis, cipher)) {
                    while ((bytesRead = cipherIn.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    LOGGER.fine("Decryption complete");
                }
            }
            //outputStream.close();

            LOGGER.fine("Completed " + operation + " of repo");
        } catch (Exception e) {
            LOGGER.severe("Could not " + ((mode == Cipher.ENCRYPT_MODE)
                                          ? "encrypt" : "decrypt") + " repo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sets the AES key for encryption and decryption.
     * @param aesKey the AES key to use for encryption and decryption.
     */
    @Override
    public void setAesKey(final byte[] aesKey) {
        this.aesKey = aesKey;
    }

    /**
     * Sets the initialization vector (IV) for encryption and decryption.
     * @param iv the IV to use for encryption and decryption.
     */
    @Override
    public void setIv(final byte[] iv) {
        this.iv = iv;
    }
}
