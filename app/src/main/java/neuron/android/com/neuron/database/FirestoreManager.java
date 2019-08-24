package neuron.android.com.neuron.database;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import neuron.android.com.neuron.core.Constants;

/**
 * Contains method to send stuff to the cloud firestore database
 */
public class FirestoreManager {
    private static FirebaseFirestore db;

    /**
     * This method should be called at the start of the application, else this class won't work
     */
    public static void init() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * @return Instance of FirebaseFirestore
     */
    public static FirebaseFirestore getInstance() {
        return db;
    }

    public static CollectionReference getCollectionReference(String collectionReference) {
        switch(collectionReference) {
            case Constants.FIRESTORE_COLLECTION_USED_USERNAMES:
                return db.collection(Constants.FIRESTORE_COLLECTION_USED_USERNAMES);
            case Constants.FIRESTORE_COLLECTION_USED_EMAILS:
                return db.collection(Constants.FIRESTORE_COLLECTION_USED_EMAILS);
            case Constants.FIRESTORE_COLLECTION_USER_DATA:
                return db.collection(Constants.FIRESTORE_COLLECTION_USER_DATA);
        }

        return null;
    }

    /**
     * Saves the user data to FireStore in the following pattern:
     *      uid: userUid
     *          username: currentUsername
     *          email: currentEmail
     *
     *  Also adds the user's username to used_usernames and email to used_emails
     *
     * @param username
     * @param email
     */
    public static void saveUserData(final String username, String email, String userUid) {
        System.out.println("[Neuron.FirestoreManager.saveUserData]: Uid for username '" + username + "' is: " + userUid);

        Map<String, Object> userData = new HashMap<>();
        userData.put(Constants.FIRESTORE_USERNAME_TAG, username);
        userData.put(Constants.FIRESTORE_EMAIL_TAG, email);

        getCollectionReference(Constants.FIRESTORE_COLLECTION_USER_DATA).document(userUid).set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                System.out.println("[Neuron.FirestoreManager.saveUserData]: User data for username " + username + " SUCCESSFUL!");
            }
        });

        addUsedUsername(username);
        addUsedEmail(email);
    }

    public static void saveUserData(DatabaseUser user) {
        saveUserData(user.getUsername(), user.getEmail(), user.getId());
    }

    /**
     * Adds a new used username to the firestore used_usernames collection
     * @param username
     */
    public static void addUsedUsername(final String username) {
        Map<String, Object> usedUsername = new HashMap<>();
        usedUsername.put(Constants.FIRESTORE_USERNAME_TAG, username);

        getCollectionReference(Constants.FIRESTORE_COLLECTION_USED_USERNAMES).add(usedUsername).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                System.out.println("[Neuron.FirestoreManager.addUsedUsername]: Adding " + username + " to used_username is SUCCESSFUL!");
            }
        });
    }

    /**
     * Adds a new used email to the firestore used_emails collection
     * @param email
     */
    public static void addUsedEmail(final String email) {
        Map<String, Object> usedEmail = new HashMap<>();
        usedEmail.put(Constants.FIRESTORE_EMAIL_TAG, email);

        getCollectionReference(Constants.FIRESTORE_COLLECTION_USED_EMAILS).add(usedEmail).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                System.out.println("[Neuron.FirestoreManager.addUsedEmail]: Adding " + email + " to used emails is SUCCESSFUL!");
            }
        });
    }

    /**
     * Deletes all of the user data specified by the uid
     * @param uid
     */
    public static void deleteUser(final String uid) {
        db.collection(Constants.FIRESTORE_COLLECTION_USER_DATA).document(uid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("[Neuron.FirestoreManager.deleteUser]: User with uid " + uid + " has been deleted successfully.");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("[Neuron.FirestoreManager.deleteUser]: ERROR deleting user with uid " + uid + ". Exception: " + e.getMessage());
            }
        });
    }
}
