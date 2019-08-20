package neuron.android.com.neuron.registration.googleRegistration;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;

/**
 * This class is used to query the current google sign in client so the user can be signed out of the app
 */
public class GoogleSignInStateManager {
    private static GoogleSignInClient googleSignInClient;

    public static void setGoogleSignInClient(GoogleSignInClient gsic) {
        googleSignInClient = gsic;
    }

    public static GoogleSignInClient getGoogleSignInClient() {
        return googleSignInClient;
    }
}
