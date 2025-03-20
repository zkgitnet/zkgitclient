package se.miun.dt133g.zkgitclient.crypto;

import java.io.InputStream;
import java.io.OutputStream;

public interface StreamEncryptionHandler {

    void encryptStream(InputStream inputStream, OutputStream outputStream);
    void decryptStream(InputStream inputStream, OutputStream outputStream);
    void setAesKey(byte[] aesKey);
    void setIv(byte[] iv);

}
