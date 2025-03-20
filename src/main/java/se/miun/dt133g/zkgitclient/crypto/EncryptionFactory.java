package se.miun.dt133g.zkgitclient.crypto;

import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.function.Supplier;

public final class EncryptionFactory {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());

    private EncryptionFactory() { }

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
                                                                   Sha256HashFileHandler::getInstance);

        return Optional.ofNullable(handlers.get(type))
            .map(Supplier::get)
            .orElseThrow(() -> new IllegalArgumentException("Invalid encryption type: " + type));
    }
}
