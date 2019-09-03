package neuron.android.com.neuron.registration.facebookRegistration;

import com.facebook.AccessToken;

public class FacebookSignInStateManager {
    private static AccessToken accessToken;

    public static void setAccessToken(AccessToken at) {
        accessToken = at;
    }

    public static AccessToken getAccessToken() {
        return accessToken;
    }
}
