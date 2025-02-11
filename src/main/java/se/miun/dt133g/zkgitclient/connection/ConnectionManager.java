package se.miun.dt133g.zkgitclient.connection;

import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.io.File;
import java.util.Map;
import java.util.Scanner;

public final class ConnectionManager {

    public static final ConnectionManager INSTANCE = new ConnectionManager();
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
        return this.httpsConnection.sendPostRequest(domain, port, postDataParams);
    }

    public String sendFilePostRequest(final File file, final Map<String, String> postDataParams) {
        return this.httpsConnection.sendFilePostRequest(IP, port, file, postDataParams);
    }

    public void setIP(final String ipv4, final String ipv6) {
        this.IP = checkConnection.getIpv6Connectivity() ? AppConfig.FORWARD_SQUARE_BRACKET + ipv6 + AppConfig.BACKWARD_SQUARE_BRACKET : ipv4;
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
                    System.out.println(AppConfig.INFO_INVALID_PORT_NUMBER);
                } else {
                    gitSocket.restartServer(portInput);
                    this.gitSocket = gitSocket.INSTANCE;
                    if (gitSocket.getGitPort() > 0) {
                        System.out.println(AppConfig.INFO_PORT_SET + input);
                        port = input;
                        break;
                    } else {
                        System.out.println(AppConfig.ERROR_PORT_NOT_FREE);
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println(AppConfig.ERROR_INVALID_PORT);
            }
        }
    }

    public int getGitPort() {
        return gitSocket.getGitPort();
    }
}
