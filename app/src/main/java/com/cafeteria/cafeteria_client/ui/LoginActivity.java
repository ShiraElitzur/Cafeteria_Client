package com.cafeteria.cafeteria_client.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
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
import com.cafeteria.cafeteria_client.data.Category;
import com.cafeteria.cafeteria_client.data.DataHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {


    private SharedPreferences sharedPreferences;
    private EditText etMail;
    private EditText etPassword;
    private final static String SERVER_IP = "172.16.26.12";
    private final static String ANAEL_SERVER_IP = "192.168.43.91";
    List<Category> categoryList;
    String emailTxt;
    String passwordTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


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

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", etMail.getText().toString());
                editor.apply();

                new MyWebServiceTask().execute();

                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        });

        // get email string from shared prefrence
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String email = sharedPreferences.getString("email", "");

        // if the email found - it's not the first time opening this app
        // automatically redirect to home screen
        if (email != null && !email.equals("")) {
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

    class MyWebServiceTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String response) {
            Type listType = new TypeToken<ArrayList<Category>>() {
            }.getType();

            categoryList = new Gson().fromJson(response, listType);
            if (categoryList != null) {
                Toast.makeText(LoginActivity.this, categoryList.get(0).getTitle(), Toast.LENGTH_SHORT).show();
                DataHolder.getInstance().setCategories(categoryList);


                for (Category c : categoryList) {
                    if (c != null){
                    Log.d("cat", c.toString());}
                    if (c.getMeals() != null){
                    Log.d("meal", c.getMeals().toString()); }
                }
            }


        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder response;
            try {

//                URL url = new URL("http://"+ANAEL_SERVER_IP+":8080/CafeteriaServer/rest/data/isCustomerExist?email="+emailTxt+"&pass="+passwordTxt+"");
                URL url = new URL("http://" + ANAEL_SERVER_IP + ":8080/CafeteriaServer/rest/data/getCategories");
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
            return response.toString();
        }

        @Override
        protected void onPreExecute() {
            emailTxt = etMail.getText().toString();
            passwordTxt = etPassword.getText().toString();
        }
    }
}

