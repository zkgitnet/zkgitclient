package se.miun.dt133g.zkgitclient.commands.git;

import se.miun.dt133g.zkgitclient.commands.BaseCommand;
import se.miun.dt133g.zkgitclient.crypto.EncryptionHandler;
import se.miun.dt133g.zkgitclient.crypto.EncryptionFactory;
import se.miun.dt133g.zkgitclient.crypto.StreamEncryptionHandler;
import se.miun.dt133g.zkgitclient.crypto.StreamEncryptionFactory;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.FileUtils;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public abstract class BaseCommandGit extends BaseCommand {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    protected StreamEncryptionHandler aesStreamHandler =
        StreamEncryptionFactory.getStreamEncryptionHandler(AppConfig.CRYPTO_AES_STREAM);
    protected EncryptionHandler aesHandler = EncryptionFactory.getEncryptionHandler(AppConfig.CRYPTO_AES);
    protected EncryptionHandler sha256Handler = EncryptionFactory.getEncryptionHandler(AppConfig.CRYPTO_SHA_256);
    protected FileUtils fileUtils = FileUtils.getInstance();

    protected Optional<String> performEncryption(final String input) {
        try {
            aesHandler.setIv(credentials.getIv());
            aesHandler.setAesKey(credentials.getAesKey());
            aesHandler.setInput(input);
            aesHandler.encrypt();
            String encFileName = utils.base64ToHex(aesHandler.getOutput());
            currentRepo.setEncFileName(encFileName);
            return Optional.of(encFileName);
        } catch (Exception e) {
            LOGGER.severe("Encryption failed: " + e.getMessage());
            return Optional.empty();
        }
    }

    protected String performFileEncryption(final String sourceDirPath,
                                           final Map<String, String> postData,
                                           final String fileName) {

        LOGGER.fine("Initializing compression and encryption of the repo");
        
        try (PipedOutputStream zipOutputStream = new PipedOutputStream();
             PipedInputStream encryptInputStream = new PipedInputStream(zipOutputStream);
             PipedOutputStream encryptOutputStream = new PipedOutputStream();
             PipedInputStream transferInputStream = new PipedInputStream(encryptOutputStream)) {

            Thread zipThread = new Thread(() -> {
                    LOGGER.finest("Starting compression thread");
                    try {
                        fileUtils.zipDirectoryStream(sourceDirPath, zipOutputStream);
                        zipOutputStream.close();
                    } catch (IOException e) {
                        LOGGER.severe("Zipping failed: " + e.getMessage());
                    }
            });

            Thread encryptThread = new Thread(() -> {
                    LOGGER.finest("Starting encryption thread");
                    try {
                        aesStreamHandler.setAesKey(credentials.getAesKey());
                        aesStreamHandler.setIv(credentials.getIv());
                        aesStreamHandler.encryptStream(encryptInputStream, encryptOutputStream);
                        encryptOutputStream.close();
                    } catch (Exception e) {
                        LOGGER.severe("Encryption failed: " + e.getMessage());
                    }
            });

            final String[] responseHolder = new String[1];
            Thread transferThread = new Thread(() -> {
                    LOGGER.finest("Starting file transfer thread");
                    try {
                        responseHolder[0] = prepareAndSendFilePostRequest(postData, transferInputStream, fileName);
                    } catch (Exception e) {
                        LOGGER.severe("File transfer failed: " + e.getMessage());
                    }
            });

            zipThread.start();
            encryptThread.start();
            transferThread.start();

            zipThread.join();
            encryptThread.join();
            transferThread.join();

            LOGGER.fine("Successfully compressed and encrypted the repo");
            return responseHolder[0];
        } catch (Exception e) {
            LOGGER.severe("Encryption failed: " + e.getMessage());
            return AppConfig.ERROR_KEY;
        }
    }

    protected String performFileDecryption(final InputStream encryptedFileStream,
                                           final String outputDir) {
        LOGGER.fine("Initializing decryption and decompression");

        try (PipedOutputStream decryptOutputStream = new PipedOutputStream();
             PipedInputStream unzipInputStream = new PipedInputStream(decryptOutputStream)) {

            Thread streamWriterThread = new Thread(() -> {
                    try {
                        byte[] buffer = new byte[4096]; // Buffer to store chunks of data
                        int bytesRead;
                        while ((bytesRead = encryptedFileStream.read(buffer)) != -1) {
                            decryptOutputStream.write(buffer, 0, bytesRead); // Write to the piped output stream
                        }
                        decryptOutputStream.close(); // Close the output stream after writing all data
                    } catch (IOException e) {
                        LOGGER.severe("Failed to write to piped output stream: " + e.getMessage());
                    }
            });

            Thread decryptThread = new Thread(() -> {
                    LOGGER.finest("Starting decryption thread");
                    try {
                        aesStreamHandler.setAesKey(credentials.getAesKey());
                        aesStreamHandler.setIv(credentials.getIv());
                        aesStreamHandler.decryptStream(encryptedFileStream, decryptOutputStream);
                        LOGGER.finest("Decryption complete, stream closed");
                    } catch (Exception e) {
                        LOGGER.severe("Decryption failed: " + e.getMessage());
                    }
            });

            Thread unzipThread = new Thread(() -> {
                    LOGGER.finest("Starting decompression thread");
                    try {
                        fileUtils.unzipDirectoryStream(unzipInputStream, outputDir);
                        LOGGER.finest("Unzipping complete, stream closed");
                    } catch (Exception e) {
                        LOGGER.severe("Decompression failed: " + e.getMessage());
                    }
            });

            streamWriterThread.start();
            decryptThread.start();
            unzipThread.start();

            streamWriterThread.join();
            decryptThread.join();
            unzipThread.join();

            LOGGER.fine("Successfully decrypted and decompressed the file");
            return "Success";
        } catch (Exception e) {
            LOGGER.severe("Decryption process failed: " + e.getMessage());
            return AppConfig.ERROR_KEY;
        }
    }

}
