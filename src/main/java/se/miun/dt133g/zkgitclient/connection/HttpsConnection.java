package se.miun.dt133g.zkgitclient.connection;

import se.miun.dt133g.zkgitclient.crypto.EncryptionHandler;
import se.miun.dt133g.zkgitclient.crypto.EncryptionFactory;
import se.miun.dt133g.zkgitclient.user.UserCredentials;
import se.miun.dt133g.zkgitclient.user.CurrentUserRepo;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.HostnameVerifier;

public final class HttpsConnection {

    private EncryptionHandler rsaSignHandler =
        EncryptionFactory.getEncryptionHandler(AppConfig.CRYPTO_RSA_SIGNATURE);
    private EncryptionHandler sha256Handler =
        EncryptionFactory.getEncryptionHandler(AppConfig.CRYPTO_SHA_256);
    private UserCredentials credentials =
        UserCredentials.getInstance();
    private CurrentUserRepo currentRepo =
        CurrentUserRepo.getInstance();
    private String uriPath = AppConfig.URI_PATH;
    
    protected String sendGetPostRequest(final String domain,
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
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setDoOutput(true);

            try (DataOutputStream wr =
                 new DataOutputStream(connection.getOutputStream())) {
                wr.writeBytes(buildPostData(postDataParams));
                wr.flush();
            }

            if (connection.getResponseCode()
                == HttpsURLConnection.HTTP_OK) {
                File file =
                    Paths.get(System.getProperty(AppConfig.JAVA_TMP),
                              currentRepo.getEncFileName()).toFile();
                try (InputStream in =
                     connection.getInputStream();
                     FileOutputStream fos = new FileOutputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                }
                connection.disconnect();
                return AppConfig.COMMAND_SUCCESS;
            } else {
                System.out.println("ErrorResponseCode");
                return AppConfig.ERROR_KEY;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return AppConfig.ERROR_KEY
                + " "
                + AppConfig.ERROR_CONNECTION;
        }
    }

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
            System.out.println("https://" + domain + ":" + port + uriPath);
            connection.setRequestMethod(AppConfig.REQUEST_TYPE_POST);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setDoOutput(true);

            try (DataOutputStream wr =
                 new DataOutputStream(connection.getOutputStream())) {
                String post = buildPostData(postDataParams);
                System.out.println(post);
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
                System.out.println("sendPostRequest Error");
                return "{" + AppConfig.ERROR_KEY
                    + "="
                    + AppConfig.ERROR_CONNECTION + "}";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "{" + AppConfig.ERROR_KEY
                    + "="
                    + AppConfig.ERROR_CONNECTION + "}";
        }
    }

    protected String sendFilePostRequest(final String domain,
                                         final String port,
                                         final File file,
                                         final Map<String, String> postParamData) {
        String boundary = "==="
            + System.currentTimeMillis()
            + "===";
        String LINE_FEED = "\r\n";

        try {
            trustAllCertificates();
            HttpsURLConnection connection =
                (HttpsURLConnection) new URL("https://"
                                             + domain
                                             + ":"
                                             + port
                                             + uriPath).openConnection();
            connection.setRequestMethod(AppConfig.REQUEST_TYPE_POST);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(1200000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary="
                                          + boundary);

            try (DataOutputStream request =
                 new DataOutputStream(connection.getOutputStream())) {

                String postParamsString = buildPostParamsString(postParamData, boundary, LINE_FEED);
                request.writeBytes(postParamsString);

                request.writeBytes("--"
                                   + boundary
                                   + LINE_FEED
                                   + "Content-Disposition: form-data; name=\"file\"; filename=\""
                                   + file.getName()
                                   + "\""
                                   + LINE_FEED
                                   + "Content-Type: "
                                   + URLConnection.guessContentTypeFromName(file.getName())
                                   + LINE_FEED
                                   + "Content-Transfer-Encoding: binary"
                                   + LINE_FEED
                                   + LINE_FEED);
                try (FileInputStream inputStream =
                     new FileInputStream(file)) {
                    byte[] buffer = new byte[65536];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        request.write(buffer, 0, bytesRead);
                    }
                }

                request.writeBytes(LINE_FEED
                                   + "--"
                                   + boundary
                                   + "--"
                                   + LINE_FEED);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                try (BufferedReader in =
                     new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    return in.lines().collect(Collectors.joining("\n"));
                }
            } else {
                return String.format("%s %s", AppConfig.ERROR_KEY,
                                     AppConfig.ERROR_CONNECTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return String.format("%s %s", AppConfig.ERROR_KEY,
                                 AppConfig.ERROR_CONNECTION);
        }
    }

    private String buildPostParamsString(Map<String, String> postParamDataInput, String boundary, String lineFeed) {
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

            if (messageBuilder.length()> 0) {
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
        
        //System.out.println(postDataBuilder.toString());
                    
        //System.out.println(messageBuilder.toString());

        return postDataBuilder.toString();
    }


    private String buildPostData(Map<String, String> postDataParams) {

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

        //System.out.println("\n" + postDataStringBuilder.toString() + "\n");
                                     
        return postDataStringBuilder.toString();
    }

    private void trustAllCertificates() {
        try {
            TrustManager[] trustAllCerts = { new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(final X509Certificate[] certs,
                                                   final String authType) {}
                    public void checkServerTrusted(final X509Certificate[] certs,
                                                   final String authType) {}
                }};
            SSLContext sc = SSLContext.getInstance(AppConfig.CRYPTO_SSL);
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
