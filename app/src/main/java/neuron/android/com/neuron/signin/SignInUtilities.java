package neuron.android.com.neuron.signin;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import neuron.android.com.neuron.MainActivity;
import neuron.android.com.neuron.R;
import neuron.android.com.neuron.core.Constants;
import neuron.android.com.neuron.database.DatabaseUser;
import neuron.android.com.neuron.registration.googleRegistration.GoogleSignInStateManager;
import neuron.android.com.neuron.tools.ActivityTools;
import neuron.android.com.neuron.tools.StringUtilities;

public class SignInUtilities {
    public static void signInWithGoogle(Context activityContext, FragmentActivity activity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activityContext.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(activityContext, gso);

        GoogleSignInStateManager.setGoogleSignInClient(googleSignInClient);

        Intent signInIntent = googleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, Constants.request_code_google_sign_in);
    }

    public static DatabaseUser determineSignInSuccess(Intent data, Context sourceActivity, Class targetActivityClass) {
        return null;
    }

    public static void firebaseAuthWithGoogle(final GoogleSignInAccount googleSignInAccount, final Context currentContext, final Class target) {
        System.out.println("[Neuron.SignInUtilities.firebaseAuthWithGoogle]: googleSignInAccount id: " + googleSignInAccount.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    System.out.println("[Neuron.SignInUtilities.firebaseAuthWithGoogle]: Sign in with credential successful.");
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(task.getResult().getAdditionalUserInfo().isNewUser()) {
                        //user is new, go to after google sign up
                        DatabaseUser incompleteDatabaseUser = new DatabaseUser("", user.getEmail(), "", user.getUid());

                        String name = StringUtilities.choose(1, user.getDisplayName());

                        ActivityTools.startNewActivity(currentContext, target, Constants.PARCELABLE_KEY_INCOMPLETE_DATABASE_USER, incompleteDatabaseUser, name);
                    } else {
                       ActivityTools.startNewActivity(currentContext, MainActivity.class);
                    }
                } else {
                    System.out.println("[Neuron.SignInUtilities.firebaseAuthWithGoogle]: ERROR! Sign in with credential failed. " + task.getException());
                }
            }
        });
    }

    public static void handleFacebookAccessToken(AccessToken token, final Context activityContext, final Class target) {
        System.out.println("[Neuron.RegisterActivity.handleFacebookAccessToken]: Handling token: " + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    System.out.println("[Neuron.RegisterActivity.handleFacebookAccessToken]: Facebook sign in successful!");
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(task.getResult().getAdditionalUserInfo().isNewUser()) {
                        //if is new user, launch SecondarySignUpActivity
                        System.out.println("[Neuron.RegisterActivity.handleFacebookAccessToken]: User is new. Launching secondary signup actiivty");

                        DatabaseUser incompleteDatabaseUser = new DatabaseUser("", user.getEmail(), "", user.getUid());
                        String name = StringUtilities.choose(1, user.getDisplayName());
                        ActivityTools.startNewActivity(activityContext, target, Constants.PARCELABLE_KEY_INCOMPLETE_DATABASE_USER, incompleteDatabaseUser, name);

                    } else {
                        //not a new user, already registered to db and firebase auth, start main
                        ActivityTools.startNewActivity(activityContext, MainActivity.class);
                    }

                    System.out.println("[Neuron.RegisterActivity.handleFacebookAccessToken]: user display name = " + user.getDisplayName() + " | user email = " + user.getEmail());

                    //todo: check if is new user! continue here!
                } else {
                    System.out.println("[Neuron.RegisterActivity.handleFacebookAccessToken]: ERROR signing in to facebook! " + task.getException().getMessage() + "\n" + " caused by: " + task.getException().getCause() );
                }
            }
        });
    }
}
