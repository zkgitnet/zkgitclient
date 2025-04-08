package se.miun.dt133g.zkgitclient.connection;

import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Manages the network connections related to the application, including checking
 * connectivity, sending HTTP requests, and managing the GitSocket.
 * This class handles the server and IPv6 connectivity checks, provides methods
 * to send HTTP requests (GET, POST, and file uploads), and allows the configuration
 * of the Git server port.
 * @author Leif Rogell
 */
public final class ConnectionManager {

    public static final ConnectionManager INSTANCE = new ConnectionManager();
    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private CheckConnection checkConnection;
    private HttpsConnection httpsConnection;
    private GitSocket gitSocket;

    private final String domain = AppConfig.API_DOMAIN;
    private String port = AppConfig.API_PORT;
    private String IP = null;

    /**
     * Private constructor to initialize the ConnectionManager, checking internet
     * connectivity and setting up the HTTPS connection and GitSocket.
     */
    private ConnectionManager() {
        this.checkConnection = new CheckConnection();
        if (checkConnection.getInternetConnectivity()) {
            this.httpsConnection = new HttpsConnection();
        }
        this.gitSocket = GitSocket.INSTANCE;
    }

    /**
     * Sends a GET or POST request with the provided parameters.
     * @param postDataParams the data parameters to be sent with the request.
     * @return InputStream containing the response from the request.
     */
    public InputStream sendGetPostRequest(final Map<String, String> postDataParams) {
        return this.httpsConnection.sendGetPostRequest(domain, port, postDataParams);
    }

    /**
     * Sends a POST request with the provided parameters.
     * @param postDataParams the data parameters to be sent with the request.
     * @return the response as a String.
     * @throws NullPointerException if any required value is null.
     */
    public String sendPostRequest(final Map<String, String> postDataParams)
        throws NullPointerException {
        LOGGER.finer("Sending POST Request to: https://" + domain + ":" + port);
        return this.httpsConnection.sendPostRequest(domain, port, postDataParams);
    }

    /**
     * Sends a POST request with file upload and the provided parameters.
     * @param postDataParams the data parameters to be sent with the request.
     * @param fileInputStream the InputStream of the file to be uploaded.
     * @param fileName the name of the file to be uploaded.
     * @return the response as a String.
     */
    public String sendFilePostRequest(final Map<String, String> postDataParams,
                                      final InputStream fileInputStream,
                                      final String fileName) {
        return this.httpsConnection.sendFilePostRequest(IP, port, fileInputStream, fileName, postDataParams);
    }

    /**
     * Sets the IP address (either IPv4 or IPv6) to be used for the connection.
     * @param ipv4 the IPv4 address to set.
     * @param ipv6 the IPv6 address to set.
     */
    public void setIP(final String ipv4, final String ipv6) {
        this.IP = checkConnection.getIpv6Connectivity() ? AppConfig.FORWARD_SQUARE_BRACKET + ipv6
            + AppConfig.BACKWARD_SQUARE_BRACKET : ipv4;
        LOGGER.config("Set remote server IP to: " + IP);
    }

    /**
     * Returns the current server connectivity status.
     * @return {@code true} if the server is reachable, {@code false} otherwise.
     */
    public boolean getServerConnectivity() {
        return checkConnection.getServerConnectivity();
    }

    /**
     * Returns the current IPv6 connectivity status.
     * @return {@code true} if IPv6 connectivity is available, {@code false} otherwise.
     */
    public boolean getIpv6Connectivity() {
        return checkConnection.getIpv6Connectivity();
    }

    /**
     * Returns the current internet connectivity status.
     * @return {@code true} if internet connectivity is available, {@code false} otherwise.
     */
    public boolean getInternetConnectivity() {
        return checkConnection.getInternetConnectivity();
    }

    /**
     * Sets the server connectivity status.
     * @param serverConnectivity the new server connectivity status.
     */
    public void setServerConnectivity(final boolean serverConnectivity) {
        checkConnection.setServerConnectivity(serverConnectivity);
    }

    /**
     * Prompts the user to enter a port number and sets the GitSocket port accordingly.
     * Continues to prompt until a valid, available port is provided.
     */
    public void setGitPort() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print(AppConfig.INFO_ENTER_PORT_NUMBER);
            String input = scanner.nextLine();

            if (AppConfig.ZERO_STRING.equals(input)) {
                break;
            }

            try {
                int portInput = Integer.parseInt(input);
                if (portInput < 0 || portInput > AppConfig.MAX_PORT_NUMBER) {
                    LOGGER.warning(AppConfig.INFO_INVALID_PORT_NUMBER);
                } else {
                    gitSocket.restartServer(portInput);
                    this.gitSocket = gitSocket.INSTANCE;
                    if (gitSocket.getGitPort() > 0) {
                        LOGGER.info(AppConfig.INFO_PORT_SET + input);
                        break;
                    } else {
                        LOGGER.warning(AppConfig.ERROR_PORT_NOT_FREE);
                    }
                }
            } catch (NumberFormatException e) {
                LOGGER.warning(AppConfig.ERROR_INVALID_PORT);
            }
        }
    }

    /**
     * Returns the current Git server port.
     * @return the Git server port.
     */
    public int getGitPort() {
        return gitSocket.getGitPort();
    }
}
