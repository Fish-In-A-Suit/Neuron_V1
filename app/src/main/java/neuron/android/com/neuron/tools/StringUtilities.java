package neuron.android.com.neuron.tools;

import java.util.ArrayList;

public class StringUtilities {

    /**
     * Checks if the given string doesnt exist in the given source array
     * @param string
     * @param source
     * @return True if string in the source arraylist doesn't exist or false, if string in the source arraylist exists
     */
    public static boolean checkIfStringDoesntExistInArray(String string, ArrayList<String> source) {
        for(String value : source) {
            if(string.equals(value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Splits the source string at spacebar elements (" ") and chooses the n-th element
     * @param n From 0 to whatever
     * @param src
     * @return
     */
    public static String choose(int n, String src) {
        int index = n-1;
        String[] split = src.split(" ");

        try {
            return split[index];
        } catch (IndexOutOfBoundsException e) {
            System.err.println("[Neuron.StringUtilities.choose]: n " + n + " is greater than elements in src: " + src);
            return "";
        }
    }

    /**
     * This method is used primarily for displaying SHA1 and SHA256 in hex format
     * @param data
     * @return
     */
    public static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }
}
