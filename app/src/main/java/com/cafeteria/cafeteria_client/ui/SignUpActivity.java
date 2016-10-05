package com.cafeteria.cafeteria_client.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.Customer;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class SignUpActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private EditText etEmail;
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Customer customer;
    private boolean isValid = true;

    String print;

    private final static String SERVER_IP = "192.168.1.11";
    //private final static String SERVER_IP = "192.168.43.231";
    private final static String USER_REGISTRATION_URL = "http://" + SERVER_IP + ":8080/CafeteriaServer/rest/users/insertUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        /* Get the components from the UI and set listeners to them */
        // The Email EditText - handle events of changes in the focus, validation check

        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);


        etFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                TextInputLayout firstNameLayout = (TextInputLayout) findViewById(R.id.input_layout_firstName);
                if (etFirstName.getText().toString().isEmpty()) {
                    firstNameLayout.setError(getResources().getString(R.string.empty_error));
                    isValid = false;
                } else {
                    firstNameLayout.setError(null);
                    isValid = true;
                }
            }
        });

        etLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                TextInputLayout lastNameLayout = (TextInputLayout) findViewById(R.id.input_layout_lastName);
                if (etLastName.getText().toString().isEmpty()) {
                    lastNameLayout.setError(getResources().getString(R.string.empty_error));
                    isValid = false;
                } else {
                    lastNameLayout.setError(null);
                    isValid = true;
                }
            }
        });

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String email = etEmail.getText().toString();
                TextInputLayout emailInputLayout = (TextInputLayout) findViewById(R.id.input_layout_email);
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailInputLayout.setError(getResources().getString(R.string.email_address_error));
                    isValid = false;
                } else if (email.isEmpty()) {
                    emailInputLayout.setError(getResources().getString(R.string.empty_error));
                    isValid = false;
                } else {
                    emailInputLayout.setError(null);
                    isValid = true;
                }
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = etPassword.getText().toString();
                TextInputLayout passwordInputLayout = (TextInputLayout) findViewById(R.id.input_layout_password);
                if (password.length() < 6 || password.length() > 10) {
                    passwordInputLayout.setError(getResources().getString(R.string.new_password_error));
                    isValid = false;
                } else if(password.isEmpty()) {
                    passwordInputLayout.setError(getResources().getString(R.string.empty_error));
                    isValid = false;
                }
                else {
                    passwordInputLayout.setError(null);
                    isValid = true;
                }
            }
        });

        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String confirmPassword = etConfirmPassword.getText().toString();
                String password = etPassword.getText().toString();
                TextInputLayout confirmPasswordInputLayout = (TextInputLayout) findViewById(R.id.input_layout_confirm_password);
                if (!confirmPassword.equals(password)) {
                    confirmPasswordInputLayout.setError(getResources().getString(R.string.passwords_no_match_error));
                    isValid = false;
                } else {
                    confirmPasswordInputLayout.setError(null);
                    isValid = true;
                }
            }
        });

        // The SignUp Button - handle the event of click on the button

        Button signUpBtn;
        signUpBtn = (Button) findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid) {
                    new SignUpTask().execute();
                } else {
                    Toast.makeText(SignUpActivity.this, getString(R.string.signup_error), Toast.LENGTH_SHORT).show();
                }

            }
        });

        TextView loginLinkTv = (TextView) findViewById(R.id.loginLinkTv);
        loginLinkTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                SignUpActivity.this.finish();
            }
        });
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

    private class SignUpTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            // create a Customer out of the edit texts
            customer = new Customer();
            customer.setEmail(etEmail.getText().toString());
            customer.setFirstName(etFirstName.getText().toString());
            customer.setLastName(etLastName.getText().toString());
            customer.setPassword(etPassword.getText().toString());
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean result = false;

            // Request - send the customer as json to the server for insertion
            Gson gson = new Gson();
            String jsonUser = gson.toJson(customer, Customer.class);
            URL url = null;
            try {
                url = new URL(USER_REGISTRATION_URL);
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
                StringBuilder response = new StringBuilder();
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));

                String line;
                while ((line = input.readLine()) != null) {
                    response.append(line + "\n");
                }

                input.close();

                con.disconnect();

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

        @Override
        protected void onPostExecute(Boolean result) {
            if (result != null && result) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", customer.getEmail());
                editor.putString("user_name", customer.getFirstName());
                editor.apply();

                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                SignUpActivity.this.finish();
            } else {
                Toast.makeText(SignUpActivity.this, getResources().getString(R.string.signup_error), Toast.LENGTH_LONG).show();
            }

        }
    }
}
