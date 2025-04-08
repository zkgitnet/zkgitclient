package se.miun.dt133g.zkgitclient.commands.git;

import se.miun.dt133g.zkgitclient.commands.BaseCommand;
import se.miun.dt133g.zkgitclient.crypto.EncryptionHandler;
import se.miun.dt133g.zkgitclient.crypto.EncryptionFactory;
import se.miun.dt133g.zkgitclient.crypto.StreamEncryptionHandler;
import se.miun.dt133g.zkgitclient.crypto.StreamEncryptionFactory;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.FileUtils;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

/**
 * Base class for Git-related commands, providing encryption utilities, file processing functions,
 * and other common methods used by commands related to Git operations.
 * @author Leif Rogell
 */
public abstract class BaseCommandGit extends BaseCommand {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    protected StreamEncryptionHandler aesStreamHandler =
        StreamEncryptionFactory.getStreamEncryptionHandler(AppConfig.CRYPTO_AES_STREAM);
    protected EncryptionHandler aesHandler = EncryptionFactory.getEncryptionHandler(AppConfig.CRYPTO_AES);
    protected EncryptionHandler aesFileHandler = EncryptionFactory.getEncryptionHandler("fileAes");
    protected EncryptionHandler sha256Handler = EncryptionFactory.getEncryptionHandler(AppConfig.CRYPTO_SHA_256);
    protected EncryptionHandler ivHandler = EncryptionFactory.getEncryptionHandler(AppConfig.CRYPTO_IV);
    protected FileUtils fileUtils = FileUtils.getInstance();

    /**
     * Encrypts a given input string using AES encryption and returns the encrypted file name.
     * @param input the string to be encrypted
     * @return an Optional containing the encrypted file name, or empty if encryption fails
     */
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

    /**
     * Performs compression, encryption, and file transfer in separate threads.
     * The method compresses a directory, encrypts the compressed file, and then sends the encrypted
     * file via a post request.
     * @param sourceDirPath the path of the source directory to be compressed
     * @param postData the data to be sent in the post request
     * @param fileName the name of the file being encrypted and transferred
     * @return a response indicating success or failure of the operation
     */
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

            LOGGER.fine("Successfully compressed and encrypted the repo" + responseHolder[0]);
            return responseHolder[0];
        } catch (Exception e) {
            LOGGER.severe("Encryption failed: " + e.getMessage());
            return AppConfig.ERROR_KEY;
        }
    }

    /**
     * Decrypts a file, decompresses it, and saves the result in a specified output directory.
     * This method reads an encrypted file, decrypts it, and then decompresses the resulting data.
     * @param inputFile the encrypted file to be decrypted
     * @param outputDir the directory to save the decompressed files
     * @return a response indicating success or failure of the operation
     */
    protected String performFileDecryption(final File inputFile,
                                           final String outputDir) {
        LOGGER.fine("Initializing decryption and decompression");

        try (PipedOutputStream streamWriterOutputStream = new PipedOutputStream();
             PipedInputStream decryptInputStream = new PipedInputStream(streamWriterOutputStream);
             PipedOutputStream decryptOutputStream = new PipedOutputStream();
             PipedInputStream unzipInputStream = new PipedInputStream(decryptOutputStream)) {

            final CountDownLatch latch = new CountDownLatch(1);

            Thread streamWriterThread = new Thread(() -> {
                    try (FileInputStream encryptedFileStream = new FileInputStream(inputFile)) {
                        byte[] buffer = new byte[4 * AppConfig.ONE_KB];
                        int bytesRead;
                        while ((bytesRead = encryptedFileStream.read(buffer)) != -1) {
                            LOGGER.finest("bytesRead (streamWriter): " + bytesRead);
                            streamWriterOutputStream.write(buffer, 0, bytesRead);
                            streamWriterOutputStream.flush();
                        }
                        streamWriterOutputStream.flush();
                        LOGGER.finest("File reading and writing to the piped stream completed.");
                    } catch (IOException e) {
                        LOGGER.severe("Failed to write to piped output stream: " + e.getMessage());
                    }
            });

            Thread decryptThread = new Thread(() -> {
                    LOGGER.finest("Starting decryption thread");
                    try {
                        aesStreamHandler.setAesKey(credentials.getAesKey());
                        aesStreamHandler.setIv(credentials.getIv());
                        aesStreamHandler.decryptStream(decryptInputStream, decryptOutputStream);
                        //latch.countDown();
                        LOGGER.finest("Decryption complete, stream closed");
                    } catch (Exception e) {
                        LOGGER.severe("Decryption failed: " + e.getMessage());
                    }
            });

            Thread unzipThread = new Thread(() -> {
                    LOGGER.finest("Starting decompression thread");
                    try {
                        latch.await();
                        fileUtils.unzipDirectoryStream(unzipInputStream);
                        decryptOutputStream.close();
                        LOGGER.finest("Unzipping complete, stream closed");
                    } catch (Exception e) {
                        LOGGER.severe("Decompression failed: " + e.getMessage());
                    }
            });

            //streamWriterThread.start();
            decryptThread.start();
            unzipThread.start();

            //streamWriterThread.join();
            decryptThread.join();
            unzipThread.join();

            //latch.await();

            LOGGER.fine("Successfully decrypted and decompressed the file");
            return "Success";
        } catch (Exception e) {
            LOGGER.severe("Decryption process failed: " + e.getMessage());
            return AppConfig.ERROR_KEY;
        }
    }
}
