package se.miun.dt133g.zkgitclient.commands;

import se.miun.dt133g.zkgitclient.connection.ConnectionManager;
import se.miun.dt133g.zkgitclient.crypto.EncryptionHandler;
import se.miun.dt133g.zkgitclient.crypto.EncryptionFactory;
import se.miun.dt133g.zkgitclient.menu.MenuTerminal;
import se.miun.dt133g.zkgitclient.user.UserCredentials;
import se.miun.dt133g.zkgitclient.user.CurrentUserRepo;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.Utils;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import org.jline.reader.UserInterruptException;
import org.jline.reader.EndOfFileException;

import java.io.InputStream;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The {@code BaseCommand} class provides common functionality for all command classes.
 * It handles HTTP requests, user input, error responses, and user credential extraction.
 * This class is meant to be extended by specific command implementations, such as login, file operations, etc.
 * @author Leif Rogell
 */
public abstract class BaseCommand {

    protected final UserCredentials credentials = UserCredentials.getInstance();
    protected final CurrentUserRepo currentRepo = CurrentUserRepo.getInstance();
    protected EncryptionHandler rsaSignHandler = EncryptionFactory.getEncryptionHandler(AppConfig.CRYPTO_RSA_SIGNATURE);
    protected final Utils utils = Utils.getInstance();
    protected final ConnectionManager conn = ConnectionManager.INSTANCE;
    private final Scanner scanner = new Scanner(System.in);
    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());

    /**
     * Sends a POST request to the server with the provided data.
     * @param postData The data to send in the POST request.
     * @return The server response as a string.
     */
    protected String prepareAndSendPostRequest(final Map<String, String> postData) {
         return conn.sendPostRequest(postData);
    }

    /**
     * Sends a GET POST request to the server with the provided data.
     * @param postData The data to send in the GET POST request.
     * @return The input stream of the server response.
     */
    protected InputStream prepareAndSendGetPostRequest(final Map<String, String> postData) {
        return conn.sendGetPostRequest(postData);
    }

    /**
     * Sends a POST request with file data to the server.
     * @param postData The data to send in the POST request.
     * @param fileInputStream The file input stream.
     * @param fileName The name of the file being uploaded.
     * @return The server response as a string.
     */
    protected String prepareAndSendFilePostRequest(final Map<String, String> postData,
                                                   InputStream fileInputStream, final String fileName) {
        return conn.sendFilePostRequest(postData, fileInputStream, fileName);
    }

    /**
     * Creates an error response string based on the given error message.
     * @param errorMessage The error message.
     * @return The error response as a string.
     */
    protected String createErrorResponse(final String errorMessage) {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put(AppConfig.ERROR_KEY, errorMessage);
        return utils.mapToString(responseMap);
    }

    /**
     * Reads user input from the console, ensuring it matches the provided regex pattern.
     * Handles user interruptions and end-of-file exceptions gracefully.
     * @param inputPrompt The prompt to display to the user.
     * @param errorMessage The error message to display if input is invalid.
     * @param regexPattern The regex pattern that the input must match.
     * @return The user input if valid, or {@link AppConfig#ERROR_KEY} if invalid or interrupted.
     */
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
                } catch (UserInterruptException | EndOfFileException e) {
                    return AppConfig.ERROR_KEY;
                }
            }
        } catch (Exception e) {
            return AppConfig.ERROR_KEY;
        }

        return AppConfig.ERROR_KEY;
    }

    /**
     * Handles invalid user input by displaying the error message and returning an error response.
     * @param errorMessage The error message to display.
     * @return {@link AppConfig#ERROR_KEY}.
     */
    protected String handleInvalidInput(final String errorMessage) {
        System.out.println(errorMessage);
        return AppConfig.ERROR_KEY;
    }

    /**
     * Extracts user credentials from the given command arguments and sets them
     * into the user credentials and current repo instances.
     * @param commandArguments The command arguments containing user credentials.
     */
    protected void extractUserCredentials(final Map<String, String> commandArguments) {
        Map<String, Consumer<String>> setters = Map.of(AppConfig.CREDENTIAL_ENC_ACCESS_TOKEN,
                                                       credentials::setEncAccessToken,
                                                       AppConfig.CREDENTIAL_ENC_PRIV_RSA_KEY,
                                                       credentials::setEncPrivRsaJson,
                                                       AppConfig.CREDENTIAL_ENC_AES_KEY,
                                                       credentials::setEncAesKey,
                                                       AppConfig.CREDENTIAL_USERNAME,
                                                       credentials::setUsername,
                                                       AppConfig.DB_IV,
                                                       currentRepo::setIv,
                                                       AppConfig.REPO_SIGNATURE,
                                                       currentRepo::setRepoSignature);

         setters.forEach((key, setter) ->
                         Optional.ofNullable(commandArguments.get(key)).ifPresent(setter));

         Optional.ofNullable(commandArguments.get(AppConfig.DB_IPV4))
             .ifPresent(ipv4 -> ConnectionManager.INSTANCE.setIP(ipv4, commandArguments.get(AppConfig.DB_IPV6)));
    }

    /**
     * Extracts key-value pairs from the server response string and maps them into a {@link Map}.
     * @param response The server response string to parse.
     * @return A {@link Map} containing the extracted key-value pairs.
     */
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
