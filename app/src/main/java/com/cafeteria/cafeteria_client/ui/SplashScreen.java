package com.cafeteria.cafeteria_client.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cafeteria.cafeteria_client.R;
import com.hanks.htextview.HTextView;
import com.hanks.htextview.HTextViewType;

import com.cafeteria.cafeteria_client.R;

public class SplashScreen extends AppCompatActivity {

    String[] titlesAnimation;

    private int mCounter = 0;

    private static final int SPLASH_TIME = 1500;
    private ProgressBar progressBar;
    private TextView tvStatus;
    private AlertDialog alertDialog;
    private Intent intent;
    private HTextView htvTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        intent = new Intent(SplashScreen.this,LoginActivity.class);
        htvTitle = (HTextView) findViewById(R.id.tvTitle);

        titlesAnimation = getResources().getStringArray(R.array.titleAnimation);

        htvTitle.setTextColor(Color.BLACK);
        //htvTitle.setBackgroundColor(Color.WHITE);
        Typeface type = Typeface.DEFAULT.createFromAsset(getAssets(),"fonts/PoiretOne-Regular.ttf");
        htvTitle.setTypeface(type);
        htvTitle.setAnimateType(HTextViewType.FALL);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.alert_dialog_title)
                .setCancelable(false)
                .setPositiveButton(R.string.alert_dialog_positive_button,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressBar.setProgress(0);
                        new BackgroundTask().execute();

                    }
                })
                .setNegativeButton(R.string.alert_dialog_negative_button,
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


    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}
