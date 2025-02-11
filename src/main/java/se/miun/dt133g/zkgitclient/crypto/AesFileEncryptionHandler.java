package se.miun.dt133g.zkgitclient.crypto;

import se.miun.dt133g.zkgitclient.user.CurrentUserRepo;
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
import java.io.FileNotFoundException;

public final class AesFileEncryptionHandler implements EncryptionHandler {

    private static AesFileEncryptionHandler INSTANCE;

    private CurrentUserRepo currentRepo = CurrentUserRepo.getInstance();
    private String tmpDir = System.getProperty(AppConfig.JAVA_TMP);
    private String inputFileName;
    private String outputFileName;
    private String rsaKey;
    private byte[] aesKey;
    private byte[] iv;

    private AesFileEncryptionHandler() { }

    public static synchronized AesFileEncryptionHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AesFileEncryptionHandler();
        }
        return INSTANCE;
    }

    @Override public void encrypt() {
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

    @Override public void decrypt() {
        try {
            setOutputFileName(currentRepo.getRepoName());

            Cipher cipher = initializeCipher(Cipher.DECRYPT_MODE);

            try (FileInputStream fis = new FileInputStream(inputFileName);
                 FileOutputStream fos = new FileOutputStream(outputFileName);
                 CipherInputStream cis = new CipherInputStream(fis, cipher)) {

                transferData(cis, fos);
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } catch (Exception e) {
        }
    }

    private Cipher initializeCipher(final int mode) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(aesKey, AppConfig.CRYPTO_AES);
        GCMParameterSpec paramSpec = new GCMParameterSpec(AppConfig.CRYPTO_AES_TAG_LENGTH, iv);
        Cipher cipher = Cipher.getInstance(AppConfig.CRYPTO_AES_GCM);
        cipher.init(mode, keySpec, paramSpec);
        return cipher;
    }

    private void transferData(final InputStream input, final OutputStream output) throws IOException {
        byte[] buffer = new byte[AppConfig.FOUR_KB];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    @Override public void setInput(final String inputFileName) {
        this.inputFileName = tmpDir + AppConfig.SLASH_SEPARATOR + inputFileName;
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
        return outputFileName;
    }

    private void setOutputFileName(final String outputFileName) {
        this.outputFileName = tmpDir + "/" + outputFileName;
    }
}
