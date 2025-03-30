package se.miun.dt133g.zkgitclient.support;

import se.miun.dt133g.zkgitclient.user.UserCredentials;
import se.miun.dt133g.zkgitclient.user.CurrentUserRepo;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;

import java.io.BufferedOutputStream;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.IntStream;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipInputStream;

public final class FileUtils {

    private static FileUtils INSTANCE;

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private UserCredentials credentials = UserCredentials.getInstance();
    private CurrentUserRepo currentRepo = CurrentUserRepo.getInstance();

    private FileUtils() { }

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

    public void unzipDirectoryStream(InputStream inputStream) {
        String outputDir = System.getProperty(AppConfig.JAVA_TMP) + "/zkgit-tmp-" + currentRepo.getRepoName();
        try (ZipInputStream zipIn = new ZipInputStream(inputStream)) {
            LOGGER.fine("Starting decompression of repo");
            ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                Path filePath = Paths.get(outputDir, entry.getName());
                //LOGGER.finest("Decompressing: " + outputDir + "/" + entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(filePath);
                } else {
                    Files.createDirectories(filePath.getParent());
                    try (OutputStream fileOut = Files.newOutputStream(filePath)) {
                        byte[] buffer = new byte[8 * AppConfig.ONE_KB];
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

    public void zipDirectoryStream(final String sourceDirPath, OutputStream outputStream) {
        try (ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
            LOGGER.fine("Starting repo compression");
            Path sourceDir = Paths.get(sourceDirPath);
            Files.walk(sourceDir).forEach(path -> {
                    try {
                        if (!Files.isDirectory(path)) {
                            String entryName = sourceDir.relativize(path).toString();
                            zipOut.putNextEntry(new ZipEntry(entryName));
                            Files.copy(path, zipOut);
                            zipOut.closeEntry();
                        }
                    } catch (Exception e) {
                        LOGGER.severe("Could not compress repo file");
                    }
                });
            zipOut.finish();
            LOGGER.fine("Finished repo compression");
        } catch (IOException e) {
            LOGGER.severe("Could not compress repo: " + e.getMessage());
        }
    }

    public String[] splitFile(final String fileName) {
        Path tempDir = Paths.get(System.getProperty(AppConfig.JAVA_TMP), fileName + AppConfig.PARTS_SUFFIX);
        Path sourceFile = Paths.get(System.getProperty(AppConfig.JAVA_TMP), fileName);
        List<String> chunkFileNames = new ArrayList<>();

        if (Files.notExists(tempDir)) {
            try {
                Files.createDirectories(tempDir);
            } catch (IOException e) {
                System.err.println(AppConfig.ERROR_CREATE_DIR + e.getMessage());
                return chunkFileNames.toArray(new String[0]);
            }
        }

        try (RandomAccessFile raf = new RandomAccessFile(sourceFile.toFile(), AppConfig.FILE_READ_MODE)) {
            long fileSize = raf.length();
            int chunkCount = (int) Math.ceil((double) fileSize / AppConfig.CHUNK_SIZE);
            byte[] buffer = new byte[AppConfig.CHUNK_SIZE];

            IntStream.range(0, chunkCount).forEach(i -> {
                    Path chunkFile = tempDir.resolve(fileName + AppConfig.PART_SUFFIX + (i + 1));
                    try (FileOutputStream fos = new FileOutputStream(chunkFile.toFile())) {
                        int bytesRead = raf.read(buffer);
                        fos.write(buffer, 0, bytesRead);
                    } catch (IOException e) {
                        System.err.println(AppConfig.ERROR_WRITE_FILE + (i + 1) + ": " + e.getMessage());
                    }
                    chunkFileNames.add(chunkFile.toString());
                });

        } catch (IOException e) {
            System.err.println(AppConfig.ERROR_WRITE_FILE + e.getMessage());
        }

        return chunkFileNames.toArray(new String[0]);
    }

    public void saveStringToFile(final String data, final String fileName) {
        Path filePath = Paths.get(System.getProperty(AppConfig.JAVA_TMP), fileName);

        try {
            Files.write(filePath, data.getBytes());
            LOGGER.finest(AppConfig.INFO_FILE_SAVED + filePath);
        } catch (IOException e) {
            LOGGER.warning(AppConfig.ERROR_FAILED_SAVE_FILE + e.getMessage());
        }
    }

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
                                                 (currentRepo != null ? currentRepo.getEncFileName() + AppConfig.PARTS_SUFFIX : "unknown")));
            LOGGER.finest("Deleting enc tmp directory");
        } catch (Exception e) {
            LOGGER.warning("Could not delete enc tmp directory");
        }

        try {
            deleteDirectoryAndContents(Paths.get(System.getProperty(AppConfig.JAVA_TMP),
                                                 (currentRepo != null ? AppConfig.TMP_PREFIX + currentRepo.getRepoName() : "unknown")));
            LOGGER.finest("Deleting tmp directory");
        } catch (Exception e) {
            LOGGER.warning("Could not delete tmp directory");
        }
    }

    private void deleteFile(String fileName) throws IOException {
        if (fileName != null) {
            Path path = Paths.get(System.getProperty(AppConfig.JAVA_TMP), fileName);
            if (Files.exists(path)) {
                Files.delete(path);
            }
        } else {
            throw new NullPointerException("");
        }
    }

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
