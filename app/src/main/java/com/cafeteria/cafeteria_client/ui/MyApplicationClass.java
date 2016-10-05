package com.cafeteria.cafeteria_client.ui;

import android.app.Application;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cafeteria.cafeteria_client.data.LocaleHelper;

import java.util.Locale;

public class MyApplicationClass extends Application {

    // when oriention changes, keep the default language = hebrew
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleHelper.onCreate(this);
    }

}
