package neuron.android.com.neuron;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import neuron.android.com.neuron.core.Constants;
import neuron.android.com.neuron.database.DatabaseUser;

public class AfterGoogleSignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_google_sign_up);

        DatabaseUser incompleteDatabaseUser = getIntent().getParcelableExtra(Constants.PARCELABLE_KEY_INCOMPLETE_DATABASE_USER);
        System.out.println("[Neuron.AfterGoogleSignUpActvity.onCreate]: Displaying incomplete db user: " + incompleteDatabaseUser.toString());
    }
}
