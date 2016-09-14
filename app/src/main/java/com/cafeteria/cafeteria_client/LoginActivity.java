package com.cafeteria.cafeteria_client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {


    private SharedPreferences sharedPreferences;
    private EditText etMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

}
