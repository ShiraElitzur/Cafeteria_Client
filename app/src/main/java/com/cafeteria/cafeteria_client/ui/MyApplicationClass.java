package com.cafeteria.cafeteria_client.ui;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import com.cafeteria.cafeteria_client.utils.LocalDBHandler;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.util.Locale;
import java.util.logging.Handler;

public class MyApplicationClass extends Application{
    private static Configuration configuration;
    public static  String language = "iw";
    private LocalDBHandler db;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("APP","OnCreate APP");
        // initialize OneSignal for handling push notifications
        OneSignal.startInit(this).setNotificationOpenedHandler(new MyNotificationOpenedHandler()).init();
        db = new LocalDBHandler(this);

        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
    }

    // when oriention changes, keep the default language = hebrew
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
    }

    public static void changeLocale(Resources res, String locale){
        configuration = res.getConfiguration();
        switch (locale){
            case "iw":
                configuration.setLocale(new Locale("iw"));
                language = "iw";
                break;
            case "en":
                configuration.setLocale(Locale.ENGLISH);
                language = "en";
                break;
            default:
                configuration.setLocale(new Locale("iw"));
                language = "iw";
                break;
        }
        res.updateConfiguration(configuration,res.getDisplayMetrics());
    }

    public LocalDBHandler getLocalDB() {
        return db;
    }

    private class MyNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        // This fires when a notification is opened by tapping on it.
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            JSONObject data = result.notification.payload.additionalData;
            Log.e("DEBUG","Notification Result : "+data.toString());
            String orderId;
            int orderNumber;
            orderId = data.optString("order", null);
            orderNumber = Integer.parseInt(orderId);

            Intent intent = new Intent(getApplicationContext(), OrderReadyActivity.class);
            intent.putExtra("order_number",orderNumber);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

//            OSNotificationAction.ActionType actionType = result.action.type;
//            JSONObject data = result.notification.payload.additionalData;
//            String customKey;
//
//            if (data != null) {
//                customKey = data.optString("customkey", null);
//                if (customKey != null)
//                    Log.i("OneSignalExample", "customkey set with value: " + customKey);
//            }
//
//            if (actionType == OSNotificationAction.ActionType.ActionTaken)
//                Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

            // The following can be used to open an Activity of your choice.
            // Replace - getApplicationContext() - with any Android Context.
            // Intent intent = new Intent(getApplicationContext(), YourActivity.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            // startActivity(intent);

            // Add the following to your AndroidManifest.xml to prevent the launching of your main Activity
            //   if you are calling startActivity above.
     /*
        <application ...>
          <meta-data android:name="com.onesignal.NotificationOpened.DEFAULT" android:value="DISABLE" />
        </application>
     */
  }
}

}
