package se.miun.dt133g.zkgitclient.crypto;

import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.function.Supplier;

public final class StreamEncryptionFactory {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());

    private StreamEncryptionFactory() { }

    public static StreamEncryptionHandler getStreamEncryptionHandler(final String type) {
        Map<String, Supplier<StreamEncryptionHandler>> handlers = Map.of(AppConfig.CRYPTO_AES_STREAM,
                                                                         AesStreamEncryptionHandler::getInstance);

        return Optional.ofNullable(handlers.get(type))
            .map(Supplier::get)
            .orElseThrow(() -> new IllegalArgumentException("Invalid encryption type: " + type));
    }
}
