package se.miun.dt133g.zkgitclient.crypto;

public interface EncryptionHandler {

    void encrypt();
    void decrypt();
    void setInput(String input);
    void setAesKey(byte[] aesKey);
    void setRsaKey(String rsaKey);
    void setIv(byte[] iv);
    String getOutput();

}
