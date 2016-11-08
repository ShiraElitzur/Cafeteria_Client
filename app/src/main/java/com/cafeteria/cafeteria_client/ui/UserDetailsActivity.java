package com.cafeteria.cafeteria_client.ui;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.Customer;
import com.google.gson.Gson;

public class UserDetailsActivity extends DrawerActivity {

    private SharedPreferences sharedPreferences;
    private Customer customer;
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnEnableEdit;
    private Button btnEditUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        super.onCreateDrawer();
        getSupportActionBar().setTitle(this.getTitle());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String customerString = sharedPreferences.getString("customer", "");
        if (customerString != null && !customerString.equals("")) {
            Gson gson = new Gson();
            customer = gson.fromJson(customerString, Customer.class);
        }
        etFirstName = (EditText)findViewById(R.id.etFirstName);
        etFirstName.setText(customer.getFirstName());
        etLastName = (EditText)findViewById(R.id.etLastName);
        etLastName.setText(customer.getLastName());
        etEmail = (EditText)findViewById(R.id.etEmail);
        etEmail.setText(customer.getEmail());
        etPassword = (EditText)findViewById(R.id.etPassword);
        etPassword.setText(customer.getPassword());
        btnEnableEdit = (Button)findViewById(R.id.btnEnableEdit);
        btnEnableEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UserDetailsActivity.this);
                alertDialogBuilder.setTitle("הזן סיסמה");
                //alertDialogBuilder.setMessage("הזן סיסמה");

                final EditText input = new EditText(UserDetailsActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialogBuilder.setView(input);

                alertDialogBuilder
                        .setPositiveButton("ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        etFirstName.setEnabled(true);
                                        etLastName.setEnabled(true);
                                        etEmail.setEnabled(true);
                                        etPassword.setEnabled(true);
                                        btnEditUser.setEnabled(true);
                                    }
                                }).create().show();
            }
        });

        btnEditUser = (Button)findViewById(R.id.btnEditUser);


    }
}
