package neuron.android.com.neuron.registration.defaultRegistration;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import neuron.android.com.neuron.MainActivity;
import neuron.android.com.neuron.R;
import neuron.android.com.neuron.authentication.AuthenticationManager;
import neuron.android.com.neuron.core.Constants;
import neuron.android.com.neuron.database.FirestoreManager;
import neuron.android.com.neuron.tools.StringUtilities;

/**
 * The role of this class is to track the registration process (and report errors) and enable the sign in button when registration process is complete. Registration process is
 * started as soon as an instance of this class is created.
 */
public class RegistrationManager {
    private EditText editText_username;
    private EditText editText_email;
    private EditText editText_password;
    private EditText editText_repeatPassword;

    //error views --> display exclamation marks
    private ImageView username_error_view;
    private static ImageView email_error_view;
    private ImageView password_error_view;
    private ImageView repeat_password_error_view;

    private TextView username_error_report_view;
    private static TextView email_error_report_view;
    private TextView password_error_report_view;
    private TextView repeat_password_error_report_view;

    private Button signUpButton;
    private static Context activityContext;

    private LinearLayout defaultRegistrationRootView;
    private ProgressBar loadingProgressBar;

    private neuron.android.com.neuron.registration.defaultRegistration.RegistrationProcess registrationProcess;

    private Drawable errorDrawable; //set this to report views when an error occurs

    public RegistrationManager(Context activityContext, EditText username, EditText email, EditText password, EditText repeatPassword,
                               ImageView username_error_view, ImageView email_error_view, ImageView password_error_view, ImageView repeat_password_error_view,
                               TextView username_error_report_view, TextView email_error_report_view, TextView password_error_report_view, TextView repeat_password_error_report_view,
                               Button signUpButton, LinearLayout defaultRegistrationRootView) {
        this.activityContext = activityContext;

        editText_username = username;
        editText_email = email;
        editText_password = password;
        editText_repeatPassword = repeatPassword;
        enableUserInputFields(false);

        this.username_error_view = username_error_view;
        this.email_error_view = email_error_view;
        this.password_error_view = password_error_view;
        this.repeat_password_error_view = repeat_password_error_view;

        this.username_error_report_view = username_error_report_view;
        this.email_error_report_view = email_error_report_view;
        this.password_error_report_view = password_error_report_view;
        this.repeat_password_error_report_view = repeat_password_error_report_view;

        this.signUpButton = signUpButton;
        this.defaultRegistrationRootView = defaultRegistrationRootView;

        //sets up the loadingProgressBar
        loadingProgressBar = new ProgressBar(activityContext);
        loadingProgressBar.setScaleX(0.6f);
        loadingProgressBar.setScaleY(0.6f);
        loadingProgressBar.setId(Constants.id_register_loading_bar);
        loadingProgressBar.setVisibility(View.INVISIBLE);
        //get all of the used user names and emails
        getUsedUsernamesAndEmails();

        configureSignUpButton();
    }

    /**
     * Starts tracking all of the input data in user input fields and modifying appropriate values
     */
    private void startRegistrationProcess() {
        registrationProcess = new neuron.android.com.neuron.registration.defaultRegistration.RegistrationProcess();

        superviseUsernameField(); //DONE
        superviseEmailField(); //DONE
        supervisePasswordField(); //DONE
        superviseRepeatPasswordField(); //DONE
    }

    /**
     * Supervises the username field.
     *     (1) checks if the username is long enough
     *          (1.1) if the username isn't long enough, sets usernameLongEnough in RP to false, else true
     *          (1.2) if the username is alreaedy taken, sets the usernameNotAlreadyTaken in RP to false, else true
     *
     *     (2)updates the error views
     *
     */
    private void superviseUsernameField() {
        editText_username.addTextChangedListener(new TextWatcher() {
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

    /**
     * Checks if the specified email is already taken and updates the error view
     */
    private void superviseEmailField() {
        editText_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean emailNotAlreadyInUse = StringUtilities.checkIfStringDoesntExistInArray(s.toString(), RegistrationActivityStateVariables.getUsedEmails());

                if(emailNotAlreadyInUse) {
                    //email isn't taken --> try to enable the sign in button
                    registrationProcess.setEmailNotAlreadyTaken(true);
                    updateReportViews(DefaultRegistrationView.EMAIL, false, "");
                    tryToEnableSignUpButton();
                } else {
                    //email already taken --> update error report view
                    registrationProcess.setEmailNotAlreadyTaken(false);

                    String reportMessage = activityContext.getResources().getString(R.string.registration_default_email_already_in_use);
                    updateReportViews(DefaultRegistrationView.EMAIL, true, reportMessage);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void supervisePasswordField() {
        editText_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean passwordLongEnough = neuron.android.com.neuron.registration.defaultRegistration.RegistrationUtilities.checkPasswordLength(s);
                boolean passwordsMatch = true;

                //checks if the passwords match
                if(!editText_repeatPassword.getText().toString().equals("")) {
                    System.out.println("[Neuron.RegistrationManager.supervisePasswordField]: Repeat password edit text contains some text. Checking if passwords match.");
                    //if repeat password edit text contains text, check if password match
                    if(s.toString().equals(editText_repeatPassword.getText().toString())) {
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

                    if(!editText_repeatPassword.getText().toString().equals("")) {
                        System.out.println("[Neuron.RegistrationManager.supervisePasswordField]: Repeat password contains text.");

                        if(neuron.android.com.neuron.registration.defaultRegistration.RegistrationUtilities.checkPasswordLength(editText_repeatPassword.getText().toString())) {
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
        editText_repeatPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean passwordLongEnough = neuron.android.com.neuron.registration.defaultRegistration.RegistrationUtilities.checkPasswordLength(s);
                boolean passwordsMatch = true;

                //checks if the passwords match
                if(!editText_password.getText().toString().equals("")) {
                    System.out.println("[Neuron.RegistrationManager.supervisePasswordField]: Repeat password edit text contains some text. Checking if passwords match.");
                    //if repeat password edit text contains text, check if password match
                    if(s.toString().equals(editText_password.getText().toString())) {
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

                    if(!editText_password.getText().toString().equals("")) {
                        System.out.println("[Neuron.RegistrationManager.supervisePasswordField]: Repeat password contains text.");

                        if(neuron.android.com.neuron.registration.defaultRegistration.RegistrationUtilities.checkPasswordLength(editText_password.getText().toString())) {
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
                error_view = username_error_view;
                error_report_view = username_error_report_view;
                break;
            case EMAIL:
                error_view = email_error_view;
                error_report_view = email_error_report_view;
                break;
            case PASSWORD:
                error_view = password_error_view;
                error_report_view = password_error_report_view;
                break;
            case REPEAT_PASSWORD:
                error_view = repeat_password_error_view;
                error_report_view = repeat_password_error_report_view;
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
     * Gets the usernames which have already been used from the database (and stores them inside usedUsernames).
     * Also, when done querying, it enables the user input fields so that the user can register.
     * As long as this method is querying usernames, display the loading animation above the root linear layout for default registration.
     */
    private void getUsedUsernamesAndEmails() {
        if(RegistrationActivityStateVariables.getUsedUsernames()!=null && RegistrationActivityStateVariables.getUsedEmails()!=null) {
            return;
        }

        startDefaultRegistrationLoadingAnimation();

        //query used usernames. when finished, enable the user input fields and stop the animation
        FirestoreManager.getCollectionReference(Constants.FIRESTORE_COLLECTION_USED_USERNAMES).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    ArrayList<String> usedUsernames = new ArrayList<>();

                    for(QueryDocumentSnapshot document : task.getResult()) {
                        String usedUsername = (String) document.getData().get(Constants.FIRESTORE_USERNAME_TAG);
                        usedUsernames.add(usedUsername);
                        System.out.println("[Neuron.RegistrationManager.getUsedUsernames]: Added " + usedUsername + " to usedUsernames");
                    }

                    RegistrationActivityStateVariables.setUsedUsernames(usedUsernames);

                    FirestoreManager.getCollectionReference(Constants.FIRESTORE_COLLECTION_USED_EMAILS).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                ArrayList<String> usedEmails = new ArrayList<>();

                                for(QueryDocumentSnapshot document : task.getResult()) {
                                    System.out.println("[Neuron.RegistrationManager.getUsedUsernamesAndEmails]: processing " + document.getData());

                                    String usedEmail = (String) document.getData().get(Constants.FIRESTORE_EMAIL_TAG);
                                    usedEmails.add(usedEmail);
                                    System.out.println("[Neuron.RegistrationManager.getUsedUsernamesAndEmails]: Added " + usedEmail + " to usedEmails");
                                }

                                RegistrationActivityStateVariables.setUsedEmails(usedEmails);

                                //stop loading animation
                                stopDefaultRegistrationLoadingAnimation();

                                //enable views
                                enableUserInputFields(true);

                                //starts the registration process when all of the user names are loaded
                                startRegistrationProcess();
                            } else {
                                System.out.println("[Neuron.RegistrationManager.getUsedUsernamesAndEmails]: Error getting documents: " + task.getException());
                            }
                        }
                    });


                } else {
                    System.out.println("[Neuron.RegistrationManager.getUsedUsernames]: Error getting documents: " + task.getException());
                }
            }
        });
    }

    /**
     * Starts the loading animation which lasts as long as the list of usernames is queried from the database.
     * It adds the loadingProgressBar to the top of defaultRegistrationRootView and starts the animation.
     */
    private void startDefaultRegistrationLoadingAnimation() {
        defaultRegistrationRootView.addView(loadingProgressBar, 0); //0 adds the view to the top of the linearlayout
        loadingProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Stops the loading animation.
     * It removes the loadingProgressBar from the top of defaultRegistrationRootView and stops the animation
     */
    private void stopDefaultRegistrationLoadingAnimation() {
        loadingProgressBar.setVisibility(View.INVISIBLE);
        defaultRegistrationRootView.removeView(loadingProgressBar);
    }

    private void enableUserInputFields(boolean value) {
        if(value == true) {
            System.out.println("[Neuron.RegistrationManager.enableUserInputFields]: Enabling user input fields.");
            //enable all of the username input fields
            editText_username.setEnabled(true);
            editText_email.setEnabled(true);
            editText_password.setEnabled(true);
            editText_repeatPassword.setEnabled(true);
        } else {
            System.out.println("[Neuron.RegistrationManager.enableUserInputFields]: Disabling user input fields.");
            //disable all of the username input fields
            editText_username.setEnabled(false);
            editText_email.setEnabled(false);
            editText_password.setEnabled(false);
            editText_repeatPassword.setEnabled(false);
        }
    }

    /**
     * Configures the sign up button
     */
    private void configureSignUpButton() {
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if a user can click this button, attempt to auth the user to firebase and store all of the user data
                String username = editText_username.getText().toString();
                String email = editText_email.getText().toString();
                String password = editText_password.getText().toString();

                //authenticate user (and also send to database, due to first time sign up)
                AuthenticationManager.authenticateUserWithEmailAndPassword(username, email, password, activityContext, MainActivity.class, true);

                //send user to database
                //FirestoreManager.saveUserData(username, email);
            }
        });
    }

    public Button getSignUpButton() {
        return signUpButton;
    }

    public static void showMalformedEmailError() {
        email_error_view.setVisibility(View.VISIBLE);
        email_error_report_view.setText(activityContext.getResources().getString(R.string.registration_default_email_does_not_exist));
    }
}
