package com.cafeteria.cafeteria_client.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.Cafeteria;
import com.cafeteria.cafeteria_client.utils.ApplicationConstant;
import com.cafeteria.cafeteria_client.utils.DataHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChooseCafeteriaActivity extends AppCompatActivity {
    private List<Cafeteria> servers = new ArrayList<>();
    private List<String> serversNames = new ArrayList<>();
    private DataHolder dataHolder = DataHolder.getInstance();
    private AutoCompleteTextView autoCompleteTvCafeteria;
    private Button btnNext;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_cafeteria);
        MyApplicationClass.changeLocale(this.getResources(), "iw");

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Cafeteria cafeteria = new Gson().fromJson(sharedPreferences.getString("cafeteria", ""),Cafeteria.class);
        if (cafeteria != null){
            dataHolder.setCafeteria(cafeteria);
            goToHomeScreen();
        } else {

            autoCompleteTvCafeteria = (AutoCompleteTextView)
                    findViewById(R.id.autoCompleteTvCafeteria);
            btnNext = (Button) findViewById(R.id.btnNext);

            new GetServers().execute();

            autoCompleteTvCafeteria.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    dataHolder.setCafeteria(servers.get(i));
                    Log.e("SERVERS", "server chosen: " + serversNames.get(i) + " in server ip " + servers.get(i).getServerIp());
                }
            });

            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dataHolder.getCafeteria() == null || !serversNames.contains(autoCompleteTvCafeteria.getText().toString())) {
                        Snackbar snackbar = Snackbar
                                .make(view, getString(R.string.choose_cafeteria_toast), Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(ChooseCafeteriaActivity.this, android.R.color.white));
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.BLACK);
                        snackbar.show();
                    } else {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("cafeteria", new Gson().toJson(dataHolder.getCafeteria()));
                        editor.apply();

                        goToHomeScreen();
                    }
                }
            });
        }
    }

    private void goToHomeScreen() {
        finish();
        Intent splashIntent = new Intent(ChooseCafeteriaActivity.this, SplashScreenActivity.class);
        startActivity(splashIntent);
    }

    private class GetServers extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {
            StringBuilder response;
            try {
                URL url = new URL(ApplicationConstant.GET_SERVERS);
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

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            return response.toString();
        }

        @Override
        protected void onPostExecute(String response) {
            if (response!=null) {
                Type listType = new TypeToken<ArrayList<Cafeteria>>() {
                }.getType();
                servers = new Gson().fromJson(response, listType);
                for (Cafeteria server: servers){
                    Log.e("SERVERS","servers name: " + server.getCafeteriaName());
                    serversNames.add(server.getCafeteriaName());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ChooseCafeteriaActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, serversNames);
                autoCompleteTvCafeteria.setAdapter(adapter);
                autoCompleteTvCafeteria.setVisibility(View.VISIBLE);
                autoCompleteTvCafeteria.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        autoCompleteTvCafeteria.showDropDown();
                    }
                });
            }
            Log.e("SERVERS","servers size: " + servers.size());

        }
    }



}
