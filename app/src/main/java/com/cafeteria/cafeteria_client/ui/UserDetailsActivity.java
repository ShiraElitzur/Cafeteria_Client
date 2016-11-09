package com.cafeteria.cafeteria_client.ui;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.Customer;
import com.cafeteria.cafeteria_client.utils.ApplicationConstant;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class UserDetailsActivity extends DrawerActivity {

    private SharedPreferences sharedPreferences;
    private Customer customer;
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmail;
    private TextView tvEditPassword;
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
        tvEditPassword = (TextView) findViewById(R.id.tvEditPassword);
        tvEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UserDetailsActivity.this);
                LayoutInflater layout = getLayoutInflater();
                final View dialogView = layout.inflate(R.layout.dialog_change_password, null);
                alertDialogBuilder.setView(dialogView);
                alertDialogBuilder
                        .setPositiveButton(getResources().getString(R.string.change_password),
                                new DialogInterface.OnClickListener() {

                                    EditText oldPassword;
                                    EditText newPassword;
                                    EditText confirmNewPassword;

                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        oldPassword = (EditText)dialogView.findViewById(R.id.etOldPassword);
                                        newPassword = (EditText)dialogView.findViewById(R.id.etNewPassword);
                                        confirmNewPassword = (EditText)dialogView.findViewById(R.id.etConfirmNewPassword);
                                        String toastText = "";
                                        if( oldPassword.getText().toString().equals(customer.getPassword())) {
                                            if (newPassword.getText().toString().equals(
                                                    confirmNewPassword.getText().toString())) {
                                                // save new password
                                                customer.setPassword(newPassword.getText().toString());
                                                toastText = getResources().getString(R.string.password_change_success);
                                            } else {
                                                // passwords dont match
                                                // TODO: 09/11/2016 need some other handling. some error msg without dismiss dialog
                                                toastText = "New passwords don't match\nPassword didn't changed";
                                            }
                                        } else {
                                            // password not correct
                                            toastText = getResources().getString(R.string.password_incorrect);
                                        }
                                        Toast.makeText(UserDetailsActivity.this, toastText,Toast.LENGTH_LONG).show();
                                    }
                                }).create().show();
            }
        });

        btnEditUser = (Button)findViewById(R.id.btnEditUser);
        btnEditUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customer.setFirstName(etFirstName.getText().toString());
                customer.setLastName(etLastName.getText().toString());
                customer.setEmail(etEmail.getText().toString());
                new UpdateUserTask().execute();
            }
        });



    }

    private class UpdateUserTask extends AsyncTask<Void,Void,Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean result = false;
            // Request - send the customer as json to the server for insertion
            Gson gson = new Gson();
            String jsonUser = gson.toJson(customer, Customer.class);
            URL url = null;
            try {
                url = new URL(ApplicationConstant.UPDATE_USER_URL);
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
                Toast.makeText(UserDetailsActivity.this, getResources().getString(R.string.update_user_success), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(UserDetailsActivity.this, getResources().getString(R.string.update_user_failed), Toast.LENGTH_LONG).show();
            }

        }
    }
}
