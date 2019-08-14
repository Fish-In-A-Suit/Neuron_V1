package neuron.android.com.neuron.registration.googleRegistration;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * This class handles the currently active GoogleApiClient used during the sign-up process for google sign up.
 */
public class GoogleSignUpAccountManager {
    private static GoogleApiClient googleApiClient;
    private static FragmentActivity fragmentActivity;

    public static void setCurrentUser(GoogleApiClient gac) {
        googleApiClient = gac;
    }

    public static void setFragmentActivity(FragmentActivity fa) {
        fragmentActivity = fa;
    }

    /**
     Disconnects the client and stops automatic lifecycle management. Use this before creating a new client (which might be necessary when switching accounts, changing the set of used APIs etc.).
     */
    public static void disconnectAndFreeMemory(FragmentActivity fa) {
        System.out.println("[Neuron.GoogleSignUpAccountManager.disconnectAndFreeMemory]: Disconnecting and stopping management of googleApiClient " + googleApiClient);
        googleApiClient.stopAutoManage(fa);
        googleApiClient.disconnect();
    }

    public static void disconnectAndFreeMemory() {
        if(null != fragmentActivity) {
            System.out.println("[Neuron.GoogleSignUpAccountManager.disconnectAndFreeMemory]: Disconnecting and stopping management of googleApiClient " + googleApiClient);
            googleApiClient.stopAutoManage(fragmentActivity);
            googleApiClient.disconnect();
        } else {
            System.out.println("[Neuron.GoogleSignUpAccountManager.disconnectAndFreeMemory]: Cannot disconnect the GoogleApiClient. The fragment activity managing the client's lifecycle is null!");
        }
    }

    public static GoogleApiClient getCurrentUser() {
        return googleApiClient;
    }

}
