package neuron.android.com.neuron;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import neuron.android.com.neuron.database.FirestoreManager;
import neuron.android.com.neuron.tools.ActivityTools;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirestoreManager.init();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //check for an existing signed-in user for google
        //todo: ENABLE THIS LATER!!!
        /*
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(googleSignInAccount==null) {
            ActivityTools.startNewActivity(this, MainActivity.class);
        }
        */
    }

    /**
     * This method gets called when "Register Here" is clicked. This method redirects the user to RegisterActivity.
     * @param v
     */
    public void onClick_registerHere(View v) {
        ActivityTools.startNewActivity(this, RegisterActivity.class);
    }

    /**
     * This method gets called when the sign in button is clicked. Attempts to sign the user in.
     * @param v
     */
    public void onClick_signIn(View v) {
        //todo: attempt to authenticate user
    }
}
