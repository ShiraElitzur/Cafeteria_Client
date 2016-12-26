package com.cafeteria.cafeteria_client.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.Customer;
import com.cafeteria.cafeteria_client.data.Order;
import com.cafeteria.cafeteria_client.utils.DataHolder;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;

import static com.cafeteria.cafeteria_client.ui.MyApplicationClass.language;

/**
 * An activity with Navigation Drawer
 * An activity that extends that class must have in it's layout xml file the following components, with the specified ids:
 * 1. DrawerLayout - drawer_layout
 * 2. ToolBar - activity_main
 * 3. NavigationView - navigation_view
 */
public abstract class DrawerActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    DrawerLayout drawerLayout;
    private Intent intent;
    private TextView tvHeaderTitle;
    NavigationView navigationView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    protected void onCreateDrawer() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //activity_main.setElevation(0);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);


        // Sets the title as empty string because the default behavior is to display as title the app name.
        // Right now we have a background image on the action bar that contains the app name -
        // Need to think about that point.
        getSupportActionBar().setTitle("");
        //getSupportActionBar().setLogo(ContextCompat.getDrawable(this,R.drawable.main_logo));
        //getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.main_logo));

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        tvHeaderTitle = (TextView) headerView.findViewById((R.id.tvHeaderTitle));
        Gson gson = new Gson();
        String customerJSON = sharedPreferences.getString("customer", "");
        Customer c = gson.fromJson(customerJSON,Customer.class);;
        tvHeaderTitle.setText(c.getFirstName() + " " + c.getLastName());

        ImageView imgviewHeaderImage = (ImageView) headerView.findViewById(R.id.imgviewHeaderImage);
        if (c.getImage() != null){
            Bitmap b = BitmapFactory.decodeByteArray(c.getImage(), 0, c.getImage().length);
            if (b != null) {
                imgviewHeaderImage.setImageBitmap(b);
            }
        }
        imgviewHeaderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent userDetailsIntent = new Intent(DrawerActivity.this,UserDetailsActivity.class);
                startActivity(userDetailsIntent);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);

                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.navigation_item_cafeteria_menu:
                        if( DrawerActivity.this instanceof MainActivity){
                            return false;
                        }
                        intent = new Intent(DrawerActivity.this,MainActivity.class);
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
                        String payedOrdersCounterString = sharedPreferences.getString("payedOrdersCounter","");
                        int counter;
                        if( !payedOrdersCounterString.isEmpty()) {
                            counter = Integer.parseInt(payedOrdersCounterString);
                            if( counter > 0 ) {
                                new AlertDialog.Builder(DrawerActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(getString(R.string.logout_dialog_alert))
                                .setMessage(getString(R.string.logout_dialog_message))
                                .setPositiveButton(getString(R.string.logout_dialog_postive),new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        editor.remove("payedOrdersCounter");
                                        logout();
                                    }
                                })
                                .setNegativeButton(getString(R.string.logout_dialog_negative), null)
                                .show();
                            } else {
                                logout();
                            }
                        } else {
                            logout();
                        }
                        break;
                    case R.id.navigation_item_about:
                        intent = new Intent(DrawerActivity.this,AboutActivity.class);
                        startActivity(intent);
                        DrawerActivity.this.finish();
                        break;
                    case R.id.navigation_item_langauge:

                        if (language.equals("en")){
                            MyApplicationClass.changeLocale(DrawerActivity.this.getResources(),"iw");
                            DrawerActivity.this.recreate();

                        } else if (language.equals("iw")){
                            MyApplicationClass.changeLocale(DrawerActivity.this.getResources(),"en");
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

                    case R.id.navigation_item_personal_details:
                        intent = new Intent(DrawerActivity.this,UserDetailsActivity.class);
                        startActivity(intent);
                        DrawerActivity.this.finish();
                        break;
                    case R.id.navigation_item_change_cafeteria:
                        String orderJSON = sharedPreferences.getString("order", "");
                        if (orderJSON.equals("")){ //no order exist
                            changeCafeteria();
                        } else{
                            Order order = new Gson().fromJson(orderJSON,Order.class);
                            if (order.getItems().size() > 0 || order.getMeals().size() > 0){ //if there's meals/items in order
                                emptyCartAlert();
                            } else{ //order is empty
                                changeCafeteria();
                            }
                        }

                        break;
                    default:
                        break;
                }

                return true;
            }
        });
    }

    public void logout() {
        editor.remove("customer");
        editor.apply();
        DataHolder.getInstance().setFavoriteMeals(null);
        DataHolder.getInstance().setFavoriteItems(null);

        LoginActivity.googleSignOut();

        FacebookSdk.sdkInitialize(getApplicationContext());
        if (LoginManager.getInstance() != null){
            LoginManager.getInstance().logOut();
        }

        intent = new Intent(DrawerActivity.this,LoginActivity.class);
        startActivity(intent);
        DrawerActivity.this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                Log.d("BACKPRESSED","back pressed");
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void emptyCartAlert() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.empty_cart_dialog_title))
                .setMessage(getString(R.string.empty_cart_message))
                .setPositiveButton(getString(R.string.empty_cart_postive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changeCafeteria();
                        dialog.dismiss();
                    }

                })
                .setNegativeButton(getString(R.string.empty_cart_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void changeCafeteria(){
        editor.remove("cafeteria");
        editor.remove("order");
        editor.apply();

        FacebookSdk.sdkInitialize(getApplicationContext());
        if (LoginManager.getInstance() != null){
            LoginManager.getInstance().logOut();
        }

        LoginActivity.googleSignOut();
        intent = new Intent(DrawerActivity.this,ChooseCafeteriaActivity.class);
        startActivity(intent);
        DrawerActivity.this.finish();
    }
}
