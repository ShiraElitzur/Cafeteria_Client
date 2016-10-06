package com.cafeteria.cafeteria_client.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.Customer;
import com.google.gson.Gson;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private EditText etMail;
    private EditText etPassword;
    private String emailTxt;
    private String passwordTxt;

    //private final static String SERVER_IP = "192.168.43.231";  // SHIRA IP
    private final static String SERVER_IP = "192.168.1.11"; // ANAEL IP
    private final static String USER_VALIDATION_URL = "http://"+SERVER_IP+":8080/CafeteriaServer/rest/users/isUserExist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.exit_dialog_title))
                .setMessage(getString(R.string.exit_dialog_message))
                .setPositiveButton(getString(R.string.exit_dialog_postive), new DialogInterface.OnClickListener()
                {
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
            if(response != null ) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                Customer toSave = gson.fromJson(response,Customer.class);
                String customerJSON = gson.toJson(toSave);
                editor.putString("customer", customerJSON);
                editor.commit();

                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            } else {
                Toast.makeText(LoginActivity.this,getResources().getString(R.string.login_error),Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder response;
            try {
                URL url = new URL(USER_VALIDATION_URL+"?email="+emailTxt+"&pass="+passwordTxt);
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

}