package neuron.android.com.neuron;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;

import neuron.android.com.neuron.authentication.AuthenticationManager;
import neuron.android.com.neuron.core.Constants;
import neuron.android.com.neuron.termination.ProtectSignupTermination;
import neuron.android.com.neuron.core.SignupType;
import neuron.android.com.neuron.termination.TerminatedSnapshotManager;
import neuron.android.com.neuron.database.DatabaseUser;
import neuron.android.com.neuron.database.FirestoreManager;
import neuron.android.com.neuron.registration.googleRegistration.SecondarySignUpManager;
import neuron.android.com.neuron.tools.ActivityTools;
import neuron.android.com.neuron.tools.AnimationTools;
import neuron.android.com.neuron.verification.EmailVerification;

public class SecondarySignUpActivity extends AppCompatActivity implements ProtectSignupTermination {
    private TextView AGSU_name;
    private SecondarySignUpManager SecondarySignUpManager;

    private Context activityContext;

    private LinearLayout signUpButton_rootLayout; //used to display the loading animation when signing up a user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary_signup);

        signUpButton_rootLayout = (LinearLayout) findViewById(R.id.AGSU_signup_button_root_layout);

        activityContext = this;

        AGSU_name = findViewById(R.id.AGSU_name);

        DatabaseUser incompleteDatabaseUser = getIntent().getParcelableExtra(Constants.PARCELABLE_KEY_INCOMPLETE_DATABASE_USER);
        System.out.println("[Neuron.AfterGoogleSignUpActvity.onCreate]: Displaying incomplete db user: " + incompleteDatabaseUser.toString());

        String name = getIntent().getStringExtra(Constants.PARCELABLE_KEY_NAME);
        AGSU_name.setText(name);

        EditText usernameField = (EditText) findViewById(R.id.AGSU_username_edit_text);
        EditText passwordField = (EditText) findViewById(R.id.AGSU_password_edit_text);
        EditText repeatPasswordField = (EditText) findViewById(R.id.AGSU_repeat_password_edit_text);

        ImageView AGSU_username_error_view = (ImageView) findViewById(R.id.AGSU_username_error_view);
        ImageView AGSU_password_error_view = (ImageView) findViewById(R.id.AGSU_password_error_view);
        ImageView AGSU_repeat_password_error_view = (ImageView) findViewById(R.id.AGSU_repeat_password_error_view);

        TextView AGSU_username_error_report_view = (TextView) findViewById(R.id.AGSU_username_error_report_view);
        TextView AGSU_password_error_report_view = (TextView) findViewById(R.id.AGSU_password_error_report_view);
        TextView AGSU_repeat_password_error_report_view = (TextView) findViewById(R.id.AGSU_repeat_password_error_report_view);

        Button signUpButton = (Button) findViewById(R.id.AGSU_sign_up_button);
        LinearLayout signupRootLinearLayout = (LinearLayout) findViewById(R.id.AGSU_root_linear_layout);

        SecondarySignUpManager = new SecondarySignUpManager(this, incompleteDatabaseUser,
                usernameField, passwordField, repeatPasswordField,
                AGSU_username_error_view, AGSU_password_error_view, AGSU_repeat_password_error_view,
                AGSU_username_error_report_view, AGSU_password_error_report_view, AGSU_repeat_password_error_report_view,
                signUpButton, signupRootLinearLayout);

        Constants.isSecondarySignUpInProcess = true;

    }

    @Override
    protected void  onStop() {
        super.onStop();

        System.out.println("[Neuron.SSUA.onStop]: here");
        ProtectSignupTermination.tryToDestroySignUpClient(SignupType.SECONDARY);
    }

    @Override
    protected void onResume() {
        super.onResume();

        System.out.println("[Neuron.SSUA.onResume]: here");
        if(TerminatedSnapshotManager.getSecondarySignupTerminatedSnapshot()!=null) {
            System.out.println("[Neuron.SSUA.onResume]: Terminated snapshot exists. Trying to restore it.");
            TerminatedSnapshotManager.getSecondarySignupTerminatedSnapshot().restore();
        } else {
            System.out.println("[Neuron.SSUA.onResume]: Terminated snapshot DOESN'T exist!");
        }
    }

    /**
     * This method gets called when the sign up button is clicked. It saves the db user to the database
     * @param view
     */
    public void onClick_AGSU_signUp(View view) {
        AnimationTools.startLoadingAnimation(Constants.ANIMATION_CODE_AGSU_LOADING, this, signUpButton_rootLayout, R.style.AGSU_loading_progress_bar);

        FirestoreManager.saveUserData(SecondarySignUpManager.getDatabaseUser());

        //add a password-email auth method
        AuthCredential credential = EmailAuthProvider.getCredential(SecondarySignUpManager.getDatabaseUser().getEmail(), SecondarySignUpManager.getDatabaseUser().getPassword());
        AuthenticationManager.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    System.out.println("[Neuron.SecondarySignUpActivity.onClick_AGSU_signUp]: link with email credential SUCCESSFUL! User uid = " + task.getResult().getUser().getDisplayName());
                    Constants.isSignUpInProcess = false; //sign up is now finished

                    //in AGSU user is always new --> send verification email
                    EmailVerification.sendVerificationEmail();

                    Constants.isSecondarySignUpInProcess = false; //end of secondary sign up process

                    ActivityTools.startNewActivity(activityContext, MainActivity.class);
                } else {
                    System.err.println("[Neuron.SecondarySignUpActivity.onClick_AGSU_signUp]: ERROR: link with email credential FAILED! " + task.getException().getMessage());
                }

                AnimationTools.stopLoadingAnimation(Constants.ANIMATION_CODE_AGSU_LOADING);
            }
        });

    }
}
