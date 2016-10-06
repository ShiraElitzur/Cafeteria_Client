package com.cafeteria.cafeteria_client.ui;

import android.app.Application;
import android.content.res.Configuration;

import com.cafeteria.cafeteria_client.data.LocaleHelper;

public class MyApplicationClass extends Application {

    // when oriention changes, keep the default language = hebrew
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleHelper.onCreate(this);
    }

}
