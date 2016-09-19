package com.cafeteria.cafeteria_client.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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

public class SignUpActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private EditText etMail;
    private EditText etPassword;
    private EditText etConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        /* Get the components from the UI and set listeners to them */
        // The Email EditText - handle events of changes in the focus, validation check

        etMail = (EditText)findViewById(R.id.etMail);
        etMail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    EditText emailEdit = (EditText)v;
                    String emailInput = emailEdit.getText().toString();
                    if (emailInput.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                        emailEdit.setError(getResources().getString(R.string.email_address_error));
                    } else {
                        emailEdit.setError(null);
                    }
                }
            }
        });

        // The Password EditText - handle events of changes in the focus, validation check

        etPassword = (EditText)findViewById(R.id.etPassword);
        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    EditText passwordEdit = (EditText)v;
                    String passwordInput = passwordEdit.getText().toString();
                    if (passwordInput.isEmpty() || passwordInput.length() < 6 || passwordInput.length() > 10 ) {
                        passwordEdit.setError(getResources().getString(R.string.new_password_error));
                    } else {
                        passwordEdit.setError(null);
                    }
                }
            }
        });

        etConfirmPassword = (EditText)findViewById(R.id.etConfirmPassword);
        etConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText inputEdit = (EditText)v;
                String inputString = inputEdit.getText().toString();
                String passwordString = etPassword.getText().toString();

                if(!inputString.equals(passwordString)) {
                    inputEdit.setError(getResources().getString(R.string.passwords_no_match_error));
                }
            }
        });

        // The SignUp Button - handle the event of click on the button

        Button signUpBtn;
        signUpBtn = (Button)findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if all the one of the input fields is not valid so the sign up is not authorized
                if( etMail.getError() != null || etPassword.getError() != null ||
                        etMail.getText().toString().isEmpty() ||
                        etPassword.getText().toString().isEmpty()
                        || etConfirmPassword.getError() != null
                        ) {
                    String error = getString(R.string.btn_signup_failed) + '\n';
                    if( etMail.getError() != null){
                        error += etMail.getError();
                        error+= "\n";
                    }
                    if (etPassword.getError() != null){
                        error += etPassword.getError();
                    }
                    if (etConfirmPassword.getError() != null){
                        error += etConfirmPassword.getError();
                    }
                    Toast.makeText(SignUpActivity.this,error,Toast.LENGTH_LONG).show();
                    return;
                }

                // TODO: 13/09/2016  Add a real signUp logic (save details in db) and think about and fix the validation logic

                etMail = (EditText)findViewById(R.id.etMail);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email",etMail.getText().toString());
                editor.apply();

                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                SignUpActivity.this.finish();
            }
        });

        TextView loginLinkTv = (TextView)findViewById(R.id.loginLinkTv);
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
}
