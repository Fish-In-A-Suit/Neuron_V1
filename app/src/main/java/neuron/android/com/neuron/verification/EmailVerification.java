package neuron.android.com.neuron.verification;

import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailVerification {
    public static void sendVerificationEmail() {
        /*
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        String url = "https://www.neuron.com/verify?uid" + user.getUid();

        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                .setUrl(url)
                .setHandleCodeInApp(false) //no need to handle email verification in app
                .setAndroidPackageName("neuron.android.com.neuron", true, "21")
                .build();
                */

        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
    }
}
