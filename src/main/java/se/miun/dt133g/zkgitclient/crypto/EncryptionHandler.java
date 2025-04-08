package se.miun.dt133g.zkgitclient.crypto;

/**
 * Interface for encryption handlers.
 * This interface defines the common methods that all encryption handlers should implement.
 * These methods allow encryption, decryption, setting inputs, keys,
 * initialization vectors (IVs), and retrieving the output.
 * @author Leif Rogell
 */
public interface EncryptionHandler {

    /**
     * Encrypts the provided input using the specific encryption algorithm.
     * This method performs encryption based on the algorithm defined in the implementing class.
     */
    void encrypt();

    /**
     * Decrypts the previously encrypted data.
     * This method reverses the encryption and returns the original data.
     */
    void decrypt();

    /**
     * Sets the input data to be encrypted or decrypted.
     * This method defines the data to be processed by the encryption or decryption operation.
     * @param input the data to be encrypted or decrypted.
     */
    void setInput(String input);

    /**
     * Sets the AES key for encryption or decryption.
     * This method provides the AES key to be used in the encryption or decryption process.
     * @param aesKey the AES encryption key.
     */
    void setAesKey(byte[] aesKey);

    /**
     * Sets the RSA key for encryption or decryption.
     * This method provides the RSA key to be used for RSA-based encryption or decryption.
     * @param rsaKey the RSA key used for encryption or decryption.
     */
    void setRsaKey(String rsaKey);

     /**
     * Sets the initialization vector (IV) for encryption or decryption.
     * This method sets the IV which is necessary for certain encryption algorithms like AES-GCM.
     * @param iv the initialization vector.
     */
    void setIv(byte[] iv);

    /**
     * Retrieves the result of the encryption or decryption process.
     * This method returns the encrypted or decrypted data as a string.
     * @return the encrypted or decrypted output.
     */
    String getOutput();
}
