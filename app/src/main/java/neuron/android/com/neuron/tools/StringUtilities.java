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
}
