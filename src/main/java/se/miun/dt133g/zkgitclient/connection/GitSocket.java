package se.miun.dt133g.zkgitclient.connection;

import se.miun.dt133g.zkgitclient.commands.CommandManager;
import se.miun.dt133g.zkgitclient.user.CurrentUserRepo;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.HashMap;

public final class GitSocket {

    public static GitSocket INSTANCE = new GitSocket();

    private int port;
    private ServerSocket serverSocket;
    private Thread serverThread;

    private GitSocket() {
        this.port = AppConfig.GIT_PORT;
        serverThread = new Thread(this::startServer);
        serverThread.start();
    }

    private GitSocket(final int port) {
        this.port = port;
        serverThread = new Thread(this::startServer);
        serverThread.start();
    }

    private void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            this.serverSocket = serverSocket;

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
                    System.out.println(AppConfig.ERROR_KEY
                                       + AppConfig.COLON_SEPARATOR
                                       + AppConfig.SPACE_SEPARATOR
                                       + AppConfig.ERROR_SOCKET_INTERRUPT
                                       );
                }
            }
        } catch (Exception e) {
            this.port = 0;
            System.out.println(AppConfig.WARNING_KEY
                               + AppConfig.COLON_SEPARATOR
                               + AppConfig.SPACE_SEPARATOR
                               + AppConfig.ERROR_NO_FREE_PORT
                               );
        }
    }

    protected synchronized void restartServer(final int newPort) {
        if (INSTANCE != null) {
            stopServer();
        }
        INSTANCE = new GitSocket(newPort);
    }

    private void handleClient(final Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                //System.out.println("Received command: " + inputLine);
                //System.out.println(inputLine.split(" ").length);
                if (inputLine.split(" ").length > 1) {
                    //System.out.println("Inside if: " + inputLine.split(" ")[1]);
                    CurrentUserRepo.getInstance().setRepoName(inputLine.split(" ")[1]);
                    if (inputLine.split(" ").length > 2) {
                        CurrentUserRepo.getInstance().setRepoSignature(inputLine.split(" ")[2]);
                    }
                }
                Map<String, String> responseMap = new HashMap<>();
                responseMap = CommandManager.INSTANCE.executeCommand(inputLine.split(" ")[0]);

                System.out.println("GitSocketResponseMap: " + responseMap.toString() + ", Command: " + inputLine.split(" ")[0]);

                if (responseMap.containsKey(AppConfig.COMMAND_SUCCESS)) {
                    System.out.println("success");
                    out.println(AppConfig.COMMAND_SUCCESS);
                } else {
                    System.out.println("error");
                    out.println(responseMap.get(AppConfig.ERROR_KEY));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (serverThread != null) {
                serverThread.interrupt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected int getGitPort() {
        return port;
    }

    protected void setGitPort(int port) {
        this.port = port;
        startServer();
    }
}
