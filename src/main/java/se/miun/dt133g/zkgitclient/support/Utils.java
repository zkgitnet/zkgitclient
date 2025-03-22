package se.miun.dt133g.zkgitclient.support;

import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;

import java.util.Base64;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class Utils {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private static Utils INSTANCE;

    private Utils() { }

    public static Utils getInstance() {
        if (INSTANCE == null) {
            synchronized (Utils.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Utils();
                }
            }
        }
        return INSTANCE;
     }


    public String mapToString(final Map<String, String> map) {
        return map.entrySet().stream()
            .map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining(", ", AppConfig.FORWARD_CURLY_BRACKET, AppConfig.BACKWARD_CURLY_BRACKET));
    }

    public String base64ToHex(final String base64) {
        return bytesToHex(Base64.getDecoder().decode(base64));
    }

    public String hexToBase64(final String hex) {
        return Base64.getEncoder().encodeToString(hexToBytes(hex));
    }

    public String bytesToHex(final byte[] bytes) {
        return IntStream.range(0, bytes.length)
            .mapToObj(i -> String.format("%02x", bytes[i]))
            .collect(Collectors.joining());
    }

    public String formatWithSpace(String input) {
        return input.replaceAll("(.{5})", "$1" + AppConfig.SPACE_SEPARATOR);
    }

    private byte[] hexToBytes(final String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                                  + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    public byte[] ivArrayToByteArray(final String ivArray) throws NumberFormatException {
        String input = ivArray.replaceAll("[\\[\\] ]", "");
        String[] byteStrings = input.split(";");
        byte[] byteArray = new byte[byteStrings.length];
        
        for (int i = 0; i < byteStrings.length; i++) {
            byteArray[i] = Byte.parseByte(byteStrings[i]);
        }

        return byteArray;
    }

    public byte[] intArrayToByteArray(final int[] intArray) {
        byte[] byteArray = new byte[intArray.length * Integer.BYTES];

        for (int i = 0; i < intArray.length; i++) {
            int value = intArray[i];
            byteArray[i * Integer.BYTES]     = (byte) (value >> 24);
            byteArray[i * Integer.BYTES + 1] = (byte) (value >> 16);
            byteArray[i * Integer.BYTES + 2] = (byte) (value >> 8);
            byteArray[i * Integer.BYTES + 3] = (byte) value;
        }
        return byteArray;
    }

    public int[] byteArrayToIntArray(final byte[] array) {
        int[] intArray = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            intArray[i] = array[i];
        }
        return intArray;
    }

    public String formatIntArray(final String label, final int[] array) {
         StringBuilder sb = new StringBuilder();
         sb.append("\"").append(label).append("\":[");

         for (int i = 0; i < array.length; i++) {
             sb.append(array[i]);
             if (i < array.length - 1) {
                 sb.append(AppConfig.COMMA_SEPARATOR);
             }
         }

         sb.append(AppConfig.BACKWARD_SQUARE_BRACKET);
         return sb.toString();
    }
}
