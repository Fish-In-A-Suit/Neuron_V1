package neuron.android.com.neuron.signin;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import neuron.android.com.neuron.R;
import neuron.android.com.neuron.authentication.AuthenticationManager;
import neuron.android.com.neuron.core.Constants;
import neuron.android.com.neuron.database.DatabaseUser;
import neuron.android.com.neuron.registration.googleRegistration.GoogleSignUpAccountManager;
import neuron.android.com.neuron.tools.ActivityTools;

public class SignInUtilities {
    public static void signInWithGoogle(Context activityContext, FragmentActivity activity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activityContext.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(activityContext, gso);

        Intent signInIntent = googleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, Constants.request_code_google_sign_in);
    }

    public static DatabaseUser determineSignInSuccess(Intent data, Context sourceActivity, Class targetActivityClass) {
        return null;
    }

    public static void firebaseAuthWithGoogle(GoogleSignInAccount googleSignInAccount, final Context currentContext, final Class target) {
        System.out.println("[Neuron.SignInUtilities.firebaseAuthWithGoogle]: googleSignInAccount id: " + googleSignInAccount.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    System.out.println("[Neuron.SignInUtilities.firebaseAuthWithGoogle]: Sign in with credential successful.");
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    DatabaseUser incompleteDatabaseUser = new DatabaseUser("", user.getEmail(), "", user.getUid());
                    ActivityTools.startNewActivity(currentContext, target, Constants.PARCELABLE_KEY_INCOMPLETE_DATABASE_USER, incompleteDatabaseUser);
                } else {
                    System.out.println("[Neuron.SignInUtilities.firebaseAuthWithGoogle]: ERROR! Sign in with credential failed. " + task.getException());
                }
            }
        });
    }
}
