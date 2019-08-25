package neuron.android.com.neuron.authentication;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import neuron.android.com.neuron.database.FirestoreManager;
import neuron.android.com.neuron.registration.defaultRegistration.RegistrationManager;
import neuron.android.com.neuron.tools.ActivityTools;
import neuron.android.com.neuron.verification.EmailVerification;

public class AuthenticationManager {
    private static FirebaseAuth firebaseAuth;

    public static void initialize() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * Tries to authenticate user with email and password to firebase, and starts target activity from context if those two variables are provided.
     * If successful, this method also signs the user in.
     * @param username Used for first-time sign up
     * @param email
     * @param password
     * @param sourceContext
     * @param target
     * @param firstTimeSignUp True if the user is signing up for the first time, or false, if the user has already signed up. If true, user data is also sent to the database.
     */
    public static void authenticateUserWithEmailAndPassword(final String username, final String email, final String password, @Nullable final Context sourceContext, @Nullable final Class target, boolean firstTimeSignUp) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    System.out.println("[Neuron.AuthenticationManager.authenticateUserWithEmailAndPassword]: Authentication for user with email " + email + " SUCCESSFUL!");

                    signUserInWithEmailAndPassword(username, email, password, true);

                    if(null!=sourceContext && null!=target) {
                        ActivityTools.startNewActivity(sourceContext, target);

                        //user is always new for createUserWithEmail, always send verification email
                        EmailVerification.sendVerificationEmail();
                    }
                } else {
                    System.out.println("[Neuron.AuthenticationManager.authenticateUserWithEmailAndPassword]: Sign up failed: " + task.getException());
                    RegistrationManager.showMalformedEmailError();
                }
            }
        });
    }

    /**
     * Attempts to sign user in to firebase with the given data.
     * @param username Used for the first time sign in
     * @param email
     * @param password
     * @param firstTimeSignIn Whether the user is signing in for the first time. If true, also saves the user data to the database.
     */
    public static void signUserInWithEmailAndPassword(final String username, final String email, final String password, boolean firstTimeSignIn) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    System.out.println("[Neuron.AuthenticationManager.signUserInWithEmailAndPassword]: Sign in for email " + email + " SUCCESSFUL1");

                    FirestoreManager.saveUserData(username, email, AuthenticationManager.getCurrentUser().getUid());
                } else {
                    System.out.println("[Neuron.AuthenticationManager.signUserInWithEmailAndPassword]: SIGN IN FAILED! ERROR: " + task.getException());
                }
            }
        });
    }

    /**
     * Gets the currently authenticated user
     * @return
     */
    public static FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }
}
