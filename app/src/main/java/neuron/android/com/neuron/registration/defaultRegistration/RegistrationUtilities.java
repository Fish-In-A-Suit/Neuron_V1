package neuron.android.com.neuron.registration.defaultRegistration;

import neuron.android.com.neuron.core.Constants;

/**
 * Class which provides methods for the registration process
 */
public class RegistrationUtilities {

    /**
     * Checks if the length of the specified CharSequence is greater than Constants.MIN_USERNAME_LENGTH
     * @param s
     * @return
     */
    public static boolean checkUsernameLength(CharSequence s) {
        if(s.length() >= Constants.MIN_USERNAME_LENGTH) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the length of the specified CharSequence is greater than Constants.MIN_PASSWORD_LENGTH
     * @param s
     * @return
     */
    public static boolean checkPasswordLength(CharSequence s) {
        if(s.length() >= Constants.MIN_PASSWORD_LENGTH) {
            return true;
        } else {
            return false;
        }
    }
}
