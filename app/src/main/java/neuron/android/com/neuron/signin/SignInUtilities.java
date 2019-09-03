package neuron.android.com.neuron.signin;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.Auth;
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
import neuron.android.com.neuron.authentication.AuthenticationManager;
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

    /**
     * This method authenticates the user and starts the target activity if the user is new and the main activity if the user is not new
     * @param googleSignInAccount
     * @param currentContext
     * @param target
     */
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

    /**
     * This method authenticates a user and does nothing else. This method is used when the SSUA is resumed after being stopped
     * @param googleSignInAccount
     */
    public static void firebaseAuthWithGoogle(final GoogleSignInAccount googleSignInAccount) {
        System.out.println("[Neuron.SignInUtilities.firebaseAuthWithGoogle]: Authintication with google. googleSignInAccount id: " + googleSignInAccount.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        AuthenticationManager.getAuth().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    //todo: remove later if this call to get the user won't be needed
                    System.out.println("[Neuron.SignInUtilities.firebaseAuthWithGoogle]: Auth successful for " + user.getEmail());

                } else {
                    System.out.println("[Neuron.SignInUtilities.firebaseAuthWithGoogle]: Auth not successful! " + task.getException().getMessage());
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

    public static void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        AuthenticationManager.getAuth().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    System.out.println("[Neuron.RegisterActivity.handleFacebookAccessToken]: Facebook sign in successful!");
                    FirebaseUser user = AuthenticationManager.getCurrentUser(); //todo: remove this method call later if obsolete
                } else {
                    System.out.println("[Neuron.RegisterActivity.handleFacebookAccessToken]: Facebook sign in FAILED! " + task.getException().getMessage());
                }
            }
        });
    }

    public static void signInWithEmailPass(final Context currentContext, final String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    System.out.println("[Neuron.SignInUtilities.signInWithEmailPass]: Sign in for " + email + " is succesful.");

                    //start main
                    ActivityTools.startNewActivity(currentContext, MainActivity.class);
                } else {
                    System.out.println("[Neuron.SignInUtilities.signInWithEmailPass]: ERROR! Sign in failed: " + task.getException());

                    //todo: respond!
                }
            }
        });
    }
}
