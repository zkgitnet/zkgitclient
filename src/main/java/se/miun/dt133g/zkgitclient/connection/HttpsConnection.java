package se.miun.dt133g.zkgitclient.connection;

import se.miun.dt133g.zkgitclient.crypto.EncryptionHandler;
import se.miun.dt133g.zkgitclient.crypto.EncryptionFactory;
import se.miun.dt133g.zkgitclient.user.UserCredentials;
import se.miun.dt133g.zkgitclient.user.CurrentUserRepo;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Provides methods for handling HTTPS connections, including sending GET/POST requests, uploading files,
 * and handling encryption and signature for the data.
 * @author Leif Rogell
 */
public final class HttpsConnection {

    private EncryptionHandler rsaSignHandler =
EncryptionFactory.getEncryptionHandler(AppConfig.CRYPTO_RSA_SIGNATURE);
    private EncryptionHandler sha256Handler =
        EncryptionFactory.getEncryptionHandler(AppConfig.CRYPTO_SHA_256);
    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private UserCredentials credentials = UserCredentials.getInstance();
    private CurrentUserRepo currentRepo = CurrentUserRepo.getInstance();
    private String uriPath = AppConfig.URI_PATH;

    /**
     * Sends a POST request with the provided parameters and returns the server response as an InputStream.
     * @param domain the target server domain.
     * @param port the server port.
     * @param postDataParams the parameters to include in the POST request body.
     * @return the server response InputStream.
     */
    protected InputStream sendGetPostRequest(final String domain,
                                             final String port,
                                             final Map<String, String> postDataParams) {
        try {
            HttpsURLConnection connection =
                (HttpsURLConnection) new URL("https://"
                                             + domain
                                             + ":"
                                             + port
                                             + uriPath).openConnection();
            connection.setRequestMethod(AppConfig.REQUEST_TYPE_POST);
            connection.setConnectTimeout(AppConfig.TIMEOUT_LIMIT);
            connection.setReadTimeout(AppConfig.TIMEOUT_LIMIT);
            connection.setDoOutput(true);

            try (DataOutputStream wr =
                 new DataOutputStream(connection.getOutputStream())) {
                wr.writeBytes(buildPostData(postDataParams));
                wr.flush();
            }

            sha256Handler.setInput(currentRepo.getRepoName());
            sha256Handler.encrypt();

            if (connection.getResponseCode()
                == HttpsURLConnection.HTTP_OK) {
                /*File file =
                    Paths.get(System.getProperty(AppConfig.JAVA_TMP),
                              sha256Handler.getOutput()).toFile();
                try (InputStream in =
                     connection.getInputStream();
                     FileOutputStream fos = new FileOutputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                }
                connection.disconnect();*/
                return connection.getInputStream();
            } else {
                LOGGER.warning("ErrorResponseCode: " + connection.getResponseCode());
                return null;
            }
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
            return null;
        }
    }

    /**
     * Sends a POST request with the provided parameters and returns the server response as a String.
     * @param domain the target server domain.
     * @param port the server port.
     * @param postDataParams the parameters to include in the POST request body.
     * @return the server response as a String.
     */
    protected String sendPostRequest(final String domain,
                                     final String port,
                                     final Map<String, String> postDataParams) {
        try {
            HttpsURLConnection connection =
                (HttpsURLConnection) new URL("https://"
                                             + domain
                                             + ":"
                                             + port
                                             + uriPath).openConnection();
            LOGGER.config("https://" + domain + ":" + port + uriPath);
            connection.setRequestMethod(AppConfig.REQUEST_TYPE_POST);
            connection.setConnectTimeout(AppConfig.TIMEOUT_LIMIT);
            connection.setReadTimeout(AppConfig.TIMEOUT_LIMIT);
            connection.setDoOutput(true);

            try (DataOutputStream wr =
                 new DataOutputStream(connection.getOutputStream())) {
                String post = buildPostData(postDataParams);
                LOGGER.finer(post);
                wr.writeBytes(post);
                wr.flush();
            }

            if (connection.getResponseCode()
                == HttpsURLConnection.HTTP_OK) {
                try (BufferedReader in =
                     new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    return in.lines().collect(Collectors.joining());
                }
            } else {
                LOGGER.severe("sendPostRequest Error");
                return "{" + AppConfig.ERROR_KEY
                    + "="
                    + connection.getResponseCode() + "}";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "{" + AppConfig.ERROR_KEY
                    + "="
                    + AppConfig.ERROR_CONNECTION + "}";
        }
    }

    /**
     * Sends a POST request with file data and additional parameters, returning the server response as a String.
     * @param domain the target server domain.
     * @param port the server port.
     * @param fileInputStream the InputStream of the file to upload.
     * @param fileName the name of the file being uploaded.
     * @param postParamData additional parameters for the POST request.
     * @return the server response as a String.
     */
    protected String sendFilePostRequest(final String domain,
                                         final String port,
                                         final InputStream fileInputStream,
                                         final String fileName,
                                         final Map<String, String> postParamData) {
        String boundary = "===" + System.currentTimeMillis() + "===";
        String LINE_FEED = "\r\n";

        try {
            trustAllCertificates();
            HttpsURLConnection connection =
                (HttpsURLConnection) new URL("https://" + domain + ":" + port + uriPath).openConnection();
            connection.setRequestMethod(AppConfig.REQUEST_TYPE_POST);
            connection.setConnectTimeout(AppConfig.TIMEOUT_LIMIT);
            connection.setReadTimeout(AppConfig.READ_TIMEOUT_LIMIT);
            connection.setDoOutput(true);
            connection.setChunkedStreamingMode(AppConfig.SIXFIVE_KB);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            try (BufferedOutputStream request = new BufferedOutputStream(connection.getOutputStream());
                 BufferedInputStream bufferedFileStream = new BufferedInputStream(fileInputStream,
                                                                                  AppConfig.SIXFIVE_KB)) {

                String postParamsString = buildPostParamsString(postParamData, boundary, LINE_FEED);
                LOGGER.finest(postParamsString);
                request.write(postParamsString.getBytes(StandardCharsets.UTF_8));

                request.write(("--" + boundary + LINE_FEED
                               + "Content-Disposition: form-data; name=\"file\"; filename=\""
                               + fileName + "\"" + LINE_FEED
                               + "Content-Type: " + URLConnection.guessContentTypeFromName(fileName) + LINE_FEED
                               + "Content-Transfer-Encoding: binary" + LINE_FEED + LINE_FEED)
                              .getBytes(StandardCharsets.UTF_8));

                byte[] buffer = new byte[AppConfig.SIXFIVE_KB];
                int bytesRead;
                int totBytes = 0;
                while ((bytesRead = bufferedFileStream.read(buffer)) != -1) {
                    request.write(buffer, 0, bytesRead);
                    totBytes += bytesRead;
                }
                LOGGER.fine("Total Bytes: " + totBytes);

                request.write((LINE_FEED + "--" + boundary + "--" + LINE_FEED).getBytes(StandardCharsets.UTF_8));
                request.flush();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    return in.lines().collect(Collectors.joining("\n"));
                }
            } else {
                return String.format("%s %s", AppConfig.ERROR_KEY, AppConfig.ERROR_CONNECTION);
            }
        } catch (Exception e) {
            LOGGER.severe("File upload failed: " + e.getMessage());
            return String.format("%s %s", AppConfig.ERROR_KEY, AppConfig.ERROR_CONNECTION);
        }
    }

    /**
     * Builds the body for the POST request containing form data parameters and encrypted signature.
     * @param postParamDataInput the parameters to include in the POST body.
     * @param boundary the boundary for multipart form-data.
     * @param lineFeed the line feed character for formatting.
     * @return the formatted POST data as a String.
     */
    private String buildPostParamsString(final Map<String, String> postParamDataInput,
                                         final String boundary,
                                         final String lineFeed) {
        StringBuilder postDataBuilder = new StringBuilder();
        StringBuilder messageBuilder = new StringBuilder();

        Map<String, String> postParamData = new HashMap<>(postParamDataInput);

        postParamData.put(AppConfig.CREDENTIAL_CURRENT_TIMESTAMP,
                          String.valueOf(System.currentTimeMillis()));

        for (Map.Entry<String, String> entry : postParamData.entrySet()) {
            postDataBuilder.append("--")
                .append(boundary)
                .append(lineFeed)
                .append("Content-Disposition: form-data; name=\"")
                .append(entry.getKey())
                .append("\"")
                .append(lineFeed)
                .append("Content-Type: text/plain; charset=UTF-8")
                .append(lineFeed)
                .append(lineFeed)
                .append(entry.getValue())
                .append(lineFeed);

            if (messageBuilder.length() > 0) {
                messageBuilder.append("&");
            }
            messageBuilder.append(entry.getKey())
                .append("=")
                .append(entry.getValue());
        }

        sha256Handler.setInput(messageBuilder.toString());
        sha256Handler.encrypt();
        String hash = sha256Handler.getOutput();
        if (hash != null) {
            postDataBuilder.append("--")
                .append(boundary)
                .append(lineFeed)
                .append("Content-Disposition: form-data; name=\"")
                .append("hash")
                .append("\"")
                .append(lineFeed)
                .append("Content-Type: text/plain; charset=UTF-8")
                .append(lineFeed)
                .append(lineFeed)
                .append(hash)
                .append(lineFeed);

            messageBuilder.append("&"
                                  + "hash"
                                  + "="
                                  + hash);


            String privRsa = null;
            try {
                privRsa = credentials.getPrivRsa();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            if (privRsa != null && !privRsa.equals("")) {
                rsaSignHandler.setRsaKey(privRsa);
                rsaSignHandler.setInput(hash);
                rsaSignHandler.encrypt();
                String signedHash = rsaSignHandler.getOutput();

                if (!signedHash.contains("argument")) {
                    postDataBuilder.append("--")
                        .append(boundary)
                        .append(lineFeed)
                        .append("Content-Disposition: form-data; name=\"")
                        .append("signature")
                        .append("\"")
                        .append(lineFeed)
                        .append("Content-Type: text/plain; charset=UTF-8")
                        .append(lineFeed)
                        .append(lineFeed)
                        .append(signedHash)
                        .append(lineFeed);

                    messageBuilder.append("&"
                                           + "signature"
                                           + "="
                                           + signedHash);
                }
            }
        }

        return postDataBuilder.toString();
    }

    /**
     * Builds the POST data string, including parameters and encrypted signature.
     * @param postDataParams the parameters to include in the POST data.
     * @return the formatted POST data string.
     */
    private String buildPostData(final Map<String, String> postDataParams) {

        String postDataString = postDataParams.entrySet().stream()
            .map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining("&"));


        StringBuilder postDataStringBuilder = new StringBuilder(postDataString);

        postDataStringBuilder.append("&"
                                     + AppConfig.CREDENTIAL_CURRENT_TIMESTAMP
                                     + "="
                                     + String.valueOf(System.currentTimeMillis()));

        sha256Handler.setInput(postDataStringBuilder.toString());
        sha256Handler.encrypt();
        String hash = sha256Handler.getOutput();
        if (hash != null) {
            postDataStringBuilder.append("&"
                                         + "hash"
                                         + "="
                                         + hash);
        }

        String privRsa = null;
        try {
            privRsa = credentials.getPrivRsa();
        } catch (Exception e) { }

        if (privRsa != null && !privRsa.equals("")) {
            rsaSignHandler.setRsaKey(privRsa);
            rsaSignHandler.setInput(hash);
            rsaSignHandler.encrypt();
            String signedHash = rsaSignHandler.getOutput();

            if (!signedHash.contains("argument")) {
                postDataStringBuilder.append("&"
                                             + "signature"
                                             + "="
                                             + signedHash);
            }
        }

        return postDataStringBuilder.toString();
    }

    /**
     * Configures the connection to trust all certificates, bypassing SSL verification.
     */
    private void trustAllCertificates() {
        try {
            TrustManager[] trustAllCerts = {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(final X509Certificate[] certs,
                                                   final String authType) { }
                    public void checkServerTrusted(final X509Certificate[] certs,
                                                   final String authType) { }
                }
            };
            SSLContext sc = SSLContext.getInstance(AppConfig.CRYPTO_SSL);
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
