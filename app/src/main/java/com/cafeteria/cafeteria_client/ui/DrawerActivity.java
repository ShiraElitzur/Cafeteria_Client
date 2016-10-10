package com.cafeteria.cafeteria_client.ui;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.Customer;
import com.cafeteria.cafeteria_client.utils.LocaleHelper;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;

/**
 * An activity with Navigation Drawer
 * An activity that extends that class must have in it's layout xml file the following components, with the specified ids:
 * 1. DrawerLayout - drawer_layout
 * 2. ToolBar - toolbar
 * 3. NavigationView - navigation_view
 */
public abstract class DrawerActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private Intent intent;
    private TextView tvHeaderTitle;

    protected void onCreateDrawer() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        // Sets the title as empty string because the default behavior is to display as title the app name.
        // Right now we have a background image on the action bar that contains the app name -
        // Need to think about that point.
        getSupportActionBar().setTitle("");
        getSupportActionBar().setLogo(ContextCompat.getDrawable(this,R.drawable.logo_transparent));
        //getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.logo));

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);

        tvHeaderTitle = (TextView) headerView.findViewById((R.id.tvHeaderTitle));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String customerJSON = sharedPreferences.getString("customer", "");
        Customer c = gson.fromJson(customerJSON,Customer.class);;
        tvHeaderTitle.setText(c.getFirstName() + " " + c.getLastName());

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.navigation_item_cafeteria_menu:
                        if( DrawerActivity.this instanceof MenuActivity){
                            return false;
                        }
                        intent = new Intent(DrawerActivity.this,MenuActivity.class);
                        startActivity(intent);
                        DrawerActivity.this.finish();
                        break;
                    case R.id.navigation_item_cart:
                        if( DrawerActivity.this instanceof OrderActivity){
                            return false;
                        }
                        intent = new Intent(DrawerActivity.this,OrderActivity.class);
                        startActivity(intent);
                        DrawerActivity.this.finish();
                        break;
                    case R.id.navigation_item_log_out:
                        SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = mySPrefs.edit();
                        editor.remove("customer");
                        editor.apply();

                        FacebookSdk.sdkInitialize(getApplicationContext());
                        if (LoginManager.getInstance() != null){
                            LoginManager.getInstance().logOut();
                        }

                        intent = new Intent(DrawerActivity.this,LoginActivity.class);
                        startActivity(intent);
                        DrawerActivity.this.finish();
                        break;
                    case R.id.navigation_item_about:
                        final Dialog dialog = new Dialog(DrawerActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(true);
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.setContentView(R.layout.dialog_about_the_app);

                        dialog.show();
                        break;
                    case R.id.navigation_item_langauge:

                        String language = LocaleHelper.getLanguage(DrawerActivity.this);
                        if (language.equals("en")){
                            LocaleHelper.setLocale(DrawerActivity.this, "iw");
                            DrawerActivity.this.recreate();

                        } else if (language.equals("iw")){
                            LocaleHelper.setLocale(DrawerActivity.this, "en");
                            DrawerActivity.this.recreate();

                        }

                        break;
                    case R.id.navigation_item_history:
                        if( DrawerActivity.this instanceof OrdersHistoryActivity){
                            return false;
                        }
                        intent = new Intent(DrawerActivity.this,OrdersHistoryActivity.class);
                        startActivity(intent);
                        DrawerActivity.this.finish();
                        break;


                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.navigation_item_cafeteria_menu:

        }

        return super.onOptionsItemSelected(item);
    }
}
