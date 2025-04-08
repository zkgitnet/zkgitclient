package se.miun.dt133g.zkgitclient.support;

import se.miun.dt133g.zkgitclient.user.UserCredentials;
import se.miun.dt133g.zkgitclient.user.CurrentUserRepo;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipInputStream;

/**
 * Utility class for handling file operations such as unzipping, zipping, saving files,
 * and cleaning temporary files.
 * This class follows the Singleton pattern to provide a single instance for file operations.
 * @author Leif Rogell
 */
public final class FileUtils {

    private static FileUtils INSTANCE;

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private UserCredentials credentials = UserCredentials.getInstance();
    private CurrentUserRepo currentRepo = CurrentUserRepo.getInstance();

    /**
     * Private constructor to prevent instantiation.
     */
    private FileUtils() { }

    /**
     * Returns the singleton instance of FileUtils.
     * @return the FileUtils instance.
     */
    public static FileUtils getInstance() {
        if (INSTANCE == null) {
            synchronized (FileUtils.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FileUtils();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Unzips a directory from an input stream and saves the extracted files to a temporary location.
     * @param inputStream the input stream containing the zip data.
     */
    public void unzipDirectoryStream(final InputStream inputStream) {
        String outputDir = System.getProperty(AppConfig.JAVA_TMP) + "/zkgit-tmp-" + currentRepo.getRepoName();
        try (ZipInputStream zipIn = new ZipInputStream(inputStream)) {
            LOGGER.fine("Starting decompression of repo");
            ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                Path filePath = Paths.get(outputDir, entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(filePath);
                } else {
                    Files.createDirectories(filePath.getParent());
                    try (OutputStream fileOut = Files.newOutputStream(filePath)) {
                        byte[] buffer = new byte[64 * AppConfig.ONE_KB];
                        int bytesRead;
                        while ((bytesRead = zipIn.read(buffer)) != -1) {
                            fileOut.write(buffer, 0, bytesRead);
                        }
                    }
                }
                zipIn.closeEntry();
            }
            LOGGER.fine("Finished decompressing repo");
        } catch (IOException e) {
            LOGGER.severe("Could not decompress repo");
        }
    }

    /**
     * Compresses a directory into a GZIP stream and writes it to the given output stream.
     * @param sourceDirPath the path of the directory to compress.
     * @param outputStream the output stream to write the compressed data to.
     */
    public void zipDirectoryStream(final String sourceDirPath,
                                   final OutputStream outputStream) {
        Path sourceDir = Paths.get(sourceDirPath);

        try (GZIPOutputStream gzipOut = new GZIPOutputStream(outputStream)) {
            LOGGER.fine("Starting repo compression: " + sourceDirPath);

            Files.walk(sourceDir)
                .filter(path -> !Files.isDirectory(path))
                .forEach(path -> {
                        String entryName = sourceDir.relativize(path).toString();
                        try (InputStream fileInputStream = Files.newInputStream(path)) {
                            gzipOut.write(("File: " + entryName + "\n").getBytes());
                            byte[] buffer = new byte[AppConfig.SIXFIVE_KB];
                            int bytesRead;
                            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                                gzipOut.write(buffer, 0, bytesRead);
                            }
                        } catch (IOException e) {
                            LOGGER.severe("Could not compress file: " + path + " - " + e.getMessage());
                        }
                    });
            LOGGER.fine("Finished repo compression");
        } catch (IOException e) {
            LOGGER.severe("Could not compress repo: " + e.getMessage());
        }
    }

    /**
     * Saves a string of data to a file in the system's temporary directory.
     * @param data the data to be saved to the file.
     * @param fileName the name of the file to save the data in.
     */
    public void saveStringToFile(final String data, final String fileName) {
        Path filePath = Paths.get(System.getProperty(AppConfig.JAVA_TMP), fileName);

        try {
            Files.write(filePath, data.getBytes());
            LOGGER.finest(AppConfig.INFO_FILE_SAVED + filePath);
        } catch (IOException e) {
            LOGGER.warning(AppConfig.ERROR_FAILED_SAVE_FILE + e.getMessage());
        }
    }

    /**
     * Cleans up temporary files related to the current repository by deleting specific files and directories.
     */
    public void cleanTmpFiles() {
        try {
            if (currentRepo != null) {
                deleteFile(currentRepo.getEncFileName());
                LOGGER.finest("Deleting current encrypted tmp repo");
            }
        } catch (Exception e) {
            LOGGER.warning("Could not delete current encrypted tmp repo");
        }

        try {
            if (currentRepo != null) {
                deleteFile(currentRepo.getRepoName() + AppConfig.ZIP_SUFFIX);
                LOGGER.finest("Deleting current tmp repo");
            }
        } catch (Exception e) {
            LOGGER.warning("Could not delete current tmp repo");
        }

        try {
            deleteDirectoryAndContents(Paths.get(System.getProperty(AppConfig.JAVA_TMP),
                                                 (currentRepo != null ? currentRepo.getEncFileName()
                                                  + AppConfig.PARTS_SUFFIX : "unknown")));
            LOGGER.finest("Deleting enc tmp directory");
        } catch (Exception e) {
            LOGGER.warning("Could not delete enc tmp directory");
        }

        try {
            deleteDirectoryAndContents(Paths.get(System.getProperty(AppConfig.JAVA_TMP),
                                                 (currentRepo != null ? AppConfig.TMP_PREFIX
                                                  + currentRepo.getRepoName() : "unknown")));
            LOGGER.finest("Deleting tmp directory");
        } catch (Exception e) {
            LOGGER.warning("Could not delete tmp directory");
        }
    }

    /**
     * Deletes a specific file by its name in the system's temporary directory.
     * @param fileName the name of the file to delete.
     * @throws IOException if an I/O error occurs.
     */
    private void deleteFile(final String fileName) throws IOException {
        if (fileName != null) {
            Path path = Paths.get(System.getProperty(AppConfig.JAVA_TMP), fileName);
            if (Files.exists(path)) {
                Files.delete(path);
            }
        } else {
            throw new NullPointerException("");
        }
    }

    /**
     * Deletes a directory and all of its contents.
     * @param directoryPath the path to the directory to delete.
     * @throws IOException if an I/O error occurs.
     */
    private void deleteDirectoryAndContents(final Path directoryPath) throws IOException {
        if (directoryPath != null && Files.exists(directoryPath)) {
            Files.walk(directoryPath)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                        }
                    });
        } else if (directoryPath == null) {
            throw new NullPointerException("");
        }
    }
}
