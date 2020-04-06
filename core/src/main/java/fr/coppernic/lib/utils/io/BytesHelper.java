package fr.coppernic.lib.utils.io;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;

/**
 * Class containing method performing some basic tasks with bytes
 * <p>It is advised to use a special lib to handle bytes such as okio from square {@see https://github.com/square/okio}
 * instread of this one.
 */
@SuppressWarnings("WeakerAccess")
public final class BytesHelper {

    static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private static final int SIZE_LONG = 8;
    private static final int SIZE_INT = 4;
    private static final int SIZE_SHORT = 2;

    private BytesHelper() {
    }

    /**
     * Fill a bytes array with zeros
     *
     * @param array
     */
    public static void clearMemoryWithZero(byte[] array) {

        for (int i = 0; i < array.length; i++) {
            array[i] = 0;
        }
    }

    public static void clearMemoryWithZero(int[] array) {

        for (int i = 0; i < array.length; i++) {
            array[i] = 0;
        }
    }

    /**
     * Return the index of a byte value in an array.
     * <p>
     * Index starts at 0.
     *
     * @param array  Array where the byte to find is
     * @param b      Value to find
     * @param offset Offset from which to search
     * @return the index value of the byte found, or -1 if not found.
     */
    public static int findByteInArray(byte[] array, byte b, int offset) {

        for (int i = offset; i < array.length; i++) {
            if (array[i] == b) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Returns the index of a sub-array in an array.
     *
     * @param array  Array where the sub-array to find may be
     * @param bytes  Sub-array to find
     * @param offset Offset from which to search
     * @return the index value of the sub-array found, or -1 if not found.
     */
    public static int findBytesInArray(byte[] array, byte[] bytes, int offset) {
        for (int i = offset; i < array.length - bytes.length + 1; i++) {
            boolean res = true;
            for (int j = 0; j < bytes.length; j++) {
                if (bytes[j] != array[i + j]) {
                    res = false;
                    break;
                }
            }

            if (res) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Return the first index of a char in a char array
     *
     * @param array  Array to search in
     * @param b      Char to find
     * @param offset Offset from where to start in array
     * @return char index, -1 if not found
     */
    public static int findCharInArray(char[] array, char b, int offset) {

        for (int i = offset; i < array.length; i++) {
            if (array[i] == b) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Convert hexadecimal ascii characters to their decimal value.
     * <p>
     * ex : 'a' = 10, '0' = 0, 'F' = 15
     * <p>
     * Accept char from '0' to '9', from 'a' to 'f' and 'A' to 'F'
     *
     * @param toConv Character to convert
     * @return value from 0 to 16 or -1 if error
     */
    public static int hexAsciiToVal(char toConv) {

        if (toConv >= 'A' && toConv <= 'F') {
            return (char) (toConv - 'A' + 10);
        }

        if (toConv >= 'a' && toConv <= 'f') {
            return (char) (toConv - 'a' + 10);
        }

        if (toConv >= '0' && toConv <= '9') {
            return (char) (toConv - '0');
        }

        return -1;
    }

    /**
     * Convert a byte array in string. Each byte value is casted in char.
     * <p>
     * For instance, 0x61 (97) value in byte gives 'a' in final string
     *
     * @param array
     * @param len
     * @return
     */
    public static String byteArrayToAsciiString(byte[] array, int len) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append((char) array[i]);
        }
        return sb.toString();
    }

    /**
     * Convert a byte array in string. Each byte value is casted in char.
     * <p>
     * For instance, 0x61 (97) value in byte gives 'a' in final string
     *
     * @param array
     * @return
     */
    public static String byteArrayToAsciiString(byte[] array) {
        if (array == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (byte anArray : array) {
            sb.append((char) anArray);
        }
        return sb.toString();
    }

    /**
     * Convert byte array to UTF-8 string.
     * <p>  new String(data,"UTF-8"); is used internally
     *
     * @param data byte array
     * @return UTF-8 string
     */
    public static String byteArrayToUtf8String(byte[] data) {
        String ret;
        if (data == null) {
            return null;
        }
        try {
            ret = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            ret = "";
        }
        return ret;
    }

    /**
     * Convert a byte array in a human readable version.
     * <p>
     * For instance, 1F value in byte gives "1F" in final string
     *
     * @param array     Byte array
     * @param len       Length of byte array
     * @param separator add a separator string between bytes. It is utils for MAC addresses that needs to
     *                  add a ':' between bytes
     * @return The newly created String
     */
    public static String byteArrayToString(byte[] array, int len, String separator) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(String.format("%02X", array[i]));
            if (separator != null) {
                sb.append(separator);
            }
        }
        if (sb.length() > 0 && separator != null && !separator.equals("")) {
            sb.delete(sb.length() - separator.length(), sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * Convert a byte array in a human readable version.
     * <p>
     * For instance, 1F value in byte gives "0x1F " in final string.
     *
     * @param array Byte array
     * @param len   Length of byte array
     * @return The newly created String
     */
    public static String byteArrayToString(@NonNull byte[] array, int len) {
        char[] hexChars = new char[len * 2];
        for (int i = 0; i < len; i++) {
            int value = array[i] & 0xFF;
            int index = i * 2;
            hexChars[index++] = HEX_ARRAY[value >>> 4];
            hexChars[index] = HEX_ARRAY[value & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Convert a byte array in a human readable version.
     * <p>
     * For instance, 1F value in byte gives "0x1F " in final string.
     *
     * @param array Byte array
     * @return The newly created String
     */
    public static String byteArrayToString(byte[] array) {

        if (array == null) {
            return "";
        }
        return byteArrayToString(array, array.length);
    }

    /**
     * Compare two arrays
     * <p>
     * If arrays do not have the same size, arrays are considered not equal
     *
     * @param a1 First array
     * @param a2 Second array
     * @return true if they are the same, false otherwise
     */
    public static boolean arrayCmp(byte[] a1, byte[] a2) {

        boolean ret = true;

        if (a1 != null && a2 != null) {
            if (a1.length != a2.length) {
                return false;
            }
            for (int i = 0; i < Math.min(a1.length, a2.length); i++) {
                if (a1[i] != a2[i]) {
                    return false;
                }
            }
        } else {
            ret = a1 == null && a2 == null;
        }

        return ret;
    }

    /**
     * Compare two arrays
     * <p>
     * If arrays do not have the same size, it only compares them using the
     * smaller size.
     *
     * @param a1 First array
     * @param a2 Second array
     * @return true if they are the same, false otherwise
     */
    public static boolean arrayCmpSoft(byte[] a1, byte[] a2) {

        boolean ret = true;

        if (a1 != null && a2 != null) {
            for (int i = 0; i < Math.min(a1.length, a2.length); i++) {
                if (a1[i] != a2[i]) {
                    return false;
                }
            }
        } else {
            ret = a1 == null && a2 == null;
        }

        return ret;
    }

    public static boolean arrayCmp(int[] a1, int[] a2) {

        boolean ret = true;

        if (a1 != null && a2 != null) {
            for (int i = 0; i < Math.min(a1.length, a2.length); i++) {
                if (a1[i] != a2[i]) {
                    return false;
                }
            }
        } else {
            ret = a1 == null && a2 == null;
        }

        return ret;
    }

    /**
     * Converts a 4 bytes array into an integer
     *
     * @param value     byte array to convert
     * @param bigEndian byte order: true big endian, false little endian
     * @return integer
     */
    public static int byteArrayToInt(byte[] value, boolean bigEndian) {
        value = fillByteArrayWithZero(value, bigEndian, SIZE_INT);
        ByteBuffer wrapped = ByteBuffer.wrap(value);
        return wrapped.order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN).getInt();
    }

    /**
     * Converts a 8 bytes array into along
     *
     * @param value     byte array to convert
     * @param bigEndian byte order: true big endian, false little endian
     * @return integer
     */
    public static long byteArrayToLong(byte[] value, boolean bigEndian) {
        value = fillByteArrayWithZero(value, bigEndian, SIZE_LONG);
        ByteBuffer wrapped = ByteBuffer.wrap(value);
        return wrapped.order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN).getLong();
    }

    /**
     * Converts a 2 bytes array into a short
     *
     * @param value     byte array to convert
     * @param bigEndian byte order: true big endian, false little endian
     * @return integer
     */
    public static short byteArrayToShort(byte[] value, boolean bigEndian) {
        value = fillByteArrayWithZero(value, bigEndian, SIZE_SHORT);
        ByteBuffer wrapped = ByteBuffer.wrap(value);
        return wrapped.order(bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN).getShort();
    }

    private static byte[] fillByteArrayWithZero(byte[] value, boolean bigEndian, int finalSize) {
        if (null != value) {
            if (value.length < finalSize) {
                if (bigEndian) {
                    value = appendTwoByteArrays(new byte[finalSize - value.length], value);
                } else {
                    value = appendTwoByteArrays(value, new byte[finalSize - value.length]);
                }
            }
        }
        return value;
    }

    /**
     * Converts an integer in a 4 bytes array
     *
     * @param value     integer to convert
     * @param bigEndian byte order: true big endian, false little endian
     * @return byte array
     */
    public static byte[] intToByteArray(int value, boolean bigEndian) {
        ByteBuffer b = ByteBuffer.allocate(4);
        b.order(bigEndian ? ByteOrder.BIG_ENDIAN
                          : ByteOrder.LITTLE_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
        b.putInt(value);
        return b.array();
    }

    /**
     * Converts an integer in a 4 bytes array
     *
     * @param value     integer to convert
     * @param bigEndian byte order: true big endian, false little endian
     * @return byte array
     */
    public static byte[] longToByteArray(long value, boolean bigEndian) {
        ByteBuffer b = ByteBuffer.allocate(4);
        b.order(bigEndian ? ByteOrder.BIG_ENDIAN
                          : ByteOrder.LITTLE_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
        b.putLong(value);

        return b.array();
    }

    // / **** Stream **** ///

    /**
     * Copy data from InputStream into OutputStream
     *
     * @param input  input stream
     * @param output output stream
     * @throws IOException exception
     */
    public static void copyStream(@Nullable InputStream input, @Nullable OutputStream output) throws IOException {
        if (input == null || output == null) {
            return;
        }
        byte[] buffer = new byte[2048]; // Adjust if you want
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    /**
     * Get a byte array from input stream. The input stream must be closed by the caller.
     *
     * @param is Input stream
     * @return byte array. If there is an error, empty byte array is returned.
     */
    public static byte[] getBytesFromInputStream(InputStream is) {
        byte[] data = new byte[0];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            copyStream(is, out);
            data = out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Closeables.closeQuietly(out);
        }
        return data;
    }

    /**
     * Parses a string containing hex characters (0123456789ABCDEF) to a byte
     * array.
     * <p>
     * Ex: 00AF1B = {0x00, 0xAF, 0x1B}
     * <p>
     * Note: This method assumes there is a pair number of characters.
     *
     * @param toBeParsed Hexadecimal string to be parsed
     * @return Byte array containing parsed data
     */
    public static byte[] parseHexStringToArray(String toBeParsed) {
        byte[] ret = new byte[toBeParsed.length() / 2];
        for (int i = 0; i < toBeParsed.length() / 2; i++) {
            String tmp = toBeParsed.substring(i * 2, i * 2 + 2);
            int val = Integer.parseInt(tmp, 16);
            ret[i] = (byte) val;
        }
        return ret;
    }

    /**
     * Mirror bits of the each byte of byte array
     *
     * @param toMirror byte array
     * @return byte array
     */
    public static byte[] mirrorBitsByteInArray(byte[] toMirror) {
        byte[] res = new byte[toMirror.length];
        for (int i = 0; i < toMirror.length; i++) {
            int newVal = 0x00;
            for (int j = 0; j < 8; j++) {
                newVal <<= 1;
                newVal |= (toMirror[i] & 1);
                toMirror[i] >>= 1;
            }
            res[i] = (byte) newVal;
        }
        return res;
    }

    /**
     * Mirror the Byte Array
     *
     * @param toMirror byte array
     * @return byte array
     */
    public static byte[] mirrorArray(byte[] toMirror) {
        byte[] res = new byte[toMirror.length];
        for (int i = 0; i < toMirror.length; i++) {
            res[toMirror.length - 1 - i] = toMirror[i];
        }
        return res;
    }

    /**
     * Removes a byte from an array
     *
     * @param array    the array to filter
     * @param valToRmv the value to remove
     * @return An array with valToRmv byte removed if present
     */
    public static byte[] removeFromArray(byte[] array, byte valToRmv) {
        int i = 0;
        boolean found = false;
        byte[] endArray = null;

        while ((i < array.length) && (!found)) {
            if (array[i] == valToRmv) {
                byte[] tempArray = new byte[array.length - i - 1];
                System.arraycopy(array, i + 1, tempArray, 0, tempArray.length);
                endArray = removeFromArray(tempArray, valToRmv);
                found = true;
            }
            i++;
        }

        if (found) {
            byte[] newArray = new byte[array.length - 1];
            System.arraycopy(array, 0, newArray, 0, i - 1);
            System.arraycopy(endArray, 0, newArray, i - 1, endArray.length);
            return (newArray);
        }
        return array;

    }

    /**
     * Append two byte array
     *
     * @param arrayA Array to append in first position
     * @param arrayB Array to append in second position
     * @return returns arrayA + arrayB
     */
    public static byte[] appendTwoByteArrays(byte[] arrayA, byte[] arrayB) {
        if (arrayA == null) {
            return arrayB;
        }
        if (arrayB == null) {
            return arrayA;
        }

        byte[] outputBytes = new byte[arrayA.length + arrayB.length];
        System.arraycopy(arrayA, 0, outputBytes, 0, arrayA.length);
        System.arraycopy(arrayB, 0, outputBytes, arrayA.length, arrayB.length);
        return outputBytes;
    }

    /**
     * insert a byte in an array
     *
     * @param array Array
     * @param b     byte to insert
     * @param index where byte will be inserted
     *              return the new byte array
     **/
    public static byte[] insertByteInArray(byte[] array, byte b, int index) {
        if (array == null) {
            return null;
        }
        if (index > array.length) {
            return null;
        }

        byte[] outputBytes = new byte[array.length + 1];
        System.arraycopy(array, 0, outputBytes, 0, index);
        outputBytes[index] = b;
        System.arraycopy(array, index, outputBytes, index + 1, array.length - index);
        return outputBytes;
    }

    /**
     * Concatenate byte arrays
     *
     * @param collection Collection of byte array
     * @return the concatenated byte array
     */
    public static byte[] concatByteArrays(Collection<byte[]> collection) {
        int size = 0;
        int index = 0;
        // calculate size
        for (byte[] array : collection) {
            if (array != null) {
                size += array.length;
            }
        }
        // allocation
        byte[] res = new byte[size];
        //copy
        for (byte[] array : collection) {
            if (array != null) {
                System.arraycopy(array, 0, res, index, array.length);
                index += array.length;
            }
        }
        return res;
    }

}
