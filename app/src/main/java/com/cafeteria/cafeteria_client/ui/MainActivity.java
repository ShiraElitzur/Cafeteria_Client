package com.cafeteria.cafeteria_client.ui;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.Customer;
import com.cafeteria.cafeteria_client.data.OrderedItem;
import com.cafeteria.cafeteria_client.utils.DataHolder;
import com.cafeteria.cafeteria_client.data.Order;
import com.cafeteria.cafeteria_client.data.OrderedMeal;
import com.google.gson.Gson;
import com.cafeteria.cafeteria_client.interfaces.OnDialogResultListener;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Shira Elitzur on 08/09/2016.
 * <p>
 * The activity that shows the menu items. Contains three tabs, as options how to display the items:
 * 1. Sorted by categories 2. 3.
 * This activity extends the DrawerActivity to support the application navigation drawer
 */
public class MainActivity extends DrawerActivity implements OnDialogResultListener {
    private static boolean firstLaunch = true;
    private SharedPreferences sharedPreferences;
    private LinearLayout llMenu;
    private LinearLayout rlNotification;
    private TextView tvReadyOrderNumber;
    private TextView tvCafeteriaName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // The layout with the tabs
        super.onCreateDrawer();

        llMenu = (LinearLayout) findViewById(R.id.llmenu);
        CustomPagerAdapter adapter = new CustomPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        // Adding to the adapter the three fragments and their titlesAnimation
        adapter.addFragment(new CategoriesFragment(), getResources().getString(R.string.categories_tab_title));
        adapter.addFragment(new FavoritesFragment(), getResources().getString(R.string.favorites_tab_title));
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        //getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        // On MainActivity's first launch we create a new Order for this session
        if (firstLaunch) {
            firstLaunch = false;
            // get the logged customer from the shared preferences
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String order = sharedPreferences.getString("order", "");
            if (order.isEmpty()){
                DataHolder.getInstance().setTheOrder(new Order());
                DataHolder.getInstance().getTheOrder().setItems(new ArrayList<OrderedItem>());
                DataHolder.getInstance().getTheOrder().setMeals(new ArrayList<OrderedMeal>());
            } else{
                DataHolder.getInstance().setTheOrder(new Gson().fromJson(order,Order.class));
            }

        }
        rlNotification = (LinearLayout) findViewById(R.id.rlNotification);
        tvReadyOrderNumber = (TextView)findViewById(R.id.tvReadyOrderNumber);
        //ibReadyOrder = (ImageButton) findViewById(R.id.ibReadyOrder);

        rlNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,OrderReadyActivity.class);
                startActivity(intent);
            }
        });

        tvCafeteriaName = (TextView) findViewById(R.id.tvCafeteriaName);
        tvCafeteriaName.setText(getString(R.string.active_cafeteria) + " " + DataHolder.getInstance().getCafeteria().getCafeteriaName());

        navigationView.setCheckedItem(R.id.navigation_item_cafeteria_menu);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.exit_dialog_title))
                .setMessage(getString(R.string.exit_dialog_message))
                .setPositiveButton(getString(R.string.exit_dialog_postive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton(getString(R.string.exit_dialog_negative), null)
                .show();
    }

    @Override
    protected void onResume() {
//        // get ready order list from shared preferences
//        String readyOrdersString = sharedPreferences.getString("readyOrders", "");
//        List<Integer> readyOrders;
//        Type listType = new TypeToken<ArrayList<Integer>>() {
//        }.getType();
//        readyOrders = new Gson().fromJson(readyOrdersString,listType);
//
//        if( readyOrders.size() <= 0 ) {
//            //ibReadyOrder.setVisibility(View.GONE);
//            //tvReadyOrderNumber.setVisibility(View.GONE);
//            rlNotification.setVisibility(View.GONE);
//        } else {
////            ibReadyOrder.setVisibility(View.VISIBLE);
////            tvReadyOrderNumber.setVisibility(View.VISIBLE);
//            rlNotification.setVisibility(View.VISIBLE);
//            tvReadyOrderNumber.setText(readyOrders.size()+"");
//        }
        Log.e("DEBUG","Main OnResume!");
        // get ready order list from shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String customerJSON = sharedPreferences.getString("customer", "");
        Customer c = new Gson().fromJson(customerJSON, Customer.class);

        String readyOrdersForUserString = sharedPreferences.getString("readyOrdersForUsers", "");
        HashMap<Integer,List<Integer>> readyOrdersForUsers;
        if (!readyOrdersForUserString.isEmpty()){
            Type listType = new TypeToken<HashMap<Integer,List<Integer>>>() {
            }.getType();
            readyOrdersForUsers = new Gson().fromJson(readyOrdersForUserString,listType);
            List<Integer> readyOrders = readyOrdersForUsers.get(c.getId());
            if( readyOrders == null || readyOrders.size() <= 0 ) {
                //ibReadyOrder.setVisibility(View.GONE);
                //tvReadyOrderNumber.setVisibility(View.GONE);
                rlNotification.setVisibility(View.GONE);
            } else {
                tvReadyOrderNumber.setText(readyOrders.size()+"");
                rlNotification.setVisibility(View.VISIBLE);
            }
        } else {
            rlNotification.setVisibility(View.GONE);
        }
        super.onResume();
    }

    @Override
    public void onPositiveResult(OrderedMeal meal) {
        DataHolder dataHolder = DataHolder.getInstance();
        dataHolder.addMealToOrder(meal);
        updateOrderInSharedPreferences();
        Intent orderActivityIntent = new Intent(this, OrderActivity.class);
        startActivity(orderActivityIntent);
    }

    @Override
    public void onNegativeResult(OrderedMeal meal) {
        showSnackBar();

        DataHolder dataHolder = DataHolder.getInstance();
        dataHolder.addMealToOrder(meal);
        updateOrderInSharedPreferences();

    }

    private void showSnackBar() {
        Snackbar snackbar = Snackbar
                .make(llMenu, getString(R.string.dialog_btn_keep_shopping_pressed), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.snack_bar_action_text), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent orderActivityIntent = new Intent(MainActivity.this, OrderActivity.class);
                        startActivity(orderActivityIntent);
                    }
                });
        snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary));
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, android.R.color.white));
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        snackbar.show();
    }

    /**
     * Pager is the element that manages and displays the tabs
     */
    static class CustomPagerAdapter extends FragmentStatePagerAdapter {

        /**
         * List of the fragments in this pager
         */
        private final List<Fragment> fragmentList = new ArrayList<>();

        /**
         * List of titlesAnimation to display at the head of each fragment tab
         */
        private final List<String> fragmentTitleList = new ArrayList<>();


        public CustomPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Returns the fragment in a given position
         *
         * @param position
         * @return
         */
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        /**
         * Returns the amount of tabs in the pager
         *
         * @return
         */
        @Override
        public int getCount() {
            return fragmentList.size();
        }

        /**
         * Returns the page title of specific tab (by position)
         *
         * @param position
         * @return
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }

        /**
         * Adds fragment to the pager
         *
         * @param fragment
         * @param title
         */
        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }
    }

    private void updateOrderInSharedPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("order", new Gson().toJson(DataHolder.getInstance().getTheOrder()));
        editor.apply();
    }
}
