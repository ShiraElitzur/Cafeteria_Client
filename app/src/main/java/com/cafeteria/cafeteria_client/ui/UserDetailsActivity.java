package com.cafeteria.cafeteria_client.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.Customer;
import com.cafeteria.cafeteria_client.utils.ApplicationConstant;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
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
    private ImageView imgViewUser;
    private Uri imgUri;
    public static final int CAMERA_REQUEST_CODE = 1;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };


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
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etFirstName.setText(customer.getFirstName());
        etLastName = (EditText) findViewById(R.id.etLastName);
        etLastName.setText(customer.getLastName());
        etEmail = (EditText) findViewById(R.id.etEmail);
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
                                        oldPassword = (EditText) dialogView.findViewById(R.id.etOldPassword);
                                        newPassword = (EditText) dialogView.findViewById(R.id.etNewPassword);
                                        confirmNewPassword = (EditText) dialogView.findViewById(R.id.etConfirmNewPassword);
                                        String toastText = "";
                                        if (oldPassword.getText().toString().equals(customer.getPassword())) {
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
                                        Toast.makeText(UserDetailsActivity.this, toastText, Toast.LENGTH_LONG).show();
                                    }
                                }).create().show();
            }
        });

        btnEditUser = (Button) findViewById(R.id.btnEditUser);
        btnEditUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customer.setFirstName(etFirstName.getText().toString());
                customer.setLastName(etLastName.getText().toString());
                customer.setEmail(etEmail.getText().toString());
                Log.e("DEBUG", "Pic : " + customer.getImagePath());
                new UpdateUserTask().execute();
            }
        });

        imgViewUser = (ImageView) findViewById(R.id.imgviewUser);

        if (customer.getImagePath() != null) {
            com.squareup.picasso.Picasso.with(this).
                    load("http://" + ApplicationConstant.SERVER_IP + customer.getImagePath()).
                    into(imgViewUser);
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPreferredConfig = Bitmap.Config.RGB_565;
//            Bitmap bitmap = BitmapFactory.decodeByteArray(
//                    customer.getImage() , 0, customer.getImage().length,options);
//            imgViewUser.setImageBitmap(bitmap);
        }
        imgViewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permission;
                if (Build.VERSION.SDK_INT >= 23) {
                     permission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        // We don't have permission so prompt the user
                        requestPermissions(
                                PERMISSIONS_STORAGE,
                                REQUEST_EXTERNAL_STORAGE
                        );
                    } else {
                        takePicture();
                    }
                } else {
                     permission = PermissionChecker.checkSelfPermission(UserDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    if (permission == PermissionChecker.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(UserDetailsActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                                REQUEST_EXTERNAL_STORAGE);
                    } else {
                        takePicture();
                    }
                }


            }
        });

        //set checked item on drawer
        navigationView.setCheckedItem(R.id.navigation_item_personal_details);

    }


    private void takePicture() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File img = null;
        try {
            img = File.createTempFile("temp", ".jpg", Environment.getExternalStorageDirectory());
            Log.e("delete file", "file delete: " + img.delete());
        } catch (IOException e) {
            Log.e("debug", "IOException- failed to create temporary file");
            e.printStackTrace();
        }

        imgUri = Uri.fromFile(img);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    takePicture();
                } else {
                    // Permission Denied
                    Toast.makeText(UserDetailsActivity.this, "Picture taking Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private class UpdateUserTask extends AsyncTask<Void, Void, Boolean> {

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

                // update the customer in the shared preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String customerJSON = gson.toJson(customer);
                editor.putString("customer", customerJSON);
                editor.apply();

                Toast.makeText(UserDetailsActivity.this, getResources().getString(R.string.update_user_success), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(UserDetailsActivity.this, getResources().getString(R.string.update_user_failed), Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

//            Bitmap bitmap = null;
//
//            byte[] byteArray = null;
//            // get the bitmap from the uri that it was stored in
//
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
//            } catch (FileNotFoundException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
            File file = new File(imgUri.getPath());
//            file.delete();

            com.squareup.picasso.Picasso.with(this).
                    load(file).
                    into(imgViewUser);
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//
//
//            if( bitmap != null ) {
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
//                byteArray = stream.toByteArray();
//            }
//
//            customer.setImage(byteArray);
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPreferredConfig = Bitmap.Config.RGB_565;
//            imgViewUser.setImageBitmap(BitmapFactory.decodeByteArray(
//                    byteArray , 0, byteArray.length,options));
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent menuIntent = new Intent(UserDetailsActivity.this, MenuActivity.class);
        startActivity(menuIntent);
    }
}
