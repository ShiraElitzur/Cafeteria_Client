package com.cafeteria.cafeteria_client.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.Category;
import com.cafeteria.cafeteria_client.data.DataHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hanks.htextview.HTextView;
import com.hanks.htextview.HTextViewType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SplashScreenActivity extends AppCompatActivity {

    String[] titlesAnimation;

    private int mCounter = 0;

    private static final int SPLASH_TIME = 1500;
    private ProgressBar progressBar;
    private TextView tvStatus;
    private AlertDialog alertDialog;
    private Intent intent;
    private HTextView htvTitle;

    private final static String ANAEL_SERVER_IP = "192.168.43.91";
    private final static String SHIRA_SERVER_IP = "192.168.43.231";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        setDefaultLanguageToHebrew();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        intent = new Intent(SplashScreenActivity.this,LoginActivity.class);
        htvTitle = (HTextView) findViewById(R.id.tvTitle);

        titlesAnimation = getResources().getStringArray(R.array.titleAnimation);

        htvTitle.setTextColor(Color.BLACK);
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
                        new BackgroundTask().execute();

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
        new BackgroundTask().execute();
    }

    private void setDefaultLanguageToHebrew(){
        //set default language to hebrew
        Locale locale = new Locale("iw");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    /**
     * THE 3 ARGUMENTS REPRESNT:
     * VOID = the arguments sent to doingBackground (when we exceute)
     * Integer - on progress update
     * Boolean - doing backgroung
     */
    private class BackgroundTask extends AsyncTask<Void, Integer, Boolean>{

        int i = 0;
        boolean isConnected;


        @Override
        protected Boolean doInBackground(Void... voids) {
            while (i < 100){
                try {
                    Thread.sleep(SPLASH_TIME);
                    i+=25;
                    publishProgress(i);
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

                new MyWebServiceTask().execute();
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



    class MyWebServiceTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String response) {
            Type listType = new TypeToken<ArrayList<Category>>() {
            }.getType();
            List<Category> categoryList;
            categoryList = new Gson().fromJson(response, listType);
            if (categoryList != null) {
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
                URL url = new URL("http://" + SHIRA_SERVER_IP + ":8080/CafeteriaServer/rest/data/getCategories");
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
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}
