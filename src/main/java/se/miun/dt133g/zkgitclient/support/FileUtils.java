package se.miun.dt133g.zkgitclient.support;

import se.miun.dt133g.zkgitclient.user.UserCredentials;
import se.miun.dt133g.zkgitclient.user.CurrentUserRepo;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.IntStream;
import java.util.logging.Logger;

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
            System.out.println(AppConfig.INFO_FILE_SAVED + filePath);
        } catch (IOException e) {
            System.err.println(AppConfig.ERROR_FAILED_SAVE_FILE + e.getMessage());
        }
    }

    public void cleanTmpFiles() {
        try {
            if (currentRepo != null) {
                deleteFile(currentRepo.getEncFileName());
            }
        } catch (NullPointerException e) {
        } catch (IOException e) {
        }

        try {
            if (currentRepo != null) {
                deleteFile(currentRepo.getRepoName() + AppConfig.ZIP_SUFFIX);
            }
        } catch (NullPointerException e) {
        } catch (IOException e) {
        }

        try {
            deleteDirectoryAndContents(Paths.get(System.getProperty(AppConfig.JAVA_TMP),
                                                 (currentRepo != null ? currentRepo.getEncFileName() + AppConfig.PARTS_SUFFIX : "unknown")));
        } catch (NullPointerException e) {
        } catch (IOException e) {
        }

        try {
            deleteDirectoryAndContents(Paths.get(System.getProperty(AppConfig.JAVA_TMP),
                                                 (currentRepo != null ? AppConfig.TMP_PREFIX + currentRepo.getRepoName() : "unknown")));
        } catch (NullPointerException e) {
        } catch (IOException e) {
        }
    }

    public void cleanTmpRepo() {
        cleanTmpFiles();
        try {
            deleteDirectoryAndContents(Paths.get(System.getProperty(AppConfig.JAVA_TMP),
                                                 (currentRepo != null ? AppConfig.TMP_PREFIX + currentRepo.getRepoName() : "unknown")));
        } catch (NullPointerException e) {
        } catch (IOException e) {
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
