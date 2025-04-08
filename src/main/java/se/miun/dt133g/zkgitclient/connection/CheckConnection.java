package se.miun.dt133g.zkgitclient.connection;

import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * Provides functionality to check the network connectivity status, including server, internet,
 * and IPv6 connectivity.
 * This class is responsible for verifying whether the client can connect to the internet,
 * whether the server is reachable, and if IPv6 connectivity is available.
 * It performs these checks during instantiation and can recheck the status when needed.
 * @author Leif Rogell
 */
public final class CheckConnection {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private String domain = AppConfig.API_DOMAIN;
    private boolean serverConnectivity = false;
    private boolean ipv6Connectivity = false;
    private boolean internetConnectivity = false;
    private boolean checksCompleted = false;

    /**
     * Constructs a CheckConnection object and performs initial checks for internet and IPv6 connectivity.
     */
    protected CheckConnection() {
        isInternetAvailable();
        isIpv6Available();
    }

    /**
     * Checks if the internet is available by trying to resolve the domain name.
     */
    private void isInternetAvailable() {
        try {
            InetAddress address = InetAddress.getByName(domain);
            this.internetConnectivity = true;
        } catch (IOException e) {
            this.internetConnectivity = false;
        }
    }

    /**
     * Checks if IPv6 connectivity is available by attempting to resolve the domain and
     * verifying that one of the addresses is IPv6 and can be connected to.
     */
    private void isIpv6Available() {
        try {
            InetAddress[] addresses = InetAddress.getAllByName(domain);
            for (InetAddress address : addresses) {
                if (address instanceof java.net.Inet6Address) {
                    if (canConnectOverIPv6((java.net.Inet6Address) address)) {
                        this.ipv6Connectivity = true;
                    }
                }
            }
        } catch (UnknownHostException e) {
            this.ipv6Connectivity = false;
        }
    }

    /**
     * Attempts to connect over IPv6 to a given address on port 80 to check connectivity.
     * @param address The IPv6 address to connect to.
     * @return {@code true} if the connection was successful, {@code false} otherwise.
     */
    private boolean canConnectOverIPv6(final java.net.Inet6Address address) {
        try (Socket socket = new Socket()) {
            InetSocketAddress socketAddress = new InetSocketAddress(address,
                                                                    AppConfig.HTTP_PORT);
            socket.connect(socketAddress, AppConfig.TIMEOUT_LIMIT);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns the current internet connectivity status.
     * @return {@code true} if internet connectivity is available, {@code false} otherwise.
     */
    protected boolean getInternetConnectivity() {
        return internetConnectivity;
    }

    /**
     * Returns the current IPv6 connectivity status.
     * @return {@code true} if IPv6 connectivity is available, {@code false} otherwise.
     */
    protected boolean getIpv6Connectivity() {
        return ipv6Connectivity;
    }

    /**
     * Returns the current server connectivity status.
     * @return {@code true} if the server is reachable, {@code false} otherwise.
     */
    protected boolean getServerConnectivity() {
        return serverConnectivity;
    }

    /**
     * Sets the server connectivity status.
     * @param serverConnectivity The new server connectivity status.
     */
    protected void setServerConnectivity(final boolean serverConnectivity) {
        this.serverConnectivity = serverConnectivity;
    }

    /**
     * Rechecks the internet and IPv6 connectivity statuses.
     */
    protected void recheckConnectivity() {
        isInternetAvailable();
        isIpv6Available();
    }
}
