package fr.coppernic.lib.utils.io;


import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@SuppressWarnings({"StatementWithEmptyBody", "unused"})
public final class StringHelper {

    private StringHelper() {
    }

    /**
     * Format a string in MAC format.
     * MAC format is ([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}
     * For instance 001122334455 = 00:11:22:33:44:55
     *
     * @param s String to format
     * @return MAC string or an empty one if error.
     */
    public static String stringToMac(String s) {

        StringBuilder sb = new StringBuilder("");
        if (!s.matches("[0-9A-Fa-f]{12}")) {
            // Log.e(TAG, "String does not match : " + s);
        } else {
            for (int i = 0; i < 6; i++) {

                sb.append(s.substring(i * 2, i * 2 + 2));
                sb.append(":");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * Test if a string is a Bluetooth MAC address
     *
     * @param s input string
     * @return true is s is a bluetooth MAC address, false otherwise
     */
    public static boolean isBdAddr(String s) {
        return s.matches("([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}");
    }

    /**
     * Transform a string with hexadecimal values to a string of ASCII
     * characters
     * For instance "616263" = "abc"
     * String shall contains only hexadecimal characters with no spaces and no
     * "0x". It uses Integer.parseInt internally. If the input string does not
     * contain valid characters, this method returns an empty string.
     *
     * @param hexString string with hexadecimal values
     * @return string of ASCII characters
     */
    public static String hexToAscii(String hexString) {
        StringBuilder output = new StringBuilder();
        if (!hexString.matches("[0-9A-Fa-f]*")) {
            // not valid
        } else if (hexString.length() % 2 != 0) {
            // not valid
        } else {
            for (int i = 0; i < hexString.length(); i += 2) {
                String str = hexString.substring(i, i + 2);
                output.append((char) Integer.parseInt(str, 16));
            }
        }
        return output.toString();
    }

    /**
     * Check if String s is contained in array array.
     *
     * @param array input
     * @param s     string to find
     * @return true if String s is in provided array, false otherwise
     */
    public static boolean isStringInArray(String[] array, String s) {
        if (array != null && s != null) {
            for (String str : array) {
                if (s.contentEquals(str)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if String s is contained in list.
     *
     * @param list list of strings
     * @param val  string to find
     * @return true if String val is in provided array, false otherwise
     */
    public static boolean isStringInList(List<String> list, String val) {
        for (String s : list) {
            if (s.contentEquals(val)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Convert a unicode formatted String into a standard String
     *
     * @param unicodeString The String to convert
     * @return a String
     **/
    public static String unicodeStringToString(String unicodeString) {

        if (unicodeString.length() < 1) {
            return unicodeString;
        }

        char[] name = unicodeString.toCharArray();
        char[] nameTrimmed = new char[name.length];
        int index = 0;
        for (int i = 0; i < nameTrimmed.length; i++) {
            if (name[i] != 0x00) {
                nameTrimmed[index] = name[i];
                index++;
            }
        }
        char[] nameFinal = new char[index];
        System.arraycopy(nameTrimmed, 0, nameFinal, 0, index);

        return (new String(nameFinal));

    }

    /**
     * Remove a all characters designed by car in string.
     * For instance, if car is a space, it removes all spaces in provided string
     *
     * @param input input string
     * @param car   character to find
     * @return output string
     */
    public static String removeCharInString(String input, String car) {
        StringBuilder sb = new StringBuilder();

        String[] array = input.split(car);
        for (String s : array) {
            sb.append(s);
        }

        return sb.toString();
    }

    /**
     * Concat strings
     *
     * @param in  list of strings to concatenate
     * @param sep String separator
     * @return Concatenated string
     */
    public static String concatString(String[] in, String sep) {
        return concatString(Arrays.asList(in), sep);
    }

    /**
     * Concat strings
     *
     * @param in  list of strings to concatenate
     * @param sep String separator
     * @return Concatenated string
     */
    public static String concatString(String[] in, int fromIndex, String sep) {
        return concatString(Arrays.asList(in), fromIndex, sep);
    }

    /**
     * Concat strings
     *
     * @param in  list of strings to concatenate
     * @param sep String separator
     * @return Concatenated string
     */
    public static String concatString(List<String> in, String sep) {
        StringBuilder sb = new StringBuilder();
        for (String tmp : in) {
            sb.append(tmp).append(sep);
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - sep.length());
        }
        return sb.toString();
    }

    /**
     * Concat strings
     *
     * @param in  list of strings to concatenate
     * @param sep String separator
     * @return Concatenated string
     */
    public static String concatString(Collection<String> in, int fromIndex, String sep) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String tmp : in) {
            if (fromIndex <= i) {
                sb.append(tmp).append(sep);
            }
            ++i;
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - sep.length());
        }
        return sb.toString();
    }

    /**
     * Return n times the provided string
     *
     * @param car input string
     * @param n   times
     * @return n times the input string
     */
    public static String getNChar(String car, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            sb.append(car);
        }
        return sb.toString();
    }


    /**
     * Format double with given decimal separator and number of decimals
     *
     * @param i   Double to format
     * @param num Number of decimal. 0 for infinite
     * @param sep Decimal separator
     * @return String representation of double
     */
    public static String formatDouble(double i, int num, String sep) {
        String res;
        if (num != 0) {
            String format = String.format(Locale.US, "%%.%df", num);
            res = String.format(format, i);
        } else {
            res = "" + i;
        }
        res = res.replaceAll("0+$", "");
        res = res.replaceAll("[.,]$", "");
        res = res.replaceAll("[.,]", sep);
        return res;
    }

    /**
     * Concatenate an array of object into a single string
     *
     * @param array object to concatenate
     * @param sep   Separator string between objects
     * @return Single string
     */
    public static String arrayToString(Object[] array, String sep) {
        if (array.length == 1) {
            return array[0].toString();
        } else if (array.length > 1) {
            StringBuilder sb = new StringBuilder();
            for (Object obj : array) {
                sb.append(obj.toString()).append(sep);
            }
            if (sep.length() > 0) {
                sb.delete(sb.length() - sep.length(), sb.length());
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    /**
     * Get the index of the n'iest character is a string
     * <p> For instance the third / in the string "/ab/cd/ef/gh" has 6 for its index.
     *
     * @param s   String input
     * @param car character to find
     * @param n   rank of the character to find
     * @return index of the character found or -1 if no characters are found
     */
    public static int getNIndexOf(String s, int car, int n) {
        int index = -1;
        for (int i = 0; i < n; i++) {
            index = s.indexOf(car, index + 1);
        }
        return index;
    }

    /**
     * Generate a random string with letters from a to z
     *
     * @param len Len of the random string
     * @return String generated
     */
    public static String generateRandomString(int len) {
        Random generator = new Random();
        StringBuilder sb = new StringBuilder();
        char tempChar;
        for (int i = 0; i < len; i++) {
            tempChar = (char) (generator.nextInt(26) + 'a');
            sb.append(tempChar);
        }
        return sb.toString();
    }

}
