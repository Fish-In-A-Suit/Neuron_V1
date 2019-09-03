package neuron.android.com.neuron.core;

import androidx.annotation.NonNull;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import neuron.android.com.neuron.authentication.AuthenticationManager;
import neuron.android.com.neuron.database.FirestoreManager;
import neuron.android.com.neuron.registration.facebookRegistration.FacebookSignInStateManager;
import neuron.android.com.neuron.registration.googleRegistration.GoogleSignInStateManager;

/**
 * The user can decide to quit the application during the process of signup. Therefore, all of the activites which give the user
 * the option to sign up should implement this interface and call it's tryToDestroySignUpClient() in their onStop() methods
 */
public interface ProtectSignupTermination {
    static void tryToDestroySignUpClient(SignupType type) {
        if(Constants.isSecondarySignUpInProcess && type == SignupType.SECONDARY) {
            System.out.println("[Neuron.PST.tryToDestroySignUpClient]: here");

            //todo: store all of the contents in a snapshot so you are able to retrieve them later
            SecondarySignupTerminatedSnapshot ssts = new SecondarySignupTerminatedSnapshot(GoogleSignInStateManager.getGoogleSignInAccount(), FacebookSignInStateManager.getAccessToken());
            TerminatedSnapshotManager.setSecondarySignupTerminatedSnapshot(ssts);

            //secondary sign up is in process, being called from the secondary sign up activity --> destroy
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if(user!=null) {
                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        System.out.println("[Neuron.PST.tryToDestroySignUpClient]: User " + user.getDisplayName() + " successfully deleted from firebase auth!");

                        //here, delete the user from firestore database
                        FirestoreManager.deleteUser(user.getUid());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("[Neuron.PST.tryToDestroySignUpClient]: ERRROR deleting user " + user.getDisplayName() + " from firebase auth!");
                    }
                });
            } else {
                System.out.println("[Neuron.PST.tryToDestroySignUpClient]: ERROR deleting firebase user. User is not signed in/is null!");
            }

            //sign out of fb
            LoginManager.getInstance().logOut();

            //sign out of google
            AuthenticationManager.getAuth().signOut();
            if(GoogleSignInStateManager.getGoogleSignInClient()!=null) {
                GoogleSignInStateManager.getGoogleSignInClient().signOut();
            }
        }
    }
}
