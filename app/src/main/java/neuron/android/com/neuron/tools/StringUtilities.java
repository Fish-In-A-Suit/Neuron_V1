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
}
