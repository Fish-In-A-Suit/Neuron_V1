package neuron.android.com.neuron.core;

public class Constants {
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MIN_PASSWORD_LENGTH = 6;

    public static final String FIRESTORE_COLLECTION_USED_USERNAMES = "used_usernames";
    public static final String FIRESTORE_COLLECTION_USED_EMAILS = "used_emails";
    public static final String FIRESTORE_COLLECTION_USER_DATA = "user_data";

    public static final String FIRESTORE_USERNAME_TAG = "username"; //each element is stored under "username" ... used when getting usernames back from the database
    public static final String FIRESTORE_EMAIL_TAG = "email";


    public static final int id_register_loading_bar = 111;
    public static final int request_code_google_sign_in = 100;

    public static final String PARCELABLE_KEY_INCOMPLETE_DATABASE_USER = "incomplete_db_user";
    public static final String PARCELABLE_KEY_NAME = "name";
}
