package com.cafeteria.cafeteria_client.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.cafeteria.cafeteria_client.R;

public class AboutActivity extends AppCompatActivity {
    private TextView tvAppName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        tvAppName = (TextView) findViewById(R.id.tvAppName);
        Typeface type = Typeface.DEFAULT.createFromAsset(getAssets(),"fonts/PatuaOne-Regular.ttf");
        tvAppName.setTypeface(type);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setLogo(ContextCompat.getDrawable(this,R.drawable.logo_transparent));
        getSupportActionBar().setTitle(this.getTitle());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                Intent intent = new Intent(AboutActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent menuIntent = new Intent(AboutActivity.this, MainActivity.class);
        startActivity(menuIntent);

    }
}
