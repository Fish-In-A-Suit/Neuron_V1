package neuron.android.com.neuron;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import mehdi.sakout.fancybuttons.FancyButton;
import neuron.android.com.neuron.authentication.AuthenticationManager;
import neuron.android.com.neuron.core.Constants;
import neuron.android.com.neuron.core.NeuronActivity;
import neuron.android.com.neuron.core.ProtectSignupTermination;
import neuron.android.com.neuron.core.SignupType;
import neuron.android.com.neuron.database.DatabaseUser;
import neuron.android.com.neuron.database.FirestoreManager;
import neuron.android.com.neuron.registration.facebookRegistration.FacebookSignInStateManager;
import neuron.android.com.neuron.registration.googleRegistration.GoogleSignInStateManager;
import neuron.android.com.neuron.signin.SignInUtilities;
import neuron.android.com.neuron.tools.ActivityTools;
import neuron.android.com.neuron.tools.AnimationTools;
import neuron.android.com.neuron.tools.StringUtilities;
import neuron.android.com.neuron.tools.ViewUtilities;

public class LoginActivity extends AppCompatActivity implements NeuronActivity, ProtectSignupTermination {
    private Context activityContext;

    private boolean isEmailFieldEmpty;
    private boolean isPasswordFieldEmpty;

    private EditText emailField;
    private EditText passwordField;

    private Button signInButton;

    private FancyButton fancyFacebookSignInButton;
    private LoginButton trueFacebookSignInButton;
    private SignInButton googleSignInButton;

    private CallbackManager facebookCallbackManager;

    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        activityContext = this;

        initializeViews();

        isEmailFieldEmpty = true;
        isPasswordFieldEmpty = true;

        FirestoreManager.init();
        AnimationTools.init();
        AuthenticationManager.init();

        //configure google sign in request
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        setupEmailPassListeners();
    }

    public void initializeViews() {
        emailField = findViewById(R.id.login_email_edit_text);
        passwordField = findViewById(R.id.login_password_edit_text);
        signInButton = findViewById(R.id.login_sign_in_button);

        fancyFacebookSignInButton = findViewById(R.id.login_facebook_sign_in_button);
        trueFacebookSignInButton = findViewById(R.id.login_true_facebook_sign_in_button);
        googleSignInButton = findViewById(R.id.login_google_sign_in_button);

        setupGoogleButton();
        setupFacebookButton();
    }

    @Override
    protected void onStart() {
        super.onStart();

        /*Logging app activations enables almost all other functionality.
         By logging an activation event, you can observe how frequently users activate your app,
         how much time they spend using it, and view other demographic information through Facebook Analytics for Apps.
        */
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        /*this prints the key hash
        //todo: remove this later!
        try {
            PackageInfo info = getPackageManager().getPackageInfo("neuron.android.com.neuron", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("KeyHash:", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("KeyHash:", e.toString());
        }

        try {
            PackageInfo info = getPackageManager().getPackageInfo("neuron.android.com.neuron", PackageManager.GET_SIGNATURES);

            for(Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(signature.toByteArray());
                String hash_key_hex = StringUtilities.convertToHex(md.digest());
                System.out.println("[Neuron.LoginActivity.onCreate]: sha256=" + hash_key_hex);
            }
        } catch(PackageManager.NameNotFoundException e) {
            System.err.println("[Neuron.LoginActivity.onCreate]: " + e.toString());
        } catch(NoSuchAlgorithmException e) {
            System.err.println("[Neuron.LoginActivity.onCreate]: " + e.toString());
        }
        */

        //check for an existing signed-in user for google
        //todo: ENABLE THIS LATER!!!
        /*
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(googleSignInAccount==null) {
            ActivityTools.startNewActivity(this, MainActivity.class);
        }
        */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.request_code_google_sign_in) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                //google sign in was successful, authenticate with fireabse
                GoogleSignInAccount account = task.getResult(ApiException.class);
                GoogleSignInStateManager.setGoogleSignInAccount(account);
                SignInUtilities.firebaseAuthWithGoogle(account, this, SecondarySignUpActivity.class);
            } catch (ApiException e) {
                //googne sign in failed
                System.out.println("[Neuron.RegisterActvitiy.onActivityResult]: ERROR! Google Sign In failed! " + task.getException());
            }
        }

        // Pass the activity result back to the Facebook SDK
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();

        System.out.println("[Neuron.LoginActivity.onStop]: here");
        ProtectSignupTermination.tryToDestroySignUpClient(SignupType.PRIMARY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //todo: remove this later
        System.out.println("[Neuron.LoginActivity.onDestroy]: Logging out of facebook");
        LoginManager.getInstance().logOut();
    }

    /**
     * This method gets called when "Register Here" is clicked. This method redirects the user to RegisterActivity.
     * @param v
     */
    public void onClick_registerHere(View v) {
        ActivityTools.startNewActivity(this, RegisterActivity.class);
    }

    /**
     * This method gets called when the sign in button is clicked. Attempts to sign the user in.
     * @param v
     */
    public void onClick_signIn(View v) {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        SignInUtilities.signInWithEmailPass(this, email, password);
    }

    /**
     * Sets up the listeners on email and password fields to check if they aren't empty and to try to enable the sign in button
     */
    private void setupEmailPassListeners() {
        emailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()!=0) {
                    isEmailFieldEmpty = false;
                    tryToEnableSignInButton();
                } else {
                    isEmailFieldEmpty = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //do nothing
            }
        });

        passwordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()!=0) {
                    isPasswordFieldEmpty = false;
                    tryToEnableSignInButton();
                } else {
                    isPasswordFieldEmpty = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //do nothing
            }
        });
    }

    private void tryToEnableSignInButton() {
        if(isEmailFieldEmpty == false && isPasswordFieldEmpty == false) {
            signInButton.setEnabled(true);
        } else {
            signInButton.setEnabled(false);
        }
    }

    public void onClickSignInWithFacebook(View view) {
        trueFacebookSignInButton.performClick();
    }

    /**
     * Configures the google sign in button in login activity
     */
    private void setupGoogleButton() {
        //set the text and make the google button's width equal to the width of the facebook button
        ViewUtilities.setGoogleButtonText(googleSignInButton, getResources().getString(R.string.google));

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInUtilities.signInWithGoogle(LoginActivity.this.getBaseContext(), LoginActivity.this);
            }
        });
    }

    /**
     * Configures the facebook sign in button in login activity
     */
    private void setupFacebookButton() {
        facebookCallbackManager = CallbackManager.Factory.create();
        trueFacebookSignInButton.setReadPermissions("email", "public_profile");
        trueFacebookSignInButton.registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("[Neuron.LoginActivity.setupFacebookButton]: facebook:onSuccess: " + loginResult);
                AccessToken token = loginResult.getAccessToken();
                FacebookSignInStateManager.setAccessToken(token);
                handleFacebookAccessToken(token);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        System.out.println("[Neuron.LoginActiivty.handleFacebookAccessToken]: " + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        AuthenticationManager.getAuth().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    //sign in successful
                    System.out.println("[Neuron.LoginAActivity.handleFacebookAccessToken]: Sign in with facebook successful!");

                    if(task.getResult().getAdditionalUserInfo().isNewUser()) {
                        FirebaseUser user = AuthenticationManager.getCurrentUser();
                        DatabaseUser incompleteDatabaseUser = new DatabaseUser("", user.getEmail(), "", user.getUid());
                        String name = StringUtilities.choose(1, user.getDisplayName());
                        ActivityTools.startNewActivity(activityContext, SecondarySignUpActivity.class, Constants.PARCELABLE_KEY_INCOMPLETE_DATABASE_USER, incompleteDatabaseUser, name);
                    } else {
                        ActivityTools.startNewActivity(activityContext, MainActivity.class);
                    }
                } else {
                    System.out.println("[Neuron.LoginActivity.handleFacebookAccessToken]: Sign in with facebook FAILED! " + task.getException().getMessage());
                    //todo: respond
                }
            }
        });
    }
}
