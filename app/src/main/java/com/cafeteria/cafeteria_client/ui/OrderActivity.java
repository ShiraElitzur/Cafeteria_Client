package com.cafeteria.cafeteria_client.ui;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cafeteria.cafeteria_client.data.Cafeteria;
import com.cafeteria.cafeteria_client.utils.LocalDBHandler;
import com.cafeteria.cafeteria_client.data.Customer;
import com.cafeteria.cafeteria_client.data.OrderedItem;
import com.cafeteria.cafeteria_client.utils.ApplicationConstant;
import com.cafeteria.cafeteria_client.utils.DataHolder;
import com.cafeteria.cafeteria_client.interfaces.OnDialogResultListener;
import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.Item;
import com.cafeteria.cafeteria_client.data.Order;
import com.cafeteria.cafeteria_client.data.OrderedMeal;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

/**
 * This activity displays the order items and meals ( both referred as item in this file ) and the amount for payment.
 *
 * @author Shira Elitzur
 */
public class OrderActivity extends DrawerActivity implements OnDialogResultListener {

    private static final int ORDER_REQ = 1;

    /**
     * The UI list that displays the items
     */
    private ListView lvOrderItems;
    private ListView lvOrderMeals;
    private Currency nis;
    private FloatingActionButton fabPay;
    // temp vars
    private List<OrderedMeal> orderedMeals;
    private List<Item> orderedItems;
    private ArrayAdapter mealsAdapter;
    private ArrayAdapter itemsAdapter;
    private Cafeteria cafeteria;
    private LinearLayout llOrderInProgress;
    private RelativeLayout rlPayList;
    private boolean showMenu = true;
    private TextView tvPayment;
    private Customer c;
    private View parentLayout;

    private MenuItem itemClear;
    /**
     * The ActionBar MenuItem that displays the amount to pay
     */
//    private MenuItem itemBill;
    /**
     * If pickup time is chosen, this display the selected time
     */
    private MenuItem itemTIme;

    /**
     * The Order object that this activity displays its details
     */
    private Order order;
    private View layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        layout = (View) findViewById(R.id.llOrderLayout);
        super.onCreateDrawer();
        Log.e("DEBUG","On Create Order Activity");

        parentLayout = findViewById(R.id.layout);

        //nis symbol
        Locale israel = new Locale("iw", "IL");
        nis = Currency.getInstance(israel);

        DataHolder dataHolder = DataHolder.getInstance();
        order = dataHolder.getTheOrder();
        lvOrderItems = (ListView) findViewById(R.id.lvOrderItems);
        lvOrderMeals = (ListView) findViewById(R.id.lvOrderMeals);
        List<QuantityItem> quantityItems = new ArrayList<>();
        for (OrderedItem it: order.getItems()){
            QuantityItem toBeAdded = new QuantityItem();
            toBeAdded.setParentItem(new Item());
            toBeAdded.getParentItem().setTitle(it.getParentItem().getTitle());
            toBeAdded.getParentItem().setId(it.getParentItem().getId());
            toBeAdded.getParentItem().setPrice(it.getParentItem().getPrice());

            if (quantityItems.contains(toBeAdded)){
                for (QuantityItem ot: quantityItems){
                    if (ot.getParentItem().getId() == toBeAdded.getParentItem().getId()){
                        int qty = ot.getQty();
                        qty++;
                        ot.setQty(qty);
                    }
                }
            }else{
                quantityItems.add(toBeAdded);
            }

        }


        if( order.getItems() == null || order.getItems().size() < 1 ) {
            Log.e("DEBUG","EMPTY ITEMS");
            if( order.getMeals() == null || order.getMeals().size() < 1 ) {
                findViewById(R.id.tvEmptyList).setVisibility(View.VISIBLE);
                Log.e("DEBUG","EMPTY MEALS");
            }

        } else {
            findViewById(R.id.tvEmptyList).setVisibility(View.GONE);
        }

        lvOrderItems.setAdapter(itemsAdapter = new OrderItemsAdapter(this, R.layout.single_order_item, quantityItems));
        lvOrderMeals.setAdapter(mealsAdapter = new OrderMealsAdapter(this, R.layout.single_order_item, order.getMeals()));

        fabPay = (FloatingActionButton) findViewById(R.id.fabPay);
        fabPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order.getItems().size() == 0 && order.getMeals().size() == 0) {
                    Toast.makeText(OrderActivity.this, getResources().getString(R.string.empty_cart), Toast.LENGTH_LONG).show();
                } else {

                    DataHolder data = DataHolder.getInstance();
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    //Gson gson = new Gson();
                    Gson gson=  new GsonBuilder().setDateFormat(ApplicationConstant.DATE_TIME_FORMAT).create();

                    String customerJSON = sharedPreferences.getString("customer", "");
                    c = gson.fromJson(customerJSON, Customer.class);
                    data.getTheOrder().setCustomer(c);
                    data.getTheOrder().setDate(Calendar.getInstance().getTime());


                    Intent payPalIntent = new Intent(OrderActivity.this, PayPalActivity.class);
                    payPalIntent.putExtra("language", MyApplicationClass.language);
                    payPalIntent.putExtra("order", order);
                    startActivityForResult(payPalIntent,ORDER_REQ);

                }
            }
        });

        //set checked item on drawer
        navigationView.setCheckedItem(R.id.navigation_item_cart);

        Log.e("CAFETERIA", DataHolder.getInstance().getCafeteria().getOpeningHoursStart().get(Calendar.HOUR_OF_DAY) + ":" + DataHolder.getInstance().getCafeteria().getOpeningHoursStart().get(Calendar.MINUTE));
        Log.e("CAFETERIA", DataHolder.getInstance().getCafeteria().getOpeningHoursEnd().get(Calendar.HOUR_OF_DAY) + ":" + DataHolder.getInstance().getCafeteria().getOpeningHoursEnd().get(Calendar.MINUTE));

        rlPayList = (RelativeLayout)findViewById(R.id.rlPayList);
        llOrderInProgress = (LinearLayout)findViewById(R.id.llOrderInProgress);
        ImageView animationTarget = (ImageView) this.findViewById(R.id.ivFirstAnim);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate_around_center_point);
        animationTarget.startAnimation(animation);
        animationTarget = (ImageView) this.findViewById(R.id.ivSecondAnim);
        animation = AnimationUtils.loadAnimation(this, R.anim.rotate_around_center_point);
        animationTarget.startAnimation(animation);

        tvPayment = (TextView) findViewById(R.id.tvPayment);
        BigDecimal bd = new BigDecimal(order.getPayment());
        bd = bd.setScale(1, RoundingMode.HALF_DOWN);
        tvPayment.setText(getResources().getString(R.string.pay_amount) + " - " + bd + " " + nis.getSymbol());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ORDER_REQ)
        {
            if(resultCode == RESULT_OK)
            {
                Snackbar snackbar = Snackbar
                        .make(parentLayout, getString(R.string.paypal_payment_success), Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(OrderActivity.this, android.R.color.white));
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.BLACK);
                snackbar.show();
                snackbar.setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        finish();
                        Intent homeActivity = new Intent(OrderActivity.this,MainActivity.class);
                        startActivity(homeActivity);
                    }
                });


                Calendar calendar = Calendar.getInstance();
                Log.e("DATE",calendar.getTime().toString());
                order.setDate(calendar.getTime());
                MyApplicationClass app = (MyApplicationClass)getApplication();
                LocalDBHandler db = app.getLocalDB();
                db.insertOrder(c.getId(),order);
                saveOrderInLocalStorage();
                new SendOrderToServer().execute();

                DataHolder.getInstance().setTheOrder(new Order());
                DataHolder.getInstance().getTheOrder().setItems(new ArrayList<OrderedItem>());
                DataHolder.getInstance().getTheOrder().setMeals(new ArrayList<OrderedMeal>());
                SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = mySPrefs.edit();
                editor.remove("order");
                editor.apply();

                llOrderInProgress.setVisibility(View.VISIBLE);
                rlPayList.setVisibility(View.INVISIBLE);
                showMenu = false;
                invalidateOptionsMenu();
//                finish();
//                Intent homeActivity = new Intent(OrderActivity.this,MainActivity.class);
//                startActivity(homeActivity);

            }
        }
    }

    public void saveOrderInLocalStorage() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        String payedOrdersString = sharedPreferences.getString("payedOrders", "");
//        List<Order> payedOrders;
//        if (payedOrdersString.isEmpty()){
//            payedOrders = new ArrayList<>();
//        } else{
//            Type listType = new TypeToken<ArrayList<Order>>() {
//            }.getType();
//            payedOrders = new Gson().fromJson(payedOrdersString,listType);
//        }
//
//        if( order != null ) {
//            Log.e("DEBUG","order ref - " + order);
//            payedOrders.add(order);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putString("payedOrders", new Gson().toJson(payedOrders));
//            editor.apply();
//        }
        String payedOrdersCounterString = sharedPreferences.getString("payedOrdersCounter","");
        int counter;
        if( payedOrdersCounterString.isEmpty()) {
            counter = 1;
        } else {
            counter = Integer.parseInt(payedOrdersCounterString);
            counter++;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("payedOrdersCounter", counter+"");
        editor.apply();
    }

    private class SendOrderToServer extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            boolean result = false;
            Log.e("DEBUG","inside do in background of SendOrderToServer");

            // Request - send the order as json to the server for insertion
            Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy HH:mm:ss.SSSZ").create();
            String jsonOrder = gson.toJson(order, Order.class);
            URL url = null;
            try {
                url = new URL(ApplicationConstant.getAddress(ApplicationConstant.SEND_ORDER));
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestProperty("Content-Type", "text/plain");
                con.setRequestProperty("Accept", "text/plain");
                con.setRequestMethod("POST");

                OutputStream os = con.getOutputStream();
                os.write(jsonOrder.getBytes("UTF-8"));
                os.flush();

                if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                // Response
                StringBuilder response = new StringBuilder();
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));

                String line;
                while ((line = input.readLine()) != null) {
                    response.append(line + "\n");
                }

                input.close();

                con.disconnect();

                if (response.toString().trim().equals("OK")) {
                    result = true;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }  catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else {
            finish();
            Intent menuIntent = new Intent(OrderActivity.this, MainActivity.class);
            startActivity(menuIntent);
        }
    }

    /**
     * The action bar menu behavior in this activity - i am adding a menu to it with the amount to pay
     * and action that send the user to payment screen
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if( showMenu ) {
            getMenuInflater().inflate(R.menu.order_items_menu, menu);
//            itemBill = (MenuItem) menu.findItem(R.id.itemBill);
            itemClear = (MenuItem) menu.findItem(R.id.itemClear);
            itemTIme = (MenuItem) menu.findItem(R.id.itemTIme);
//            BigDecimal bd = new BigDecimal(order.getPayment());
//            bd = bd.setScale(1, RoundingMode.HALF_DOWN);
//            itemBill.setTitle(getResources().getString(R.string.pay_amount) + " - " + bd + " " + nis.getSymbol());
            itemTIme.setTitle(order.getPickupTime());
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemClear:
                clearOrder();
                return true;
            case R.id.itemTimeAction:
                final Calendar currentTime = Calendar.getInstance();
                final int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                final int minute = currentTime.get(Calendar.MINUTE);
                TimePickerDialog timePicker;

                timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        if (selectedHour < DataHolder.getInstance().getCafeteria().getOpeningHoursStart().get(Calendar.HOUR_OF_DAY)
                                || (selectedHour == DataHolder.getInstance().getCafeteria().getOpeningHoursStart().get(Calendar.HOUR_OF_DAY)
                                && selectedMinute < DataHolder.getInstance().getCafeteria().getOpeningHoursStart().get(Calendar.MINUTE))){
                            Toast.makeText(OrderActivity.this, DataHolder.getInstance().getCafeteria().getCafeteriaName() + " " +
                                    getResources().getString(R.string.cafeteria_not_open) + " " +
                                    DataHolder.getInstance().getCafeteria().getOpeningHoursStart().get(Calendar.HOUR_OF_DAY)
                                    + ":" + DataHolder.getInstance().getCafeteria().getOpeningHoursStart().get(Calendar.MINUTE), Toast.LENGTH_LONG).show();
                            order.setPickupTime("");
                            itemTIme.setTitle(order.getPickupTime());
                        } else if (selectedHour > DataHolder.getInstance().getCafeteria().getOpeningHoursEnd().get(Calendar.HOUR_OF_DAY)
                                || (selectedHour == DataHolder.getInstance().getCafeteria().getOpeningHoursEnd().get(Calendar.HOUR_OF_DAY)
                                && selectedMinute > DataHolder.getInstance().getCafeteria().getOpeningHoursEnd().get(Calendar.MINUTE))){
                            Toast.makeText(OrderActivity.this, DataHolder.getInstance().getCafeteria().getCafeteriaName() + " " +
                                    getResources().getString(R.string.cafeteria_closed), Toast.LENGTH_LONG).show();
                            order.setPickupTime("");
                            itemTIme.setTitle(order.getPickupTime());
                        }
                        else if (selectedHour < hour || (selectedHour == hour && selectedMinute < minute)) {
                            Toast.makeText(OrderActivity.this,
                                    getResources().getString(R.string.dialog_time_picker_time_passed), Toast.LENGTH_LONG).show();
                            order.setPickupTime("");
                            itemTIme.setTitle(order.getPickupTime());
                        } else if (selectedHour == hour && selectedMinute < minute + 15) {
                            Toast.makeText(OrderActivity.this,
                                    getResources().getString(R.string.dialog_time_picker_limit_time), Toast.LENGTH_LONG).show();
                            order.setPickupTime("");
                            itemTIme.setTitle(order.getPickupTime());
                        } else {
                            String time = "";
                            if (selectedHour < 10){
                                time += '0' + String.valueOf(selectedHour);
                            } else{
                                time += String.valueOf(selectedHour);
                            }
                            time += ":";
                            if (selectedMinute < 10){
                                time += '0' + String.valueOf(selectedMinute);
                            }else{
                                time += String.valueOf(selectedMinute);
                            }
                            order.setPickupTime(time);
                            itemTIme.setTitle(order.getPickupTime());
                        }

                    }
                },hour, minute, true);//Yes 24 hour time
                timePicker.setTitle(getResources().getString(R.string.dialog_time_picker_title));
                timePicker.show();

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * This inner class adapts between the ListView of the items and the actual list of <OrderItem>
     * OrderItem is an interface that with it we force on the items classes to implement the method we use here
     * getTitle() and getPrice()
     *
     * @param <T>
     */
    private class OrderItemsAdapter<T> extends ArrayAdapter<QuantityItem> {

        private int layout;
        private List<QuantityItem> items;

        /**
         * @param context the ui context
         * @param layout  the layout for one row
         * @param items   the list of the data
         */
        public OrderItemsAdapter(Context context, int layout, List<QuantityItem> items) {
            super(context, layout, items);
            this.layout = layout;
            this.items = items;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            // in the creation of the listView rows i assign the components to vars
            if (convertView == null) {

                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(layout, parent, false);

                holder = new ViewHolder();
                holder.tvOrderItemTitle = (TextView) convertView.findViewById(R.id.tvOrderItemTitle);
                holder.tvOrderItemPrice = (TextView) convertView.findViewById(R.id.tvOrderItemPrice);
                holder.tvQty = (TextView) convertView.findViewById(R.id.tvQty);
                holder.imgBtnRemoveItem = (ImageButton) convertView.findViewById(R.id.imgBtnRemoveItem);
                holder.imgBtnRemoveItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderActivity.this);
                        alertDialogBuilder.setTitle(R.string.alert_dialog_delete_item_title)
                                .setCancelable(false)
                                .setPositiveButton(R.string.alert_dialog_delete_item_positive_button,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                order.setPayment(order.getPayment() - items.get(position).getParentItem().getPrice());
                                                if (items.get(position).getQty() == 1) {
                                                    // Remove the item from the adapter
                                                    removeItemFromList(items.get(position));
                                                    OrderItemsAdapter.this.remove(items.get(position));
                                                    updateOrderInSharedPreferences();
                                                    if( items.size() == 0 && order.getMeals().size() == 0) {
                                                        findViewById(R.id.tvEmptyList).setVisibility(View.VISIBLE);
                                                    }
                                                    lvOrderItems.invalidateViews();
                                                } else{
                                                    items.get(position).setQty((items.get(position).getQty())-1);
                                                    removeItemFromList(items.get(position));
                                                    lvOrderItems.invalidateViews();
                                                }
                                                // Refresh the UI
                                                //itemBill.setTitle(getResources().getString(R.string.pay_amount) + " - " + order.getPayment() + " " + nis.getSymbol());
                                                tvPayment.setText(getResources().getString(R.string.pay_amount) + " - " + order.getPayment() + " " + nis.getSymbol());

                                            }
                                        })
                                .setNegativeButton(R.string.alert_dialog_delete_item_negative_button,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        }).create().show();
                    }

                });
                // keep as tag of the row the vars that match its components
                convertView.setTag(holder);
            } else { // if it is not the creation of the row we take to components from the row's tag
                holder = (ViewHolder) convertView.getTag();
            }
            // in both cases sets the ui data to fit the item data
            BigDecimal bd = new BigDecimal(items.get(position).getParentItem().getPrice());
            bd = bd.setScale(1, RoundingMode.HALF_DOWN);

            holder.tvOrderItemTitle.setText(items.get(position).getParentItem().getTitle());
            holder.tvOrderItemPrice.setText(bd + " " + nis.getSymbol());
            holder.tvQty.setText(""+items.get(position).getQty());

            return convertView;
        }

        private void removeItemFromList(QuantityItem item) {
            OrderedItem toDelete = (OrderedItem) item;
            for (OrderedItem it: order.getItems()){
                if (it.getId() == item.getId()){
                    toDelete = it;
                    break;
                }
            }

            order.getItems().remove(toDelete);
        }

        private class ViewHolder {
            TextView tvOrderItemTitle;
            ImageButton imgBtnRemoveItem;
            TextView tvOrderItemPrice;
            ImageButton imgBtnEditItem;
            TextView tvQty;
        }

    }

    private class OrderMealsAdapter<T> extends ArrayAdapter<OrderedMeal> {

        private int layout;
        private List<OrderedMeal> meals;

        /**
         * @param context the ui context
         * @param layout  the layout for one row
         * @param meals   the list of the data
         */
        public OrderMealsAdapter(Context context, int layout, List<OrderedMeal> meals) {
            super(context, layout, meals);
            this.layout = layout;
            this.meals = meals;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            // in the creation of the listView rows i assign the components to vars
            if (convertView == null) {

                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(layout, parent, false);

                holder = new ViewHolder();
                holder.tvOrderItemTitle = (TextView) convertView.findViewById(R.id.tvOrderItemTitle);
                holder.tvOrderItemPrice = (TextView) convertView.findViewById(R.id.tvOrderItemPrice);
                holder.imgBtnRemoveItem = (ImageButton) convertView.findViewById(R.id.imgBtnRemoveItem);
                holder.imgBtnEditItem = (ImageButton) convertView.findViewById(R.id.imgBtnEditItem);
                holder.imgBtnEditItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentManager fm = getSupportFragmentManager();
                        MealDetailsDialog mealDetailsDialog = new MealDetailsDialog();
                        Bundle args = new Bundle();
                        args.putSerializable("meal", meals.get(position));
                        mealDetailsDialog.setArguments(args);
                        mealDetailsDialog.show(fm, "");
                    }
                });
                holder.imgBtnRemoveItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderActivity.this);
                        alertDialogBuilder.setTitle(R.string.alert_dialog_delete_item_title)
                                .setCancelable(false)
                                .setPositiveButton(R.string.alert_dialog_delete_item_positive_button,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                double payChange = meals.get(position).getTotalPrice();
                                                order.setPayment(order.getPayment() - payChange);
                                                // Remove the item from the adapter
                                                OrderMealsAdapter.this.remove(meals.get(position));
                                                updateOrderInSharedPreferences();
                                                if( meals.size() == 0 && order.getItems().size() == 0 ) {
                                                    findViewById(R.id.tvEmptyList).setVisibility(View.VISIBLE);
                                                }
                                                // Refresh the UI
                                                OrderActivity.this.findViewById(android.R.id.content).getRootView().invalidate();
                                                //itemBill.setTitle(getResources().getString(R.string.pay_amount) + " - " + order.getPayment() + " " + nis.getSymbol());
                                                tvPayment.setText(getResources().getString(R.string.pay_amount) + " - " + order.getPayment() + " " + nis.getSymbol());

                                            }
                                        })
                                .setNegativeButton(R.string.alert_dialog_delete_item_negative_button,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        }).create().show();
                    }
                });
                // keep as tag of the row the vars that match its components
                convertView.setTag(holder);
            } else { // if it is not the creation of the row we take to components from the row's tag
                holder = (ViewHolder) convertView.getTag();
            }
            // in both cases sets the ui data to fit the item data
            double mealPrice = meals.get(position).getTotalPrice();
            BigDecimal bd = new BigDecimal(mealPrice);
            bd = bd.setScale(1, RoundingMode.HALF_DOWN);

            holder.tvOrderItemTitle.setText(meals.get(position).getParentMeal().getTitle());
            holder.tvOrderItemPrice.setText(bd + " " + nis.getSymbol());
            holder.imgBtnEditItem.setVisibility(View.VISIBLE);
            return convertView;
        }

        private class ViewHolder {
            TextView tvOrderItemTitle;
            ImageButton imgBtnRemoveItem;
            TextView tvOrderItemPrice;
            ImageButton imgBtnEditItem;
        }

    }

    // On the edit meal dialog - update meal and keep shopping buttons leads here
    // update click
    @Override
    public void onPositiveResult(OrderedMeal meal) {
        Toast.makeText(this, getString(R.string.succesful_edit_meal), Toast.LENGTH_SHORT).show();
        DataHolder dataHolder = DataHolder.getInstance();
        dataHolder.getTheOrder().getMeals().remove(meal);
        dataHolder.addMealToOrder(meal);
        updateOrderInSharedPreferences();

        mealsAdapter.notifyDataSetChanged();
//        itemBill.setTitle(getResources().getString(R.string.pay_amount) + " - " + order.getPayment() + " " + nis.getSymbol());
        tvPayment.setText(getResources().getString(R.string.pay_amount) + " - " + order.getPayment() + " " + nis.getSymbol());

    }

    // keep shopping click
    @Override
    public void onNegativeResult(OrderedMeal meal) {
        Toast.makeText(this, getString(R.string.succesful_edit_meal), Toast.LENGTH_SHORT).show();
        DataHolder dataHolder = DataHolder.getInstance();
        dataHolder.getTheOrder().getMeals().remove(meal);
        dataHolder.addMealToOrder(meal);
        updateOrderInSharedPreferences();

        Intent menuActivityIntent = new Intent(OrderActivity.this, MainActivity.class);
        startActivity(menuActivityIntent);
        finish();
    }

    private class SaveOrderLocallyTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            MyApplicationClass app = (MyApplicationClass)getApplication();
            LocalDBHandler db = app.getLocalDB();
            db.insertOrder(c.getId(),order);
            return null;
        }
    }

    private class QuantityItem extends OrderedItem{
        int qty = 1;

        public int getQty() {
            return qty;
        }

        public void setQty(int qty) {
            this.qty = qty;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            QuantityItem that = (QuantityItem) o;

            return getParentItem().getId() == (that.getParentItem().getId());

        }

        @Override
        public int hashCode() {
            return getId();
        }
    }

    public void clearOrder() {
        mealsAdapter.clear();
        mealsAdapter.notifyDataSetChanged();
        itemsAdapter.clear();
        itemsAdapter.notifyDataSetChanged();
        DataHolder.getInstance().setTheOrder(new Order());
        DataHolder.getInstance().getTheOrder().setItems(new ArrayList<OrderedItem>());
        DataHolder.getInstance().getTheOrder().setMeals(new ArrayList<OrderedMeal>());
        order = DataHolder.getInstance().getTheOrder();
        findViewById(R.id.tvEmptyList).setVisibility(View.VISIBLE);
        updateOrderInSharedPreferences();
        tvPayment.setText(getResources().getString(R.string.pay_amount) + " - " + "0.0" + " " + nis.getSymbol());
    }

    private void updateOrderInSharedPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("order", new Gson().toJson(DataHolder.getInstance().getTheOrder()));
        editor.apply();
    }

}