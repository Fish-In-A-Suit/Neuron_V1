package neuron.android.com.neuron.registration.googleRegistration;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import neuron.android.com.neuron.R;
import neuron.android.com.neuron.database.DatabaseUser;
import neuron.android.com.neuron.registration.defaultRegistration.DefaultRegistrationView;
import neuron.android.com.neuron.registration.defaultRegistration.RegistrationActivityStateVariables;
import neuron.android.com.neuron.registration.defaultRegistration.RegistrationProcess;
import neuron.android.com.neuron.tools.StringUtilities;

/**
 * This class is responsible for tracking the process of sign up in the AfterGoogleSignUpActivity
 */
public class AGSUmanager {
    private RegistrationProcess registrationProcess;

    private Context activityContext;
    private DatabaseUser dbUser;

    private EditText usernameField;
    private EditText passwordField;
    private EditText repeatPasswordField;

    private ImageView AGSU_username_error_view;
    private ImageView AGSU_password_error_view;
    private ImageView AGSU_repeat_password_error_view;

    private TextView AGSU_username_error_report_view;
    private TextView AGSU_password_error_report_view;
    private TextView AGSU_repeat_password_error_report_view;

    private Button signUpButton;

    public AGSUmanager(Context activityContext, DatabaseUser incompleteDbUser,
                       EditText usernameField, EditText passwordField, EditText repeatPasswordField,
                       ImageView AGSU_username_error_view, ImageView AGSU_password_error_view, ImageView AGSU_repeat_password_error_view,
                       TextView AGSU_username_error_report_view, TextView AGSU_password_error_report_view, TextView AGSU_repeat_password_error_report_view,
                       Button signUpButton) {
        registrationProcess = new RegistrationProcess();
        registrationProcess.setEmailNotAlreadyTaken(true);

        this.activityContext = activityContext;
        this.dbUser = incompleteDbUser;

        this.usernameField = usernameField;
        this.passwordField = passwordField;
        this.repeatPasswordField = repeatPasswordField;

        this.AGSU_username_error_view = AGSU_username_error_view;
        this.AGSU_password_error_view = AGSU_password_error_view;
        this.AGSU_repeat_password_error_view = AGSU_repeat_password_error_view;

        this.AGSU_username_error_report_view = AGSU_username_error_report_view;
        this.AGSU_password_error_report_view = AGSU_password_error_report_view;
        this.AGSU_repeat_password_error_report_view = AGSU_repeat_password_error_report_view;

        this.signUpButton = signUpButton;

        startAGSUprocess();
    }

    private void startAGSUprocess() {
        superviseUsernameField();
        supervisePasswordField();
        superviseRepeatPasswordField();
    }

    private void superviseUsernameField() {
        usernameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //unnecessary
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String reportMessage = ""; //the message that gets displayed in the report view

                boolean usernameNotAlreadyInUse = false;
                boolean usernameLongEnough = neuron.android.com.neuron.registration.defaultRegistration.RegistrationUtilities.checkUsernameLength(s);

                if(usernameLongEnough == true) {
                    usernameNotAlreadyInUse = StringUtilities.checkIfStringDoesntExistInArray(s.toString(), RegistrationActivityStateVariables.getUsedUsernames());
                }

                registrationProcess.setUsernameLongEnough(usernameLongEnough);
                registrationProcess.setUsernameNotAlreadyTaken(usernameNotAlreadyInUse);

                if(usernameNotAlreadyInUse == false) {
                    reportMessage = activityContext.getResources().getString(R.string.registration_default_username_already_in_use);
                }

                if(usernameLongEnough == false) {
                    reportMessage = activityContext.getResources().getString(R.string.registration_default_username_not_long_enough);
                }

                if(usernameLongEnough == true && usernameNotAlreadyInUse == true) {
                    //username is long enough and not in use --> no error
                    updateReportViews(DefaultRegistrationView.USERNAME, false, reportMessage);

                    //try to unlock sign up button
                    tryToEnableSignUpButton();
                } else {
                    //either username not long enough or already in use --> display error
                    updateReportViews(DefaultRegistrationView.USERNAME, true, reportMessage);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //unnecessary
            }
        });
    }

    private void supervisePasswordField() {
        passwordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean passwordLongEnough = neuron.android.com.neuron.registration.defaultRegistration.RegistrationUtilities.checkPasswordLength(s);
                boolean passwordsMatch = true;

                //checks if the passwords match
                if(!repeatPasswordField.getText().toString().equals("")) {
                    System.out.println("[Neuron.RegistrationManager.supervisePasswordField]: Repeat password edit text contains some text. Checking if passwords match.");
                    //if repeat password edit text contains text, check if password match
                    if(s.toString().equals(repeatPasswordField.getText().toString())) {
                        System.out.println("[Neuron.RegistrationManager.supervisePasswordField]: Passwords match.");
                        passwordsMatch = true;
                    } else {
                        System.out.println("[Neuron.RegistrationManager.supervisePasswordField]: Passwords don't match.");
                        passwordsMatch = false;
                    }
                }

                String reportMessage = "";
                //chooses the error message and update state of RP
                if(passwordsMatch == false) {
                    registrationProcess.setPasswordsMatch(false);
                    reportMessage = activityContext.getResources().getString(R.string.registration_default_passwords_dont_match);
                } else {
                    registrationProcess.setPasswordsMatch(true);
                    reportMessage="";
                }

                if(passwordLongEnough == false) {
                    registrationProcess.setPasswordLongEnough(false);
                    reportMessage = activityContext.getResources().getString(R.string.registration_default_password_too_short);
                } else {
                    registrationProcess.setPasswordLongEnough(true);
                }

                System.out.println("[Neuron.RegistrationManager.supervisePasswordField]: Choose report message: " + reportMessage);

                //display error messages
                if(passwordsMatch == false) {
                    System.out.println("[Neuron.RegistrationManager.supervisePasswordField]: Passwords don't match! Updating password and repeat password error views with rm: " + reportMessage);
                    updateReportViews(DefaultRegistrationView.PASSWORD, true, reportMessage);
                    updateReportViews(DefaultRegistrationView.REPEAT_PASSWORD, true, reportMessage);
                } else {
                    System.out.println("[Neuron.RegistrationManager.supervisePasswordField]: Passwords match! Clearing errors from error views");
                    updateReportViews(DefaultRegistrationView.PASSWORD, false, "");
                    updateReportViews(DefaultRegistrationView.REPEAT_PASSWORD, false, "");
                }

                if(passwordLongEnough == false) {
                    System.out.println("[Neuron.RegistrationManager.supervisePasswordField]: Password isn't long enough. Updating password error views.");
                    //password isn't long enough --> error
                    updateReportViews(DefaultRegistrationView.PASSWORD, true, reportMessage);

                    if(!repeatPasswordField.getText().toString().equals("")) {
                        System.out.println("[Neuron.RegistrationManager.supervisePasswordField]: Repeat password contains text.");

                        if(neuron.android.com.neuron.registration.defaultRegistration.RegistrationUtilities.checkPasswordLength(repeatPasswordField.getText().toString())) {
                            System.out.println("[Neuron.RegistrationManager.supervisePasswordField]: Repeat password text is longer than min pass length, clearing error from repeat password error views.");
                            //editText repeat password has text of greater length than minimum password length --> clear any errors
                            updateReportViews(DefaultRegistrationView.REPEAT_PASSWORD, false, "");
                        } else {
                            System.out.println("[Neuron.RegistrationManager.supervisePasswordField]: Repeat password text is shorter than min pass length, displaying error in error views.");
                            updateReportViews(DefaultRegistrationView.REPEAT_PASSWORD, true, reportMessage);
                        }
                    }
                } else {
                    System.out.println("[Neuron.RegistrationManager.supervisePasswordField]: Password length is sufficient --> Clearing errors.");
                    //password is long enough --> clear error
                    if(passwordsMatch==true) {
                        updateReportViews(DefaultRegistrationView.PASSWORD, false, "");
                    }
                }

                tryToEnableSignUpButton();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void superviseRepeatPasswordField() {
        repeatPasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean passwordLongEnough = neuron.android.com.neuron.registration.defaultRegistration.RegistrationUtilities.checkPasswordLength(s);
                boolean passwordsMatch = true;

                //checks if the passwords match
                if(!passwordField.getText().toString().equals("")) {
                    System.out.println("[Neuron.RegistrationManager.supervisePasswordField]: Repeat password edit text contains some text. Checking if passwords match.");
                    //if repeat password edit text contains text, check if password match
                    if(s.toString().equals(passwordField.getText().toString())) {
                        System.out.println("[Neuron.RegistrationManager.supervisePasswordField]: Passwords match.");
                        passwordsMatch = true;
                    } else {
                        System.out.println("[Neuron.RegistrationManager.supervisePasswordField]: Passwords don't match.");
                        passwordsMatch = false;
                    }
                }

                String reportMessage = "";
                //chooses the error message and update state of RP
                if(passwordsMatch == false) {
                    registrationProcess.setPasswordsMatch(false);
                    reportMessage = activityContext.getResources().getString(R.string.registration_default_passwords_dont_match);
                } else {
                    registrationProcess.setPasswordsMatch(true);
                    reportMessage="";
                }

                if(passwordLongEnough == false) {
                    registrationProcess.setPasswordLongEnough(false);
                    reportMessage = activityContext.getResources().getString(R.string.registration_default_password_too_short);
                } else {
                    registrationProcess.setPasswordLongEnough(true);
                }

                System.out.println("[Neuron.RegistrationManager.supervisePasswordField]: Choose report message: " + reportMessage);

                //display error messages
                if(passwordsMatch == false) {
                    System.out.println("[Neuron.RegistrationManager.supervisePasswordField]: Passwords don't match! Updating password and repeat password error views with rm: " + reportMessage);
                    updateReportViews(DefaultRegistrationView.PASSWORD, true, reportMessage);
                    updateReportViews(DefaultRegistrationView.REPEAT_PASSWORD, true, reportMessage);
                } else {
                    System.out.println("[Neuron.RegistrationManager.supervisePasswordField]: Passwords match! Clearing errors from error views");
                    updateReportViews(DefaultRegistrationView.PASSWORD, false, "");
                    updateReportViews(DefaultRegistrationView.REPEAT_PASSWORD, false, "");
                }

                if(passwordLongEnough == false) {
                    System.out.println("[Neuron.RegistrationManager.supervisePasswordField]: Password isn't long enough. Updating password error views.");
                    //password isn't long enough --> error
                    updateReportViews(DefaultRegistrationView.REPEAT_PASSWORD, true, reportMessage);

                    if(!passwordField.getText().toString().equals("")) {
                        System.out.println("[Neuron.RegistrationManager.supervisePasswordField]: Repeat password contains text.");

                        if(neuron.android.com.neuron.registration.defaultRegistration.RegistrationUtilities.checkPasswordLength(passwordField.getText().toString())) {
                            System.out.println("[Neuron.RegistrationManager.supervisePasswordField]: Repeat password text is longer than min pass length, clearing error from repeat password error views.");
                            //editText repeat password has text of greater length than minimum password length --> clear any errors
                            updateReportViews(DefaultRegistrationView.PASSWORD, false, "");
                        } else {
                            System.out.println("[Neuron.RegistrationManager.supervisePasswordField]: Repeat password text is shorter than min pass length, displaying error in error views.");
                            updateReportViews(DefaultRegistrationView.PASSWORD, true, reportMessage);
                        }
                    }
                } else {
                    System.out.println("[Neuron.RegistrationManager.supervisePasswordField]: Password length is sufficient --> Clearing errors.");
                    //password is long enough --> clear error
                    if(passwordsMatch==true) {
                        updateReportViews(DefaultRegistrationView.REPEAT_PASSWORD, false, "");
                    }
                }

                tryToEnableSignUpButton();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * Updates the report views
     * @param view either USERNAME, EMAIL, PASSWORD or REPEAT_PASSWORD
     * @param error true if an error has occurred, or false if no error has occurred
     * @param errorMessage The error message to be shown
     */
    private void updateReportViews(DefaultRegistrationView view, boolean error, String errorMessage) {
        System.out.println("[Neuron.RegistrationManager.updateReportViews]: Updating for " + view + " with error value " + error + " with error message: " + errorMessage);
        ImageView error_view = null;
        TextView error_report_view = null;

        //chooses which view to update
        switch(view) {
            case USERNAME:
                error_view = AGSU_username_error_view;
                error_report_view = AGSU_username_error_report_view;
                break;
            case PASSWORD:
                error_view = AGSU_password_error_view;
                error_report_view = AGSU_password_error_report_view;
                break;
            case REPEAT_PASSWORD:
                error_view = AGSU_repeat_password_error_view;
                error_report_view = AGSU_repeat_password_error_report_view;
                break;
        }

        if(error) {
            //if there has been an error, display exclamation mark and error report msg
            error_view.setVisibility(View.VISIBLE);
            error_report_view.setText(errorMessage);
        } else {
            error_view.setVisibility(View.INVISIBLE);
            error_report_view.setText("");
        }
    }

    /**
     * Tries to enable the sign up button by looking at the state of registrationProcess
     */
    private void tryToEnableSignUpButton() {
        if(registrationProcess.getOverallRegistrationSuccess()) {
            signUpButton.setEnabled(true);
        } else {
            signUpButton.setEnabled(false);
        }
    }

    /**
     * @return The complete databse user instance after AGSU process
     */
    public DatabaseUser getDatabaseUser() {
        dbUser.setUsername(usernameField.getText().toString());
        dbUser.setPassword(passwordField.getText().toString());
        return dbUser;
    }
}
