package se.miun.dt133g.zkgitclient.connection;

import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Logger;

public final class CheckConnection {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private String domain = AppConfig.API_DOMAIN;
    private boolean serverConnectivity = false;
    private boolean ipv6Connectivity = false;
    private boolean internetConnectivity = false;
    private boolean checksCompleted = false;

    protected CheckConnection() {
        isInternetAvailable();
        isIpv6Available();
    }

    private void isInternetAvailable() {
        try {
            InetAddress address = InetAddress.getByName(domain);
            this.internetConnectivity = true;
        } catch (IOException e) {
            this.internetConnectivity = false;
        }
    }

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

    private boolean canConnectOverIPv6(java.net.Inet6Address address) {
        try (Socket socket = new Socket()) {
            InetSocketAddress socketAddress = new InetSocketAddress(address, 80);
            socket.connect(socketAddress, 2000); 
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean getInternetConnectivity() {
        return internetConnectivity;
    }

    protected boolean getIpv6Connectivity() {
        return ipv6Connectivity;
    }

    protected boolean getServerConnectivity() {
        return serverConnectivity;
    }

    protected void setServerConnectivity(boolean serverConnectivity) {
        this.serverConnectivity = serverConnectivity;
    }

    protected void recheckConnectivity() {
        isInternetAvailable();
        isIpv6Available();
    }
}
