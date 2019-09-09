package neuron.android.com.neuron.termination;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;

import neuron.android.com.neuron.core.SignupMethod;
import neuron.android.com.neuron.registration.facebookRegistration.FacebookSignInStateManager;
import neuron.android.com.neuron.signin.SignInUtilities;

/**
 * This class contains a snapshot of all of the data if the SSUA is stopped (causing the current data to be deleted from firebase) and then resumed again
 */
public class SecondarySignupTerminatedSnapshot {
    GoogleSignInAccount googleSignInAccount; //for FirebaseAuthWithGoogle
    AccessToken facebookAccessToken; //for handleFacebookAccessToken()

    public SecondarySignupTerminatedSnapshot(GoogleSignInAccount googleSignInAccount, AccessToken accessToken) {
        this.googleSignInAccount = googleSignInAccount;
        this.facebookAccessToken = accessToken;
    }

    public void restore() {
        SignupMethod method = determineSignUpMethod();

        switch(method) {
            case GOOGLE:
                SignInUtilities.firebaseAuthWithGoogle(googleSignInAccount);
                break;
            case FACEBOOK:
                SignInUtilities.handleFacebookAccessToken(facebookAccessToken);
        }
    }

    /**
     * Determines the method of signup (SignupMethod.GOOGLE or SignupMethod.FACEBOOK) to correctly restore the data
     */
    private SignupMethod determineSignUpMethod() {
        if(googleSignInAccount!=null) {
            System.out.println("[Neuuron.SSTS.determineSignUpMethod]: GOOGLE signup");
            return SignupMethod.GOOGLE;
        } else {
            System.out.println("[Neuuron.SSTS.determineSignUpMethod]: FACEBOOK signup");
            return SignupMethod.FACEBOOK;
        }
    }
}
