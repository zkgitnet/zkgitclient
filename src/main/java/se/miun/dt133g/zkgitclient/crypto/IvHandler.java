package se.miun.dt133g.zkgitclient.crypto;

import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.Utils;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.Arrays;
import java.util.logging.Logger;
import java.security.SecureRandom;

public final class IvHandler implements EncryptionHandler {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private static IvHandler INSTANCE;
    private Utils utils = Utils.getInstance();

    private String input;
    private String output;
    private String rsaKey;
    private byte[] aesKey;
    private byte[] iv;

    private IvHandler() { }

    public static IvHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new IvHandler();
        }
        return INSTANCE;
    }

    @Override public void encrypt() {
        this.iv = new byte[12];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(this.iv);
        output = Arrays.toString(this.iv).replace(" ", "");
    }

    @Override public void decrypt() { }

    @Override public void setInput(final String input) {
        this.input = input;
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
        return output;
    }
}
