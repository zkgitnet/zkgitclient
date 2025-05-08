package se.miun.dt133g.zkgitclient.support;

import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;

import java.util.Base64;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Utility class providing various helper methods for data transformations,
 * string formatting, and byte array manipulations.
 * This class follows the Singleton pattern to provide a single instance of utility methods.
 * @author Leif Rogell
 */
public final class Utils {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private static Utils INSTANCE;

    /**
     * Private constructor to prevent instantiation.
     */
    private Utils() { }

    /**
     * Returns the singleton instance of Utils.
     * @return the Utils instance.
     */
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

    /**
     * Converts a map to a string representation.
     * @param map the map to convert.
     * @return a string representation of the map.
     */
    public String mapToString(final Map<String, String> map) {
        return map.entrySet().stream()
            .map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining(", ", AppConfig.FORWARD_CURLY_BRACKET, AppConfig.BACKWARD_CURLY_BRACKET));
    }

    /**
     * Decodes a base64 string to its hexadecimal representation.
     * @param base64 the base64 encoded string.
     * @return the hexadecimal string.
     */
    public String base64ToHex(final String base64) {
        return bytesToHex(Base64.getDecoder().decode(base64));
    }

    /**
     * Encodes a hexadecimal string to its base64 representation.
     * @param hex the hexadecimal string.
     * @return the base64 encoded string.
     */
    public String hexToBase64(final String hex) {
        return Base64.getEncoder().encodeToString(hexToBytes(hex));
    }

    /**
     * Converts a byte array to its hexadecimal string representation.
     * @param bytes the byte array to convert.
     * @return the hexadecimal string representation of the byte array.
     */
    public String bytesToHex(final byte[] bytes) {
        return IntStream.range(0, bytes.length)
            .mapToObj(i -> String.format("%02x", bytes[i]))
            .collect(Collectors.joining());
    }

    /**
     * Formats a string by inserting a space after every 5 characters.
     * @param input the input string.
     * @return the formatted string.
     */
    public String formatWithSpace(final String input) {
        return input.replaceAll("(.{5})", "$1" + AppConfig.SPACE_SEPARATOR);
    }

    /**
     * Converts a hexadecimal string to a byte array.
     * @param hex the hexadecimal string.
     * @return the corresponding byte array.
     */
    private byte[] hexToBytes(final String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                                  + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Converts a string representation of an integer array (e.g., "[1;2;3]") into a byte array.
     * @param ivArray the string representation of the integer array.
     * @return the resulting byte array.
     * @throws NumberFormatException if the string cannot be parsed as integers.
     */
    public byte[] ivArrayToByteArray(final String ivArray) throws NumberFormatException {
        String input = ivArray.replaceAll("[\\[\\] ]", "");
        String[] byteStrings = input.split("[;,]");
        byte[] byteArray = new byte[byteStrings.length];

        for (int i = 0; i < byteStrings.length; i++) {
            byteArray[i] = Byte.parseByte(byteStrings[i]);
        }

        return byteArray;
    }

    /**
     * Converts an integer array to a byte array.
     * @param intArray the integer array to convert.
     * @return the resulting byte array.
     */
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

    /**
     * Converts a byte array to an integer array.
     * @param array the byte array to convert.
     * @return the resulting integer array.
     */
    public int[] byteArrayToIntArray(final byte[] array) {
        int[] intArray = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            intArray[i] = array[i];
        }
        return intArray;
    }

    /**
     * Formats an integer array as a JSON-style string with a label.
     * @param label the label to prefix the array.
     * @param array the integer array to format.
     * @return the formatted string.
     */
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
