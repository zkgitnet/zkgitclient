package se.miun.dt133g.zkgitclient.connection;

import se.miun.dt133g.zkgitclient.commands.CommandManager;
import se.miun.dt133g.zkgitclient.user.CurrentUserRepo;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Manages the Git server socket, handling client connections and executing Git commands.
 * Provides methods for starting, stopping, and configuring the server, as well as handling
 * client requests and interacting with the CommandManager.
 * @author Leif Rogell
 */
public final class GitSocket {

    public static GitSocket INSTANCE = new GitSocket();
    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private final String ERROR_MESSAGE = AppConfig.ERROR_KEY
        + AppConfig.COLON_SEPARATOR + AppConfig.SPACE_SEPARATOR;

    private int port;
    private ServerSocket serverSocket;
    private Thread serverThread;

    /**
     * Default constructor initializing the server with the default port.
     */
    private GitSocket() {
        this(AppConfig.GIT_PORT);
    }

    /**
     * Constructor initializing the server with a specified port.
     * @param port the port number for the Git server to listen on.
     */
    private GitSocket(final int port) {
        this.port = port;
        LOGGER.config("Setting Git Port: " + port);
        serverThread = new Thread(this::startServer);
        serverThread.start();
    }

    /**
     * Starts the server and listens for incoming client connections. Each client connection
     * is handled in a new thread. If the server is interrupted, it stops accepting new clients.
     */
    private void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            this.serverSocket = serverSocket;
            LOGGER.info("Starting Git Server...");

            while (!serverThread.isInterrupted()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(() -> handleClient(clientSocket)).start();
                } catch (Exception e) {
                    if (serverThread.isInterrupted()) {
                        this.port = 0;
                        break;
                    }
                    this.port = 0;
                    LOGGER.severe(AppConfig.ERROR_SOCKET_INTERRUPT);
                }
            }
        } catch (Exception e) {
            this.port = 0;
            LOGGER.severe(AppConfig.ERROR_NO_FREE_PORT);
        }
    }

    /**
     * Restarts the server with a new port.
     * @param newPort the new port number to set for the server.
     */
    protected synchronized void restartServer(final int newPort) {
        if (INSTANCE != null) {
            stopServer();
        }
        INSTANCE = new GitSocket(newPort);
    }

    /**
     * Handles incoming client connections, processes Git commands, and sends the response back
     * to the client. Commands are parsed and executed using the CommandManager.
     * @param clientSocket the client socket for communication.
     */
    private void handleClient(final Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                LOGGER.finest("Received command: " + inputLine);
                String[] inputItems = inputLine.split(AppConfig.SPACE_SEPARATOR);
                if (inputItems.length > 1) {
                    CurrentUserRepo.getInstance()
                        .setRepoName(inputItems[1].substring(inputItems[1].lastIndexOf("/") + 1));
                    CurrentUserRepo.getInstance().setRepoPath(inputItems[1]);
                    if (inputItems.length > 2) {
                        CurrentUserRepo.getInstance().setRepoSignature(inputItems[2]);
                    }
                }
                Map<String, String> responseMap = new HashMap<>();
                responseMap = CommandManager.INSTANCE.executeCommand(inputItems[0]);

                LOGGER.fine("GitSocketResponseMap: " + responseMap.toString()
                            + ", Command: " + inputLine.split(" ")[0]);

                if (responseMap.containsKey(AppConfig.COMMAND_SUCCESS)) {
                    out.println(responseMap.toString());
                } else {
                    out.println(responseMap.get(AppConfig.ERROR_KEY));
                }
            }
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (Exception e) {
                LOGGER.severe(e.getMessage());
            }
        }
    }

    /**
     * Stops the server and closes any open server socket and associated threads.
     */
    protected void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (serverThread != null) {
                serverThread.interrupt();
            }
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
        }
    }

    /**
     * Returns the current port the Git server is listening on.
     * @return the current Git server port.
     */
    protected int getGitPort() {
        return port;
    }

    /**
     * Sets a new port for the Git server and restarts it.
     * @param port the new port number to set for the server.
     */
    protected void setGitPort(final int port) {
        this.port = port;
        LOGGER.config("Setting Git Port: " + port);
        startServer();
    }
}
