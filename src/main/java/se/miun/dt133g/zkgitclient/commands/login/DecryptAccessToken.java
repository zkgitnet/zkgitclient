package se.miun.dt133g.zkgitclient.commands.login;

import se.miun.dt133g.zkgitclient.crypto.EncryptionHandler;
import se.miun.dt133g.zkgitclient.crypto.EncryptionFactory;
import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.Map;
import java.util.Optional;

public final class DecryptAccessToken extends BaseCommandLogin implements Command {

    @Override public String execute() {
        return Optional.ofNullable(readPassword(AppConfig.INFO_ENTER_PASSWORD, AppConfig.INFO_INVALID_PASSWORD, AppConfig.REGEX_PASSWORD))
            .filter(password -> !AppConfig.ERROR_KEY.equals(password))
            .map(password -> {
                    pbkdf2Handler.encrypt();
                    aesHandler.setAesKey(credentials.getPbkdf2Hash());
                    aesHandler.setIv(credentials.getIv());
                    aesHandler.setInput(credentials.getEncPrivRsa());
                    aesHandler.decrypt();

                    credentials.setPrivRsa(aesHandler.getOutput());
                    //System.out.println(aesHandler.getOutput());

                    rsaHandler.setRsaKey(aesHandler.getOutput());
                    rsaHandler.setInput(credentials.getEncAccessToken());
                    rsaHandler.decrypt();

                    String decryptedAccessToken = rsaHandler.getOutput();

                    //System.out.println("DecryptedAccessToken: " + decryptedAccessToken);

                    if (decryptedAccessToken.length() == AppConfig.CRYPTO_TOKEN_LENGTH) {

                        credentials.setAccessToken(decryptedAccessToken);
                        
                        Map<String, String> postData = Map.of(
                                                              AppConfig.COMMAND_KEY,
                                                              AppConfig.COMMAND_REQUEST_AES_KEY,
                                                              AppConfig.CREDENTIAL_ACCOUNT_NR,
                                                              credentials.getAccountNumber(),
                                                              AppConfig.CREDENTIAL_USERNAME,
                                                              credentials.getUsername(),
                                                              AppConfig.CREDENTIAL_ACCESS_TOKEN,
                                                              credentials.getAccessToken()
                                                              );
                        return prepareAndSendPostRequest(postData);
                    } else {
                        System.out.println(AppConfig.ERROR_KEY
                                           + AppConfig.COLON_SEPARATOR
                                           + AppConfig.SPACE_SEPARATOR
                                           + AppConfig.ERROR_INVALID_PASSWORD);
                        return AppConfig.NONE;
                    }
                })
            .orElse(AppConfig.COMMAND_EXIT);
    }
}
