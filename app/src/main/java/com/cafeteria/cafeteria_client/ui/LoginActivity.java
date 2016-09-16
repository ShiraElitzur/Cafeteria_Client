package com.cafeteria.cafeteria_client.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
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
    private final static String SERVER_IP = "172.16.26.12";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //set default language to hebrew
        Locale locale = new Locale("iw");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String email = sharedPreferences.getString("email","");

        TextView signUpLinkTv = (TextView)findViewById(R.id.tvSignUpLink);
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
                etMail = (EditText)findViewById(R.id.etMail);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email",etMail.getText().toString());
                editor.commit();

                new MyWebServiceTask().execute();

                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        });

        // if the email found - it's not the first time opening this app
        // automatically redirect to home screen
        if (email != null && !email.equals("")){
            finish();
            Intent homeScreen = new Intent(this,MenuActivity.class);
            startActivity(homeScreen);
        }
    }


    class MyWebServiceTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPostExecute(String response) {
            Type listType = new TypeToken<ArrayList<Category>>(){}.getType();

            List<Category> categoryList = new Gson().fromJson(response,listType);
            if (categoryList!= null) {
                Toast.makeText(LoginActivity.this, categoryList.get(0).getTitle(), Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder response;
            try {

                URL url = new URL("http://"+SERVER_IP+":8080/CafeteriaServer/rest/data/getCategories");
                response = new StringBuilder();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                BufferedReader input = new BufferedReader(
                        new InputStreamReader(conn .getInputStream()));

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
    }
}

