package se.miun.dt133g.zkgitclient.crypto;

import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.function.Supplier;

/**
 * Factory class responsible for providing instances of various encryption handlers.
 * This class allows dynamic creation of encryption handler objects based on the specified encryption type.
 * The supported encryption types are configured in the {@link AppConfig} class and can be used to instantiate
 * specific encryption implementations such as AES, RSA, PBKDF2, SHA-256, and others.
 * @author Leif Rogell
 */
public final class EncryptionFactory {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());

    /**
     * Private constructor to prevent instantiation.
     */
    private EncryptionFactory() { }

    /**
     * Retrieves an appropriate encryption handler based on the given encryption type.
     * This method checks the provided encryption type against a predefined map of supported types
     * and returns the corresponding handler. If the type is invalid, an {@link IllegalArgumentException} is thrown.
     * @param type the encryption type (e.g., AES, RSA, SHA-256).
     * @return an instance of the corresponding {@link EncryptionHandler}.
     * @throws IllegalArgumentException if the encryption type is invalid.
     */
    public static EncryptionHandler getEncryptionHandler(final String type) {
        Map<String, Supplier<EncryptionHandler>> handlers = Map.of(AppConfig.CRYPTO_AES,
                                                                   AesEncryptionHandler::getInstance,
                                                                   AppConfig.CRYPTO_PBKDF2,
                                                                   Pbkdf2KeyGenerator::getInstance,
                                                                   AppConfig.CRYPTO_RSA,
                                                                   RsaEncryptionHandler::getInstance,
                                                                   AppConfig.CRYPTO_SHA_256,
                                                                   Sha256HashHandler::getInstance,
                                                                   AppConfig.CRYPTO_RSA_SIGNATURE,
                                                                   RsaSignatureHandler::getInstance,
                                                                   AppConfig.CRYPTO_SHA_256_FILE,
                                                                   Sha256HashFileHandler::getInstance,
                                                                   AppConfig.CRYPTO_IV,
                                                                   IvHandler::getInstance,
                                                                   "fileAes",
                                                                   AesFileEncryptionHandler::getInstance);

        return Optional.ofNullable(handlers.get(type))
            .map(Supplier::get)
            .orElseThrow(() -> new IllegalArgumentException("Invalid encryption type: " + type));
    }
}
