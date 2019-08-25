package neuron.android.com.neuron;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import neuron.android.com.neuron.database.FirestoreManager;
import neuron.android.com.neuron.registration.googleRegistration.GoogleSignInStateManager;
import neuron.android.com.neuron.tools.ActivityTools;
import neuron.android.com.neuron.tools.AnimationTools;
import neuron.android.com.neuron.tools.StringUtilities;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirestoreManager.init();
        AnimationTools.init();
    }

    @Override
    protected void onStart() {
        super.onStart();

        /*Logging app activations enables almost all other functionality.
         By logging an activation event, you can observe how frequently users activate your app,
         how much time they spend using it, and view other demographic information through Facebook Analytics for Apps.
        */
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        //this prints the key hash
            // Add code to print out the key hash
        //todo: remove this later!
        try {
            PackageInfo info = getPackageManager().getPackageInfo("neuron.android.com.neuron", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("KeyHash:", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("KeyHash:", e.toString());
        }

        try {
            PackageInfo info = getPackageManager().getPackageInfo("neuron.android.com.neuron", PackageManager.GET_SIGNATURES);

            for(Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(signature.toByteArray());
                String hash_key_hex = StringUtilities.convertToHex(md.digest());
                System.out.println("[Neuron.LoginActivity.onCreate]: sha256=" + hash_key_hex);
            }
        } catch(PackageManager.NameNotFoundException e) {
            System.err.println("[Neuron.LoginActivity.onCreate]: " + e.toString());
        } catch(NoSuchAlgorithmException e) {
            System.err.println("[Neuron.LoginActivity.onCreate]: " + e.toString());
        }


        //check for an existing signed-in user for google
        //todo: ENABLE THIS LATER!!!
        /*
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(googleSignInAccount==null) {
            ActivityTools.startNewActivity(this, MainActivity.class);
        }
        */
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(GoogleSignInStateManager.getGoogleSignInClient()!=null) {
            //todo: remove this later
            //signs out the current google sign in client
            GoogleSignInStateManager.getGoogleSignInClient().signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    System.out.println("[Neuron.LoginActivity.onDestroy]: Successfully signed out google sign in client");
                }
            });

        }

        System.out.println("[Neuron.LoginActivity.onDestroy]: Logging out of facebook");
        LoginManager.getInstance().logOut();
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
