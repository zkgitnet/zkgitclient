package se.miun.dt133g.zkgitclient.crypto;

import se.miun.dt133g.zkgitclient.user.CurrentUserRepo;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

public final class AesStreamEncryptionHandler implements StreamEncryptionHandler {

    private static AesStreamEncryptionHandler INSTANCE;
    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());

    private CurrentUserRepo currentRepo = CurrentUserRepo.getInstance();
    private byte[] aesKey;
    private byte[] iv;

    private AesStreamEncryptionHandler() { }

    public static synchronized AesStreamEncryptionHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AesStreamEncryptionHandler();
        }
        return INSTANCE;
    }

    @Override
    public void encryptStream(InputStream inputStream, OutputStream outputStream) {
        processStream(Cipher.ENCRYPT_MODE, inputStream, outputStream);
    }

    @Override
    public void decryptStream(InputStream inputStream, OutputStream outputStream) {
        processStream(Cipher.DECRYPT_MODE, inputStream, outputStream);
    }

    private void processStream(int mode, InputStream inputStream, OutputStream outputStream) {
        try {
            String operation = (mode == Cipher.ENCRYPT_MODE) ? "encryption" : "decryption";
            LOGGER.fine("Starting " + operation + " of repo");

            Cipher cipher = Cipher.getInstance(AppConfig.CRYPTO_AES_GCM);
            SecretKey secretKey = new SecretKeySpec(aesKey, AppConfig.CRYPTO_AES);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(AppConfig.CRYPTO_AES_TAG_LENGTH, iv);
            cipher.init(mode, secretKey, gcmSpec);

            byte[] buffer = new byte[8 * AppConfig.ONE_KB];
            int bytesRead;

            if (mode == Cipher.ENCRYPT_MODE) {
                try (CipherOutputStream cipherOut = new CipherOutputStream(outputStream, cipher)) {
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        cipherOut.write(buffer, 0, bytesRead);
                    }
                }
            } else {
                try (CipherInputStream cipherIn = new CipherInputStream(inputStream, cipher)) {
                    while ((bytesRead = cipherIn.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
            }
            outputStream.close();

            LOGGER.fine("Completed " + operation + " of repo");
        } catch (Exception e) {
            LOGGER.severe("Could not " + ((mode == Cipher.ENCRYPT_MODE) ? "encrypt" : "decrypt") + " repo: " + e.getMessage());
        }
    }

    @Override
    public void setAesKey(final byte[] aesKey) {
        this.aesKey = aesKey;
    }

    @Override
    public void setIv(final byte[] iv) {
        this.iv = iv;
    }
}
