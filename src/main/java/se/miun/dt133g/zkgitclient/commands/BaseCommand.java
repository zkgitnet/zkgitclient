package se.miun.dt133g.zkgitclient.commands;

import se.miun.dt133g.zkgitclient.connection.ConnectionManager;
import se.miun.dt133g.zkgitclient.crypto.EncryptionHandler;
import se.miun.dt133g.zkgitclient.crypto.EncryptionFactory;
import se.miun.dt133g.zkgitclient.menu.MenuTerminal;
import se.miun.dt133g.zkgitclient.user.UserCredentials;
import se.miun.dt133g.zkgitclient.user.CurrentUserRepo;
import se.miun.dt133g.zkgitclient.support.Utils;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.io.IOException;
import java.io.File;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.EndOfFileException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public abstract class BaseCommand {

    protected final UserCredentials credentials = UserCredentials.getInstance();
    protected final CurrentUserRepo currentRepo = CurrentUserRepo.getInstance();
    protected EncryptionHandler rsaSignHandler = EncryptionFactory.getEncryptionHandler(AppConfig.CRYPTO_RSA_SIGNATURE);
    protected final Utils utils = Utils.getInstance();
    protected final ConnectionManager conn = ConnectionManager.INSTANCE;
    private final Scanner scanner = new Scanner(System.in);

    protected String prepareAndSendPostRequest(final Map<String, String> postData) {
         return conn.sendPostRequest(postData);
    }

    protected String prepareAndSendGetPostRequest(final Map<String, String> postData) {
        return conn.sendGetPostRequest(postData);
    }

    protected String prepareAndSendFilePostRequest(final File file, final Map<String, String> postData) {
        return conn.sendFilePostRequest(file, postData);
    }

    protected String createErrorResponse(final String errorMessage) {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put(AppConfig.ERROR_KEY, errorMessage);
        return utils.mapToString(responseMap);
    }

    protected String readUserInput(final String inputPrompt, final String errorMessage, final String regexPattern) {
        try {
            for (int attempts = 0; attempts < AppConfig.MAX_NUM_INPUT_ATTEMPTS; attempts++) {
                try {
                    String input = Optional.ofNullable(MenuTerminal.INSTANCE.getLineReader().readLine(inputPrompt))
                        .map(String::trim)
                        .map(t -> t.replace(AppConfig.SPACE_SEPARATOR, AppConfig.NONE))
                        .orElse(AppConfig.ZERO_STRING);

                    if (AppConfig.ZERO_STRING.equals(input) || Pattern.matches(regexPattern, input)) {
                        return AppConfig.ZERO_STRING.equals(input) ? AppConfig.ERROR_KEY : input;
                    } else {
                        System.out.println(errorMessage);
                    }
                } catch (UserInterruptException | EndOfFileException e) {;
                    return AppConfig.ERROR_KEY;
                }
            }
        } catch (Exception e) {
            return AppConfig.ERROR_KEY;
        }

        return AppConfig.ERROR_KEY;
    }
    
    protected String handleInvalidInput(final String errorMessage) {
        System.out.println(errorMessage);
        return AppConfig.ERROR_KEY;
    }

    protected void extractUserCredentials(final Map<String, String> commandArguments) {
        Map<String, Consumer<String>> setters = Map.of(
                                                        AppConfig.CREDENTIAL_ENC_ACCESS_TOKEN,
                                                        credentials::setEncAccessToken,
                                                        AppConfig.CREDENTIAL_ENC_PRIV_RSA_KEY,
                                                        credentials::setEncPrivRsaJson,
                                                        AppConfig.CREDENTIAL_ENC_AES_KEY,
                                                        credentials::setEncAesKey,
                                                        AppConfig.CREDENTIAL_USERNAME,
                                                        credentials::setUsername
                                                        );

         setters.forEach((key, setter) ->
                         Optional.ofNullable(commandArguments.get(key)).ifPresent(setter)
                        );

         Optional.ofNullable(commandArguments.get(AppConfig.DB_IPV4))
             .ifPresent(ipv4 -> ConnectionManager.INSTANCE.setIP(ipv4, commandArguments.get(AppConfig.DB_IPV6)));
     }

    protected Map<String, String> extractResponseToMap(final String response) {
        Pattern pattern = Pattern.compile(AppConfig.REGEX_COMMAND_RESPONSE);

        return Stream.of(response)
            .map(r -> r.startsWith(AppConfig.FORWARD_CURLY_BRACKET) && r.endsWith(AppConfig.BACKWARD_CURLY_BRACKET)
                 ? r.substring(1, r.length() - 1)
                 : r)
            .flatMap(r -> Stream.of(r.split(AppConfig.COMMA_SEPARATOR)))
            .map(String::trim)
            .map(pair -> {
                    Matcher matcher = pattern.matcher(pair);
                    if (matcher.matches()) {
                        return Map.entry(matcher.group(1), matcher.group(2));
                    } else {
                        return null;
                    }
                })
            .filter(entry -> entry != null)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
