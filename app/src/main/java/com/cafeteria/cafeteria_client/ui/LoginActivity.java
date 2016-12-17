package com.cafeteria.cafeteria_client.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.Customer;
import com.cafeteria.cafeteria_client.utils.ApplicationConstant;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import com.onesignal.OneSignal;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private SharedPreferences sharedPreferences;
    private EditText etMail;
    private EditText etPassword;
    private TextView tvForgotPassword;
    private String emailTxt;
    private String passwordTxt;
    private CallbackManager facebookCallbackManager;
    private LoginButton facebookLoginBtn;
    private ProfileTracker profileTracker;
    private Customer customer = new Customer();
    private static final int RC_SIGN_IN = 9001;
    private SignInButton googleLoginBtn;
    private static final String TAG = "GoogleLogin";
    public static GoogleApiClient mGoogleApiClient;


    /**
     *  The Id of tbe user that this class logs in - PK for primary key
     *  this is the user id in the database
     */
    private int userPKId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // With this lines the events will show in the developer
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        setContentView(R.layout.activity_login);

        tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                LayoutInflater layout = getLayoutInflater();
                final EditText input = new EditText(LoginActivity.this);
                input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialogBuilder.setView(input);
                alertDialogBuilder
                        .setTitle(getString(R.string.dialog_forgot_password_title))
                        .setMessage(getString(R.string.dialog_forgot_password_message))
                        .setPositiveButton(getResources().getString(R.string.dialog_forgot_password_button),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String email = input.getText().toString();
                                        new ForgotPasswordTask(email).execute();
                                        dialogInterface.dismiss();

                                    }
                                }).create().show();
            }
        });

        TextView loginAppTitle = (TextView) findViewById(R.id.loginAppTitle);
        Typeface type = Typeface.DEFAULT.createFromAsset(getAssets(),"fonts/PatuaOne-Regular.ttf");
        loginAppTitle.setTypeface(type);

        facebookCallbackManager = CallbackManager.Factory.create();
        facebookLoginBtn = (LoginButton) findViewById(R.id.facebookLoginBtn);
        facebookLoginBtn.setReadPermissions("email");

        // get email string from shared preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String customerJSON = sharedPreferences.getString("customer", "");
        // if the email found - it's not the first time opening  this app
        // automatically redirect to home screen
        if (customerJSON != null && !customerJSON.equals("")) {
            // before the redirect... get the userId and execute the task that checks if this user
            // has this device token in the db
            setTokenAndStartHomeActivity();
        }

        facebookLoginBtn.registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                // if facebook profile changes
                if (Profile.getCurrentProfile() == null) {
                    profileTracker = new ProfileTracker() {
                        Gson gson = new Gson();
                        SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = mySPrefs.edit();

                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                            //delete old details
                            if (sharedPreferences.getString("customer", "") != null) {
                                editor.remove("customer");
                                editor.apply();
                            }

                            customer = new Customer();
                            customer.setLastName(currentProfile.getLastName());
                            customer.setFirstName(currentProfile.getFirstName());
                            //update in db

                        }
                    };
                } else {
                    customer = new Customer();
                    customer.setFirstName(Profile.getCurrentProfile().getFirstName());
                    customer.setLastName(Profile.getCurrentProfile().getLastName());
                }

                Log.d("FACEBOOK", "login facebook succesfull");
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    // After the grap aync task will completed will have the extra infomration (email)
                    // first/last name, id, profile pic etc.. is public already
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("FACEBOOK", response.toString());
                        // Get facebook data from login
                        Bundle facebookData = getFacebookData(object);
                        customer.setEmail(facebookData.getString("email"));
                        // for now i assume that the birthday is the password
                        customer.setPassword(facebookData.getString("birthday"));
                        facebookData.getString("gender");

                        new ValidateOrSignUpTask().execute();
                    }
                });
                Bundle parameters = new Bundle();
                // the params we ask from facebook
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                // user canceled
            }

            @Override
            public void onError(FacebookException exception) {
            }
        });

        etPassword = (EditText) findViewById(R.id.etPassword);
        etMail = (EditText) findViewById(R.id.etMail);

        TextView signUpLinkTv = (TextView) findViewById(R.id.tvSignUpLink);
        signUpLinkTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        });

        Button loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoginAndValidationTask().execute();
            }
        });

        // Google Login

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        googleLoginBtn = (SignInButton) findViewById(R.id.googleLoginBtn);
        // Set the dimensions of the sign-in button.
        googleLoginBtn.setSize(SignInButton.SIZE_WIDE);
        googleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

//        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
//        if (opr.isDone()) {
//            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
//            // and the GoogleSignInResult will be available instantly.
//            Log.d(TAG, "Got cached sign-in");
//            GoogleSignInResult result = opr.get();
//            handleSignInResult(result);
//        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.

//            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(GoogleSignInResult googleSignInResult) {
//                    handleSignInResult(googleSignInResult);
//                }
//            });
//        }
    }

    private Bundle getFacebookData(JSONObject object) {
        Bundle bundle = new Bundle();
        String id = null;
        try {
            id = object.getString("id");


            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bundle;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from
        //   GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }else {

            facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            customer.setEmail(acct.getEmail());
            customer.setFirstName(acct.getGivenName());
            customer.setLastName(acct.getFamilyName());
            customer.setPassword("123");

            new GoogleValidateOrSignUpTask().execute();

        } else {
            Log.d(TAG,"Couldn't sign in");
            // Signed out, show unauthenticated UI.

        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private class LoginAndValidationTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String response) {
            if (response != null && !response.equals("null")) {
                Log.e("DEBUG",response);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                Customer toSave = gson.fromJson(response, Customer.class);

                String customerJSON = gson.toJson(toSave);
                editor.putString("customer", customerJSON);
                editor.apply();
                setTokenAndStartHomeActivity();

            } else {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_error), Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder response;
            try {
                URL url = new URL(ApplicationConstant.USER_VALIDATION_URL + "?email=" + emailTxt + "&pass=" + passwordTxt);
                response = new StringBuilder();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                BufferedReader input = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));

                String line;
                while ((line = input.readLine()) != null) {
                    response.append(line + "\n");
                }

                input.close();

                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            String responseString = response.toString().trim();
            Log.e("DEBUG","user response : "+ responseString);
            return responseString;
        }

        @Override
        protected void onPreExecute() {
            emailTxt = etMail.getText().toString().trim();
            passwordTxt = etPassword.getText().toString().trim();
        }
    }

    // FACEBOOK LOGIN TASK
    private class ValidateOrSignUpTask extends AsyncTask<Void, Void, Boolean> {
        String emailTxt;
        String passwordTxt;
        boolean success = false;

        public ValidateOrSignUpTask(){
            emailTxt = customer.getEmail();
            passwordTxt = customer.getPassword();
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Boolean result = false;

            Log.d("FACEBOOK","Log in validation in process for.. " + emailTxt + " " + passwordTxt);

            StringBuilder response;
            try {
                URL url = new URL(ApplicationConstant.USER_VALIDATION_URL + "?email=" + emailTxt + "&pass=" + passwordTxt);
                response = new StringBuilder();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                BufferedReader input = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));

                String line;
                while ((line = input.readLine()) != null) {
                    response.append(line + "\n");
                }

                input.close();

                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            String responseString = response.toString().trim();
            Log.e("FACEBOOK","user response : "+ responseString);

            Log.d("FACEBOOK","after validation result is: " +response );

            if (!response.toString().trim().equals("null")) {
                success = true;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("customer", response.toString());
                editor.apply();
                return false;
            } else{
                Log.d("FACEBOOK","Sign up process starting..");

                // Request - send the customer as json to the server for insertion
                Gson gson = new Gson();
                String jsonUser = gson.toJson(customer, Customer.class);
                URL url = null;
                try {
                    url = new URL(ApplicationConstant.USER_FACEBOOK_REGISTRATION_URL);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    con.setRequestProperty("Content-Type", "text/plain");
                    con.setRequestProperty("Accept", "text/plain");
                    con.setRequestMethod("POST");

                    OutputStream os = con.getOutputStream();
                    os.write(jsonUser.getBytes("UTF-8"));
                    os.flush();

                    if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        return null;
                    }

                    // Response
                    response = new StringBuilder();
                    BufferedReader input = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));

                    String line;
                    while ((line = input.readLine()) != null) {
                        response.append(line + "\n");
                    }

                    input.close();

                    con.disconnect();

                    Log.d("FACEBOOK","after sign up result is: " +response );
                    success = true;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("customer", response.toString());
                    editor.apply();

                    if (response.toString().trim().equals("null") || response == null) {
                        result = true;
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result != null && result) {
                Log.d("FACEBOOK","Succesful sign up");

            } else {
                Log.d("FACEBOOK","Client is already exist or signup is failed");
            }
            if (success){
                setTokenAndStartHomeActivity();
            }

        }
    }

    //Google login task
    private class GoogleValidateOrSignUpTask extends AsyncTask<Void, Void, Boolean> {
        boolean success = false;

        @Override
        protected Boolean doInBackground(Void... voids) {
            Boolean result = false;

            Log.d("GOOGLE","Log in validation in process for.. " + customer.getEmail()
                    + " " + customer.getPassword());

            StringBuilder response;
            try {
                URL url = new URL(ApplicationConstant.USER_VALIDATION_URL + "?email=" +
                        customer.getEmail() + "&pass=" + customer.getPassword());
                response = new StringBuilder();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                BufferedReader input = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));

                String line;
                while ((line = input.readLine()) != null) {
                    response.append(line + "\n");
                }

                input.close();

                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            String responseString = response.toString().trim();
            Log.e("GOOGLE","user response : "+ responseString);

            Log.d("GOOGLE","after validation result is: " +response );

            if (!response.toString().trim().equals("null")) {
                success = true;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("customer", response.toString());
                editor.apply();
                return false;
            } else{
                Log.d("GOOGLE","Sign up process starting..");

                // Request - send the customer as json to the server for insertion
                Gson gson = new Gson();
                String jsonUser = gson.toJson(customer, Customer.class);
                URL url = null;
                try {
                    url = new URL(ApplicationConstant.USER_FACEBOOK_REGISTRATION_URL);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    con.setRequestProperty("Content-Type", "text/plain");
                    con.setRequestProperty("Accept", "text/plain");
                    con.setRequestMethod("POST");

                    OutputStream os = con.getOutputStream();
                    os.write(jsonUser.getBytes("UTF-8"));
                    os.flush();

                    if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        return null;
                    }

                    // Response
                    response = new StringBuilder();
                    BufferedReader input = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));

                    String line;
                    while ((line = input.readLine()) != null) {
                        response.append(line + "\n");
                    }

                    input.close();

                    con.disconnect();

                    Log.d("GOOGLE","after sign up result is: " +response );
                    success = true;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("customer", response.toString());
                    editor.apply();

                    if (response.toString().trim().equals("null") || response == null) {
                        result = true;
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result != null && result) {
                Log.d("GOOGLE","Succesful sign up");

            } else {
                Log.d("GOOGLE","Client is already exist or signup is failed");
            }
            if (success){
                setTokenAndStartHomeActivity();
            }

        }
    }


    private class RefreshTokenTask extends AsyncTask<Void,Void,Void> {
        // to compare with the device token
        String userOldToken;
        StringBuilder response;
        URL url;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // http request to get the old token of the current user
                url = new URL(ApplicationConstant.GET_TOKEN+"?user="+userPKId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                Log.e("GET TOKEN",conn.getResponseCode()+"");
                if (conn.getResponseCode() < HttpURLConnection.HTTP_OK) {
                    Log.e("GET TOKEN",conn.getResponseMessage());
                    return null;
                }

                // get the response data
                response = new StringBuilder();
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));

                String line;
                while ((line = input.readLine()) != null) {
                    response.append(line + "\n");
                }
                // and get the token out of it as a clean string
                userOldToken = response.toString().trim();
                Log.e("TOKEN","old token : " + userOldToken);
                conn.disconnect();

                // now comparing this old token with the device token (= 'userId' in the overridden method)
                OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                    @Override
                    public void idsAvailable(String userId, String registrationId) {
                        Log.d("debug", "Device Token:" + userId);
                        // if there is no saved token or token is different from device token
                        if(userOldToken == null || !userOldToken.equals(userId)) {
                            try {
                                // request the server to attach the device token (userId) to the right user (with the id userPKId)
                                url = new URL(ApplicationConstant.SET_TOKEN+"?userId="+userPKId+"&pushId="+userId);
                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                Log.e("SET TOKEN", conn.getResponseCode() + "");
                                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                                    Log.e("SET TOKEN", conn.getResponseMessage());
                                }
                            }catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (registrationId != null)
                            Log.d("debug", "registrationId:" + registrationId);
                    }
                });

                // ******* SEND THE NOTIFICATION ***********
                // trigger the server to send the notification, shouldn't be here but i want to keep the code
//                url = new URL(ApplicationConstant.SEND_NOTI_URL+"?userId="+userPKId);
//                conn = (HttpURLConnection) url.openConnection();
//                Log.e("DEBUG",conn.getResponseCode()+"");
//                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
//                    Log.e("DEBUG",conn.getResponseMessage());
//                    return null;
//                }
//                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (profileTracker != null) {
            profileTracker.stopTracking();
        }
    }

    private void setTokenAndStartHomeActivity(){
        Gson gson = new Gson();
        String customerJSON = sharedPreferences.getString("customer", "");
        customer = gson.fromJson(customerJSON, Customer.class);

        userPKId = customer.getId();
        new RefreshTokenTask().execute();
        finish();
        Intent homeScreen = new Intent(LoginActivity.this, MenuActivity.class);
        startActivity(homeScreen);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.exit_dialog_title))
                .setMessage(getString(R.string.exit_dialog_message))
                .setPositiveButton(getString(R.string.exit_dialog_postive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton(getString(R.string.exit_dialog_negative), null)
                .show();
    }

    public static void googleSignOut() {
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                Log.d("GOOGLE", "cliend is connected");
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                Log.d("GOOGLE", "cliend signed out");
                            }
                        });
            }
        }
    }

    private class ForgotPasswordTask extends AsyncTask<Boolean, Void, Boolean> {
        private String email;
        private boolean result = false;

        public ForgotPasswordTask(String email){
            this.email = email;
        }

        @Override
        protected Boolean doInBackground(Boolean... params) {
            StringBuilder response;
            try {
                URL url = new URL(ApplicationConstant.FORGOT_PASSWORD + "?email=" + email);
                response = new StringBuilder();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                BufferedReader input = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));

                String line;
                while ((line = input.readLine()) != null) {
                    response.append(line + "\n");
                }

                input.close();

                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            String responseString = response.toString().trim();
            Log.e("DEBUG","user response : "+ responseString);
            if (responseString.trim().equals("OK")) {
                result = true;
            }
            return result;
        }


        @Override
        protected void onPostExecute(Boolean response) {
            if (result) {
                Toast.makeText(LoginActivity.this,getString(R.string.dialog_forgot_password_success),Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(LoginActivity.this,getString(R.string.dialog_forgot_password_fail),Toast.LENGTH_LONG).show();
            }

        }

    }

}