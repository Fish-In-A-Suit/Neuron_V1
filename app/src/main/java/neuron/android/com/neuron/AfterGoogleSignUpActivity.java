package neuron.android.com.neuron;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import neuron.android.com.neuron.core.Constants;
import neuron.android.com.neuron.database.DatabaseUser;
import neuron.android.com.neuron.database.FirestoreManager;
import neuron.android.com.neuron.registration.googleRegistration.AGSUmanager;
import neuron.android.com.neuron.tools.ActivityTools;

public class AfterGoogleSignUpActivity extends AppCompatActivity {
    private TextView AGSU_name;
    private AGSUmanager AGSUmanager;

    private Context activityContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_google_sign_up);

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

        AGSUmanager = new AGSUmanager(this, incompleteDatabaseUser,
                usernameField, passwordField, repeatPasswordField,
                AGSU_username_error_view, AGSU_password_error_view, AGSU_repeat_password_error_view,
                AGSU_username_error_report_view, AGSU_password_error_report_view, AGSU_repeat_password_error_report_view,
                signUpButton);

    }

    /**
     * This method gets called when the sign up button is clicked. It saves the db user to the database
     * @param view
     */
    public void onClick_AGSU_signUp(View view) {
        FirestoreManager.saveUserData(AGSUmanager.getDatabaseUser());

        //add a password-email auth method
        AuthCredential credential = EmailAuthProvider.getCredential(AGSUmanager.getDatabaseUser().getEmail(), AGSUmanager.getDatabaseUser().getPassword());
        FirebaseAuth.getInstance().getCurrentUser().linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    System.out.println("[Neuron.AfterGoogleSignUpActivity.onClick_AGSU_signUp]: link with email credential SUCCESSFUL! User uid = " + task.getResult().getUser().getDisplayName());
                    ActivityTools.startNewActivity(activityContext, MainActivity.class);
                } else {
                    System.err.println("[Neuron.AfterGoogleSignUpActivity.onClick_AGSU_signUp]: ERROR: link with email credential FAILED! " + task.getException().getMessage());
                }
            }
        });
    }
}
