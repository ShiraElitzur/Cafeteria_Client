package com.cafeteria.cafeteria_client.ui;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import com.cafeteria.cafeteria_client.LocalDBHandler;
import com.cafeteria.cafeteria_client.utils.LocaleHelper;
import com.onesignal.OneSignal;

public class MyApplicationClass extends Application {

    private LocalDBHandler db;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("APP","OnCreate APP");
        // initialize OneSignal for handling push notifications
        OneSignal.startInit(this).init();
        db = new LocalDBHandler(this);
    }

    // when oriention changes, keep the default language = hebrew
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleHelper.onCreate(this);

    }

    public LocalDBHandler getLocalDB() {
        return db;
    }

}
