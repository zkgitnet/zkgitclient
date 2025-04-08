package se.miun.dt133g.zkgitclient.crypto;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface for stream encryption handlers that provide encryption and decryption operations
 * on input and output streams.
 * @author Leif Rogell
 */
public interface StreamEncryptionHandler {

    /**
     * Encrypts the data from the provided input stream and writes the encrypted data to the output stream.
     * @param inputStream the input stream containing the data to be encrypted.
     * @param outputStream the output stream where the encrypted data will be written.
     */
    void encryptStream(InputStream inputStream, OutputStream outputStream);

    /**
     * Decrypts the data from the provided input stream and writes the decrypted data to the output stream.
     * @param inputStream the input stream containing the encrypted data to be decrypted.
     * @param outputStream the output stream where the decrypted data will be written.
     */
    void decryptStream(InputStream inputStream, OutputStream outputStream);

    /**
     * Sets the AES key for encryption and decryption operations.
     * @param aesKey the AES key to be set.
     */
    void setAesKey(byte[] aesKey);

    /**
     * Sets the initialization vector (IV) for encryption and decryption operations.
     * @param iv the initialization vector to be set.
     */
    void setIv(byte[] iv);

}
