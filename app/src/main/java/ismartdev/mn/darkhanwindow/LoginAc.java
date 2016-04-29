package ismartdev.mn.darkhanwindow;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import ismartdev.mn.darkhanwindow.text.Regular;
import ismartdev.mn.darkhanwindow.util.Services;
import ismartdev.mn.darkhanwindow.util.Utils;

public class LoginAc extends Activity implements View.OnClickListener, View.OnTouchListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    CallbackManager callbackManager;
    private ImageView btn_fb_login;
    private ImageView btn_plus_login;
    private ImageView show_pass_btn;
    private EditText password_edit;
    private EditText username_edit;
    private Button login_btn;
    private Regular register_btn;
    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private static final int PROFILE_PIC_SIZE = 400;
    private ConnectionResult mConnectionResult;
    private boolean mIntentInProgress;

    private boolean mSignInClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        password_edit = (EditText) findViewById(R.id.password);
        username_edit = (EditText) findViewById(R.id.username);
        btn_fb_login = (ImageView) findViewById(R.id.fb_login_btn);
        btn_plus_login = (ImageView) findViewById(R.id.plus_login_btn);
        show_pass_btn = (ImageView) findViewById(R.id.show_pass_btn);
        show_pass_btn.setOnTouchListener(this);
        btn_fb_login.setOnClickListener(this);
        btn_plus_login.setOnClickListener(this);
        login_btn = (Button) findViewById(R.id.login_btn);
        login_btn.setOnClickListener(this);
        register_btn = (Regular) findViewById(R.id.create_acc_btn);
        register_btn.setOnClickListener(this);
        // Initializing google plus api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
    }

    private void initFacebook() {
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Login");
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        Log.i("fb  ", response.getJSONObject().toString());
                                        try {

                                            if (response != null) {
                                                JSONObject user = response.getJSONObject();
                                                Services.register(LoginAc.this, user.getInt("id") + "", user.getString("name"),
                                                        user.getString("email"), "facebook", user.getJSONObject("picture").getJSONObject("data").getString("url"));

                                            } else {
                                                Toast.makeText(LoginAc.this, R.string.error_login_social, Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            Toast.makeText(LoginAc.this, R.string.error_login_social, Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        }

                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender, picture.type(large)");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginAc.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginAc.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                mSignInClicked = false;
            }
            Log.e("resultCode", resultCode + "");
            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btn_plus_login) {
            if (!mGoogleApiClient.isConnecting()) {
                mSignInClicked = true;
                resolveSignInError();
            }


        }
        if (v == btn_fb_login) {
            initFacebook();
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        }
        if (v == login_btn) {
            if (username_edit.getText().toString().length() > 5 && password_edit.getText().toString().length() > 3) {
                if (Utils.isValidEmail(username_edit.getText()))
                    Services.login(LoginAc.this, username_edit.getText().toString(), password_edit.getText().toString());
                else
                    Toast.makeText(LoginAc.this, R.string.error_invalid_email, Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(LoginAc.this, R.string.fill_the_fields, Toast.LENGTH_SHORT).show();

        }
        if (v == register_btn) {
//            startActivity(new Intent(LoginAc.this, CreateAccAc.class));
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            password_edit.setInputType(InputType.TYPE_CLASS_TEXT);

            password_edit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            password_edit.setSelection(password_edit.getText().length());
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            password_edit.setInputType(
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            password_edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
            password_edit.setSelection(password_edit.getText().length());

            return true;
        }
        return false;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.e("connected", "plus");
        mSignInClicked = false;
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();

                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);


                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;

                Services.register(LoginAc.this, currentPerson.getId() + "", personName, email, "google", personPhotoUrl + "");
            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

}
