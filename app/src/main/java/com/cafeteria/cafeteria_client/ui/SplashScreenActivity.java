package com.cafeteria.cafeteria_client.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.Category;
import com.cafeteria.cafeteria_client.data.Customer;
import com.cafeteria.cafeteria_client.utils.ApplicationConstant;
import com.cafeteria.cafeteria_client.utils.DataHolder;
import com.cafeteria.cafeteria_client.data.Drink;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hanks.htextview.HTextView;
import com.hanks.htextview.HTextViewType;
import com.onesignal.OneSignal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class SplashScreenActivity extends AppCompatActivity {

    String[] titlesAnimation;

    private int mCounter = 0;

    private static final int SPLASH_TIME = 1500;
    private ProgressBar progressBar;
    private TextView tvStatus;
    private AlertDialog alertDialog;
    private Intent intent;
    private HTextView htvTitle;
    private getCategoriesTask getCategoriesTask;
    private GetDrinksTask getDrinksTask;
    private int userPKId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        printKeyHash();
        // set default language to hebrew
        MyApplicationClass.changeLocale(this.getResources(),"iw");

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        intent = new Intent(SplashScreenActivity.this,LoginActivity.class);
        htvTitle = (HTextView) findViewById(R.id.tvTitle);

        titlesAnimation = getResources().getStringArray(R.array.titleAnimation);

        htvTitle.setTextColor(Color.WHITE);
        //htvTitle.setBackgroundColor(Color.WHITE);
        Typeface type = Typeface.DEFAULT.createFromAsset(getAssets(),"fonts/PoiretOne-Regular.ttf");
        htvTitle.setTypeface(type);
        htvTitle.setAnimateType(HTextViewType.FALL);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.alert_dialog_no_internet_title)
                .setCancelable(false)
                .setPositiveButton(R.string.alert_dialog_no_internet_positive_button,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressBar.setProgress(0);
                        getDrinksTask.cancel(true);
                        getCategoriesTask.cancel(true);
                        new ProgressBarTask().execute();

                    }
                })
                .setNegativeButton(R.string.alert_dialog_no_internet_negative_button,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        finish();
                        startActivity(intent);
                    }
                });



        //Transpearent status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        alertDialog = alertDialogBuilder.create();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean logged = sharedPreferences.getBoolean("logged", false);
        if (logged){
            String customerJSON = sharedPreferences.getString("customer", "");
            if (!customerJSON.equals("")){
                Customer customer = new Gson().fromJson(customerJSON,Customer.class);
                userPKId = customer.getId();
                intent = new Intent(SplashScreenActivity.this,MenuActivity.class);
                new RefreshTokenTask().execute();
            }
        }

        new ProgressBarTask().execute();

    }

    private void printKeyHash(){
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.cafeteria.cafeteria_client",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("KeyHash:", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.d("KeyHash:", e.toString());
        }
    }
    /**
     * THE 3 ARGUMENTS REPRESNT:
     * VOID = the arguments sent to doingBackground (when we exceute)
     * Integer - on progress update
     * Boolean - doing backgroung
     */
    private class ProgressBarTask extends AsyncTask<Void, Integer, Boolean>{

        int i = 0;
        boolean isConnected;
        boolean getCategoriesTaskFinished = false;
        boolean getDrinksTaskFinished = false;


        @Override
        protected Boolean doInBackground(Void... voids) {
            while (i < 100){
                try {
                    if (i <=75){
                        Thread.sleep(SPLASH_TIME);
                        i+=25;
                        publishProgress(i);
                    } else {
                        getCategoriesTaskFinished = getCategoriesTask.getStatus() == Status.FINISHED;
                        getDrinksTaskFinished = getDrinksTask.getStatus() == Status.FINISHED;
                        if (getCategoriesTaskFinished && getDrinksTaskFinished){
                            i+=25;
                            publishProgress(i);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            isConnected = isOnline();


            return isConnected;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tvStatus.setText(R.string.progressBar_pre_execute);
            getCategoriesTask = new SplashScreenActivity.getCategoriesTask();
            getCategoriesTask.execute();
            getDrinksTask = new SplashScreenActivity.GetDrinksTask();
            getDrinksTask.execute();

        }


        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            progressBar.setProgress(100);
            if (!result) {
                tvStatus.setText(R.string.progressBar_post_execute_error);
                this.cancel(true);
                alertDialog.show();
            }else{
                tvStatus.setText(R.string.progressBar_post_execute_success);
                progressBar.setVisibility(View.GONE);
                try {
                    Thread.sleep(SPLASH_TIME-500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
                startActivity(intent);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progress = values[0];
            progressBar.setProgress(progress);
            mCounter = mCounter >= titlesAnimation.length - 1 ? 0 : mCounter + 1;
            htvTitle.animateText(titlesAnimation[mCounter]);
            if (progress > 70){
                tvStatus.setText(R.string.progressBar_progress_update);
            }
        }
    }

    private class getCategoriesTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String response) {
            if (response!=null) {
                Log.e("CATEGORIES", response);
                Type listType = new TypeToken<ArrayList<Category>>() {
                }.getType();
                List<Category> categoryList;
                categoryList = new Gson().fromJson(response, listType);
                if (categoryList != null) {
                    Log.e("CATEGORIES", "inside if categories not null");
                    DataHolder.getInstance().setCategories(categoryList);


                    for (Category c : categoryList) {
                        if (c != null) {
                            Log.d("cat", c.toString());
                        }
                        if (c.getMeals() != null) {
                            Log.d("meal", c.getMeals().toString());
                        }
                    }
                }
            }else{
                Toast.makeText(SplashScreenActivity.this,"Server is down",Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            Log.e("SHIRA","Second do in background");
            StringBuilder response;
            try {
                URL url = new URL(ApplicationConstant.GET_CATEGORIES_URL);
                response = new StringBuilder();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e("DEBUG","getCategories " +conn.getResponseCode() + " : "+ conn.getResponseMessage() );
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
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }


    private class GetDrinksTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            StringBuilder response;
            try {
                URL url = new URL(ApplicationConstant.GET_DRINKS_URL);
                response = new StringBuilder();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                Log.e("DEBUG",conn.getResponseCode()+"");
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e("DEBUG",conn.getResponseMessage());
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

                Type listType = new TypeToken<ArrayList<Drink>>() {
                }.getType();
                List<Drink> drinksList;
                drinksList = new Gson().fromJson(response.toString(), listType);
                DataHolder.getInstance().setDrinksList(drinksList);
                Log.e("DRINKS",drinksList.get(0).getTitle());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            return null;
        }
    }

    private class RefreshTokenTask extends AsyncTask<Void,Void,Void> {
        // to compare with the device token
        String userOldToken;
        StringBuilder response;
        URL url;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // http request to get the old token of the current user
                url = new URL(ApplicationConstant.GET_TOKEN+"?user="+userPKId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                Log.e("GET TOKEN",conn.getResponseCode()+"");
                if (conn.getResponseCode() < HttpURLConnection.HTTP_OK) {
                    Log.e("GET TOKEN",conn.getResponseMessage());
                    return null;
                }

                // get the response data
                response = new StringBuilder();
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));

                String line;
                while ((line = input.readLine()) != null) {
                    response.append(line + "\n");
                }
                // and get the token out of it as a clean string
                userOldToken = response.toString().trim();
                Log.e("TOKEN","old token : " + userOldToken);
                conn.disconnect();

                // now comparing this old token with the device token (= 'userId' in the overridden method)
                OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                    @Override
                    public void idsAvailable(String userId, String registrationId) {
                        Log.d("debug", "Device Token:" + userId);
                        // if there is no saved token or token is different from device token
                        if(userOldToken == null || !userOldToken.equals(userId)) {
                            try {
                                // request the server to attach the device token (userId) to the right user (with the id userPKId)
                                url = new URL(ApplicationConstant.SET_TOKEN+"?userId="+userPKId+"&pushId="+userId);
                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                Log.e("SET TOKEN", conn.getResponseCode() + "");
                                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                                    Log.e("SET TOKEN", conn.getResponseMessage());
                                }
                            }catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (registrationId != null)
                            Log.d("debug", "registrationId:" + registrationId);
                    }
                });

                // ******* SEND THE NOTIFICATION ***********
                // trigger the server to send the notification, shouldn't be here but i want to keep the code
//                url = new URL(ApplicationConstant.SEND_NOTI_URL+"?userId="+userPKId);
//                conn = (HttpURLConnection) url.openConnection();
//                Log.e("DEBUG",conn.getResponseCode()+"");
//                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
//                    Log.e("DEBUG",conn.getResponseMessage());
//                    return null;
//                }
//                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            return null;
        }
    }

}
