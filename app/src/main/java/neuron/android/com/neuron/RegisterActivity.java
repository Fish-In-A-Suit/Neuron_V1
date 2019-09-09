package neuron.android.com.neuron;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import mehdi.sakout.fancybuttons.FancyButton;
import neuron.android.com.neuron.core.Constants;
import neuron.android.com.neuron.core.NeuronActivity;
import neuron.android.com.neuron.termination.ProtectSignupTermination;
import neuron.android.com.neuron.core.SignupType;
import neuron.android.com.neuron.registration.defaultRegistration.RegistrationManager;
import neuron.android.com.neuron.registration.facebookRegistration.FacebookSignInStateManager;
import neuron.android.com.neuron.registration.googleRegistration.GoogleSignInStateManager;
import neuron.android.com.neuron.signin.SignInUtilities;
import neuron.android.com.neuron.tools.ViewUtilities;

public class RegisterActivity extends AppCompatActivity implements NeuronActivity, ProtectSignupTermination {

    private Context activityContext;

    private RegistrationManager registrationManager;

    private EditText editText_username;
    private EditText editText_email;
    private EditText editText_password;
    private EditText editText_repeatPassword;

    //error views --> display exclamation marks
    private ImageView register_username_error_view;
    private ImageView register_email_error_view;
    private ImageView register_password_error_view;
    private ImageView register_repeat_password_error_view;

    //error report views --> display error messages
    private TextView register_username_error_report_view;
    private TextView register_email_error_report_view;
    private TextView register_password_error_report_view;
    private TextView register_repeat_password_error_report_view;

    private Button signUpButton;
    private SignInButton googleSignInButton;

    private LinearLayout defaultRegistrationRootView;

    private GoogleSignInClient googleSignInClient;

    private CallbackManager facebookCallbackManager;
    private LinearLayout facebookLoginButton;
    private LoginButton trueFbLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        activityContext = this;

        initializeViews();

        registrationManager = new RegistrationManager(this, editText_username, editText_email, editText_password, editText_repeatPassword,
                register_username_error_view, register_email_error_view, register_password_error_view, register_repeat_password_error_view,
                register_username_error_report_view, register_email_error_report_view, register_password_error_report_view, register_repeat_password_error_report_view,
                signUpButton, defaultRegistrationRootView);

        //configure google sign in request
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        initializeListeners();

        setupFacebookSignup();

        configureGoogleButton();

        Constants.isPrimarySignUpInProcess = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

        System.out.println("[Neuron.RegisterActivity.onStop]: here");
        ProtectSignupTermination.tryToDestroySignUpClient(SignupType.PRIMARY);
    }

    public void onClick_default_signUp(View v) {

    }

    public void initializeViews() {
        editText_username = (EditText) findViewById(R.id.register_username_edit_text);
        editText_email = (EditText) findViewById(R.id.register_email_edit_text);
        editText_password = (EditText) findViewById(R.id.register_password_edit_text);
        editText_repeatPassword = (EditText) findViewById(R.id.register_repeat_password_edit_text);

        register_username_error_view = (ImageView) findViewById(R.id.register_username_error_view);
        register_email_error_view = (ImageView) findViewById(R.id.register_email_error_view);
        register_password_error_view = (ImageView) findViewById(R.id.register_password_error_view);
        register_repeat_password_error_view = (ImageView) findViewById(R.id.register_repeat_password_error_view);

        register_username_error_report_view = (TextView) findViewById(R.id.register_username_error_report_view);
        register_email_error_report_view = (TextView) findViewById(R.id.register_email_error_report_view);
        register_password_error_report_view = (TextView) findViewById(R.id.register_password_error_report_view);
        register_repeat_password_error_report_view = (TextView) findViewById(R.id.register_repeat_password_error_report_view);

        signUpButton = (Button) findViewById(R.id.register_default_sign_up_button);
        googleSignInButton = (SignInButton) findViewById(R.id.register_google_sign_up_button);
        //DONE: set google sign in button text
        ViewUtilities.setGoogleButtonText(googleSignInButton, getResources().getString(R.string.sign_in_with_google));

        defaultRegistrationRootView = (LinearLayout) findViewById(R.id.register_default_sign_up_linear_layout);

        facebookLoginButton = (LinearLayout) findViewById(R.id.register_facebook_signup_button);
        trueFbLoginButton = (LoginButton) findViewById(R.id.register_true_facebook_login_button);
    }

    /**
     * This method sets up the needed listeners to particular buttons
     */
    private void initializeListeners() {
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInUtilities.signInWithGoogle(RegisterActivity.this.getBaseContext(), RegisterActivity.this);
            }
        });

        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trueFbLoginButton.performClick();
            }
        });
    }

    /**
     * Configures the facebook login button to log users in with facebook
     */
    private void setupFacebookSignup() {
        facebookCallbackManager = CallbackManager.Factory.create();

        trueFbLoginButton.setReadPermissions("email", "public_profile", "user_friends");
        //facebookLoginButton.setCustomTextFont(R.font.roboto_medium);
        //ViewUtilities.setFancyButtonTextMargin(facebookLoginButton, 0, 48, 0, 0);
        //ViewUtilities.test(facebookLoginButton);

        trueFbLoginButton.registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("[Neuron.RegisterActivity.setupFacebookSignup]: Facebook login successful: " + loginResult);
                AccessToken token = loginResult.getAccessToken();
                FacebookSignInStateManager.setAccessToken(token);
                SignInUtilities.handleFacebookAccessToken(token, activityContext, SecondarySignUpActivity.class);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                System.out.println("[Neuron.RegisterActivity.setupFacebookSignup]: ERROR while facebook login: " + error);
                //todo: respond to error
            }
        });
    }

    private void configureGoogleButton() {
        ViewUtilities.setGoogleButtonText(googleSignInButton, getResources().getString(R.string.signup_with_google));
    }
}
