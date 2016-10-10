package com.cafeteria.cafeteria_client.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.Customer;
import com.cafeteria.cafeteria_client.utils.ApplicationConstant;
import com.facebook.AccessToken;
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
import com.google.gson.Gson;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static com.cafeteria.cafeteria_client.utils.ApplicationConstant.SERVER_IP;


public class LoginActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private EditText etMail;
    private EditText etPassword;
    private String emailTxt;
    private String passwordTxt;
    private CallbackManager facebookCallbackManager;
    private LoginButton facebookLoginBtn;
    private ProfileTracker profileTracker;

    private final static String SERVER_IP = "192.168.43.231";  // SHIRA IP
    //private final static String SERVER_IP = "192.168.1.11"; // ANAEL IP
    private final static String USER_VALIDATION_URL = "http://"+SERVER_IP+":8080/CafeteriaServer/rest/users/isUserExist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//         With this lines the events will show in the developer
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        setContentView(R.layout.activity_login);

        facebookCallbackManager = CallbackManager.Factory.create();
        facebookLoginBtn = (LoginButton) findViewById(R.id.facebookLoginBtn);
        facebookLoginBtn.setReadPermissions("email");

        // get email string from shared preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String customer = sharedPreferences.getString("customer", "");

        // if the email found - it's not the first time opening this app
        // automatically redirect to home screen
        if (customer != null && !customer.equals("")) {
            finish();
            Intent homeScreen = new Intent(this, MenuActivity.class);
            startActivity(homeScreen);
        }

        facebookLoginBtn.registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            Customer customer;

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

                        Gson gson = new Gson();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        String customerJSON = gson.toJson(customer);
                        editor.putString("customer", customerJSON);
                        editor.apply();

                        new validateOrSignUpTask(customer).execute();
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
                new MyWebServiceTask().execute();
            }
        });
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
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
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
                .setNegativeButton(getString(R.string.exit_dialog_negavtive), null)
                .show();
    }

    private class MyWebServiceTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                Log.e("DEBUG","before save user");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                Customer toSave = gson.fromJson(response, Customer.class);
                String customerJSON = gson.toJson(toSave);
                editor.putString("customer", customerJSON);
                editor.apply();

                Log.e("DEBUG","after save user");

                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
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

            String responseString = response.toString();
            return responseString.toString();
        }

        @Override
        protected void onPreExecute() {
            emailTxt = etMail.getText().toString().trim();
            passwordTxt = etPassword.getText().toString().trim();
        }
    }

    private class validateOrSignUpTask extends AsyncTask<Void, Void, Boolean> {
        Customer facebookCustomer;
        String emailTxt;
        String passwordTxt;

        public validateOrSignUpTask(Customer customer){
            this.facebookCustomer = customer;
            emailTxt = facebookCustomer.getEmail();
            passwordTxt = facebookCustomer.getPassword();
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Boolean isExist = false;
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

            Log.d("FACEBOOK","after validation result is: " +response );

            if (!response.toString().trim().equals("null")) {
                return false;
            } else{
                Log.d("FACEBOOK","Sign up process starting..");

                // Request - send the customer as json to the server for insertion
                Gson gson = new Gson();
                String jsonUser = gson.toJson(facebookCustomer, Customer.class);
                URL url = null;
                try {
                    url = new URL(ApplicationConstant.USER_REGISTRATION_URL);
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


                    if (response.toString().trim().equals("OK")) {
                        result = true;
                    }
//                JsonObject objectRes = new JsonParser().parse(response.toString()).getAsJsonObject();
//                JsonElement elementRes = objectRes.get("result");
//                result = elementRes.getAsBoolean();

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

            finish();
            Intent homeScreen = new Intent(LoginActivity.this, MenuActivity.class);
            startActivity(homeScreen);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (profileTracker != null) {
            profileTracker.stopTracking();
        }
    }
}