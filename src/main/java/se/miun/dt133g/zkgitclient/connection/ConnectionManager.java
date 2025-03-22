package se.miun.dt133g.zkgitclient.connection;

import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

public final class ConnectionManager {

    public static final ConnectionManager INSTANCE = new ConnectionManager();
    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private CheckConnection checkConnection;
    private HttpsConnection httpsConnection;
    private GitSocket gitSocket;

    private final String domain = AppConfig.API_DOMAIN;
    private String port = AppConfig.API_PORT;

    private String IP = null;

    private ConnectionManager() {
        this.checkConnection = new CheckConnection();
        if (checkConnection.getInternetConnectivity()) {
            this.httpsConnection = new HttpsConnection();
        }
        this.gitSocket = GitSocket.INSTANCE;
    }

    public String sendGetPostRequest(final Map<String, String> postDataParams) {
        return this.httpsConnection.sendGetPostRequest(domain, port, postDataParams);
    }

    public String sendPostRequest(final Map<String, String> postDataParams)
        throws NullPointerException {
        LOGGER.finer("Sending POST Request to: https://" + domain + ":" + port);
        return this.httpsConnection.sendPostRequest(domain, port, postDataParams);
    }

    public String sendFilePostRequest(final Map<String, String> postDataParams,
                                      final InputStream fileInputStream,
                                      final String fileName) {
        return this.httpsConnection.sendFilePostRequest(IP, port, fileInputStream, fileName, postDataParams);
    }

    public void setIP(final String ipv4, final String ipv6) {
        this.IP = checkConnection.getIpv6Connectivity() ? AppConfig.FORWARD_SQUARE_BRACKET + ipv6 + AppConfig.BACKWARD_SQUARE_BRACKET : ipv4;
        LOGGER.config("Set remote server IP to: " + IP);
    }

    public boolean getServerConnectivity() {
        return checkConnection.getServerConnectivity();
    }

    public boolean getIpv6Connectivity() {
        return checkConnection.getIpv6Connectivity();
    }

    public boolean getInternetConnectivity() {
        return checkConnection.getInternetConnectivity();
    }

    public void setServerConnectivity(boolean serverConnectivity) {
        checkConnection.setServerConnectivity(serverConnectivity);
    }

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

    public int getGitPort() {
        return gitSocket.getGitPort();
    }
}
