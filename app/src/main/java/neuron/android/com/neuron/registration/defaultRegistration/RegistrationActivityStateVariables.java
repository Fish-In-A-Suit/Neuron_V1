package neuron.android.com.neuron.registration.defaultRegistration;

import java.util.ArrayList;

public class RegistrationActivityStateVariables {
    private static ArrayList<String> usedUsernames;
    private static ArrayList<String> usedEmails;

    public static void setUsedUsernames(ArrayList<String> a) {
        usedUsernames = a;
    }

    public static void setUsedEmails(ArrayList<String> a) {
        usedEmails = a;
    }

    public static ArrayList<String> getUsedUsernames() {
        return usedUsernames;
    }

    public static ArrayList<String> getUsedEmails() {
        return usedEmails;
    }
}
