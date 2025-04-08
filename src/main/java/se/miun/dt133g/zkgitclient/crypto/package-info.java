/**
 * Provides encryption-related functionality for the application.
 *
 * This package includes a variety of encryption handlers that implement the
 * {@link se.miun.dt133g.zkgitclient.crypto.EncryptionHandler} interface. Each handler is responsible
 * for implementing a specific type of encryption, decryption, or cryptographic operation.
 * The {@link se.miun.dt133g.zkgitclient.crypto.EncryptionFactory} is used to create instances of
 * the appropriate encryption handler based on the specified type.
 *
 * Available encryption types:
 * <ul>
 *   <li>AES (Advanced Encryption Standard) - {@link se.miun.dt133g.zkgitclient.crypto.AesEncryptionHandler}</li>
 *   <li>PBKDF2 (Password-Based Key Derivation Function 2) -
 * {@link se.miun.dt133g.zkgitclient.crypto.Pbkdf2KeyGenerator}</li>
 *   <li>RSA (Rivest-Shamir-Adleman) Encryption - {@link se.miun.dt133g.zkgitclient.crypto.RsaEncryptionHandler}</li>
 *   <li>SHA-256 Hashing - {@link se.miun.dt133g.zkgitclient.crypto.Sha256HashHandler}</li>
 *   <li>RSA Signature - {@link se.miun.dt133g.zkgitclient.crypto.RsaSignatureHandler}</li>
 *   <li>SHA-256 File Hashing - {@link se.miun.dt133g.zkgitclient.crypto.Sha256HashFileHandler}</li>
 *   <li>Initialization Vector (IV) handling - {@link se.miun.dt133g.zkgitclient.crypto.IvHandler}</li>
 *   <li>File AES Encryption - {@link se.miun.dt133g.zkgitclient.crypto.AesFileEncryptionHandler}</li>
 * </ul>
 *
 * This package aims to abstract the cryptographic operations behind a common interface
 * to allow the client application to choose an encryption scheme dynamically based on configuration.
 *
 * The {@link se.miun.dt133g.zkgitclient.crypto.EncryptionFactory} is the primary entry point
 * for obtaining encryption handler instances based on encryption type configuration.
 */
package se.miun.dt133g.zkgitclient.crypto;
