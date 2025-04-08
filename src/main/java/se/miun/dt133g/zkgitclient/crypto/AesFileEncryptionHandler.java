package se.miun.dt133g.zkgitclient.crypto;

import se.miun.dt133g.zkgitclient.user.CurrentUserRepo;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 * Handler for AES file encryption and decryption operations.
 * Provides methods to encrypt and decrypt files using AES in GCM mode.
 * @author Leif Rogell
 */
public final class AesFileEncryptionHandler implements EncryptionHandler {

    private static AesFileEncryptionHandler INSTANCE;
    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());

    private CurrentUserRepo currentRepo = CurrentUserRepo.getInstance();
    private String tmpDir = System.getProperty(AppConfig.JAVA_TMP);
    private String inputFileName;
    private String outputFileName;
    private String rsaKey;
    private byte[] aesKey;
    private byte[] iv;

    /**
     * Private constructor to prevent instantiation.
     */
    private AesFileEncryptionHandler() { }

    /**
     * Returns the singleton instance of AesFileEncryptionHandler.
     * @return the singleton instance of AesFileEncryptionHandler.
     */
    public static synchronized AesFileEncryptionHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AesFileEncryptionHandler();
        }
        return INSTANCE;
    }

    /**
     * Encrypts the input file and writes the encrypted data to the output file.
     */
    @Override
    public void encrypt() {
        try {
            setOutputFileName(currentRepo.getEncFileName());

            Cipher cipher = initializeCipher(Cipher.ENCRYPT_MODE);

            try (FileInputStream fis = new FileInputStream(inputFileName);
                 FileOutputStream fos = new FileOutputStream(outputFileName);
                 CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {

                transferData(fis, cos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Decrypts the input file and writes the decrypted data to the output file.
     */
    @Override
    public void decrypt() {
        try {
            LOGGER.finest("DECRYPTING");
            setOutputFileName("test_repo2");

            Cipher cipher = initializeCipher(Cipher.DECRYPT_MODE);

            try (FileInputStream fis = new FileInputStream(inputFileName);
                 FileOutputStream fos = new FileOutputStream(outputFileName);
                 CipherInputStream cis = new CipherInputStream(fis, cipher)) {

                transferData(cis, fos);
            }
            LOGGER.finest("DECRYPTION complete");
        } catch (Exception e) {
            LOGGER.severe("Decryption error: " + e.getMessage());
        }
    }

    /**
     * Initializes the AES cipher for encryption or decryption.
     * @param mode the mode of operation (Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE).
     * @return the initialized Cipher.
     * @throws Exception if the cipher cannot be initialized.
     */
    private Cipher initializeCipher(final int mode) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(aesKey, AppConfig.CRYPTO_AES);
        GCMParameterSpec paramSpec = new GCMParameterSpec(AppConfig.CRYPTO_AES_TAG_LENGTH, iv);
        Cipher cipher = Cipher.getInstance(AppConfig.CRYPTO_AES_GCM);
        cipher.init(mode, keySpec, paramSpec);
        return cipher;
    }

    /**
     * Transfers data from the input stream to the output stream using a buffer.
     * @param input the input stream to read from.
     * @param output the output stream to write to.
     * @throws IOException if an I/O error occurs.
     */
    private void transferData(final InputStream input, final OutputStream output) throws IOException {
        byte[] buffer = new byte[AppConfig.FOUR_KB];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    /**
     * Sets the input file path for encryption or decryption.
     * @param inputFileName the input file name.
     */
    @Override
    public void setInput(final String inputFileName) {
        this.inputFileName = tmpDir + AppConfig.SLASH_SEPARATOR + inputFileName;
    }

    /**
     * Sets the AES key to be used for encryption or decryption.
     * @param aesKey the AES key.
     */
    @Override
    public void setAesKey(final byte[] aesKey) {
        this.aesKey = aesKey;
    }

    /**
     * Sets the RSA key, though not used in this handler.
     * @param rsaKey the RSA key (unused in this handler).
     */
    @Override
    public void setRsaKey(final String rsaKey) {
        this.rsaKey = rsaKey;
    }

    /**
     * Sets the initialization vector (IV) for AES encryption or decryption.
     * @param iv the initialization vector.
     */
    @Override
    public void setIv(final byte[] iv) {
        this.iv = iv;
    }

    /**
     * Returns the path to the output file.
     * @return the output file path.
     */
    @Override
    public String getOutput() {
        return outputFileName;
    }

    /**
     * Sets the output file name for encryption or decryption.
     * @param outputFileName the output file name.
     */
    private void setOutputFileName(final String outputFileName) {
        this.outputFileName = tmpDir + "/" + outputFileName;
    }
}
