package se.miun.dt133g.zkgitclient.crypto;

import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.function.Supplier;

/**
 * Factory class for creating instances of stream encryption handlers based on the specified encryption type.
 * This class provides a method to obtain the appropriate stream encryption handler (e.g., AES) based on a given type.
 * @author Leif Rogell
 */
public final class StreamEncryptionFactory {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());

    /**
     * Private constructor to prevent instantiation.
     */
    private StreamEncryptionFactory() { }

    /**
     * Retrieves the appropriate stream encryption handler based on the specified type.
     * This method supports creating an AES stream encryption handler based on the type provided.
     * If an invalid type is specified, an exception is thrown.
     * @param type the encryption type (e.g., "AES_STREAM").
     * @return the corresponding StreamEncryptionHandler instance.
     * @throws IllegalArgumentException if the specified encryption type is invalid.
     */
    public static StreamEncryptionHandler getStreamEncryptionHandler(final String type) {
        Map<String, Supplier<StreamEncryptionHandler>> handlers =
            Map.of(AppConfig.CRYPTO_AES_STREAM,
                   AesStreamEncryptionHandler::getInstance);

        return Optional.ofNullable(handlers.get(type))
            .map(Supplier::get)
            .orElseThrow(() -> new IllegalArgumentException("Invalid encryption type: " + type));
    }
}
