package neuron.android.com.neuron.database;

import android.os.Parcel;
import android.os.Parcelable;

public class DatabaseUser implements Parcelable {
    private String username;
    private String email;
    private String password;
    private String id;

    public DatabaseUser() {

    }

    public DatabaseUser(String username, String email, String password, String id) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.id = id;
    }

    protected DatabaseUser(Parcel in) {
        username = in.readString();
        email = in.readString();
        password = in.readString();
        id = in.readString();
    }

    public static final Creator<DatabaseUser> CREATOR = new Creator<DatabaseUser>() {
        @Override
        public DatabaseUser createFromParcel(Parcel in) {
            return new DatabaseUser(in);
        }

        @Override
        public DatabaseUser[] newArray(int size) {
            return new DatabaseUser[size];
        }
    };

    public void sendDataToDatabase() {
        //todo: add email-password based authentication here


        FirestoreManager.saveUserData(username, password, id);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "username = " + username + " | email = " + email + " | password = " + password + " | id = " + id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * When we launch the destination Activity, our object will be written to a Parcel using this method
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(id);
    }
}