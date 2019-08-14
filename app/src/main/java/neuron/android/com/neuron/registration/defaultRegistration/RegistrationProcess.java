package neuron.android.com.neuron.registration.defaultRegistration;

public class RegistrationProcess {
    private boolean usernameLongEnough;
    private boolean usernameNotAlreadyTaken;
    private boolean emailNotAlreadyTaken;
    private boolean passwordLongEnough;
    private boolean passwordsMatch;

    public RegistrationProcess() {
        usernameLongEnough = false;
        usernameNotAlreadyTaken = false;
        emailNotAlreadyTaken = false;
        passwordLongEnough = false;
        passwordsMatch = false;
    }

    public void setUsernameLongEnough(boolean value) {
        System.out.println("[Neuron.RegistrationProcess.setUsernameLongEnough]: = " + value);
        usernameLongEnough = value;
    }

    public void setUsernameNotAlreadyTaken(boolean value) {
        System.out.println("[Neuron.RegistrationProcess.setUsernameNotAlreadyTak]: = " + value);
        usernameNotAlreadyTaken = value;
    }

    public void setEmailNotAlreadyTaken(boolean value) {
        System.out.println("[Neuron.RegistrationProcess.setEmailNotAlreadyTaken]: = " + value);
        emailNotAlreadyTaken = value;
    }

    public void setPasswordLongEnough(boolean value) {
        System.out.println("[Neuron.RegistrationProcess.setPasswordLongEnough]: = " + value);
        passwordLongEnough = value;
    }

    public void setPasswordsMatch(boolean value) {
        System.out.println("[Neuron.RegistrationProcess.setPasswordsMatch]: = " + value);
        passwordsMatch = value;
    }

    public boolean isUsernameLongEnough() {
        return usernameLongEnough;
    }

    public boolean isNotUsernameNotAlreadyTaken() {
        return usernameNotAlreadyTaken;
    }

    public boolean isEmailNotAlreadyTaken() {
        return emailNotAlreadyTaken;
    }

    public boolean isPasswordLongEnough() {
        return passwordLongEnough;
    }

    public boolean isPasswordsMatch() {
        return passwordsMatch;
    }

    /**
     * @return The overall registration success. True if username and password are long enough and if passwords match. Else, returns false.
     */
    public boolean getOverallRegistrationSuccess() {
        if(usernameLongEnough && usernameNotAlreadyTaken && passwordLongEnough && passwordsMatch) {
            return true;
        } else {
            return false;
        }
    }
}
