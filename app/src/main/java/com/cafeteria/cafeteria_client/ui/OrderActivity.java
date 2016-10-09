package com.cafeteria.cafeteria_client.ui;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cafeteria.cafeteria_client.data.Customer;
import com.cafeteria.cafeteria_client.utils.DataHolder;
import com.cafeteria.cafeteria_client.utils.LocaleHelper;
import com.cafeteria.cafeteria_client.interfaces.OnDialogResultListener;
import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.Item;
import com.cafeteria.cafeteria_client.data.Order;
import com.cafeteria.cafeteria_client.data.OrderedMeal;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    ArrayAdapter mealsAdapter;

    /**
     * The ActionBar MenuItem that displays the amount to pay
     */
    private MenuItem itemBill;
    /**
     * If pickup time is chosen, this display the selected time
     */
    private MenuItem itemTIme;

    /**
     * The Order object that this activity displays its details
     */
    private Order order;
    View layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        layout = (View) findViewById(R.id.llOrderLayout);
        super.onCreateDrawer();

        //nis symbol
        Locale israel = new Locale("iw", "IL");
        nis = Currency.getInstance(israel);

        DataHolder dataHolder = DataHolder.getInstance();
        order = dataHolder.getTheOrder();
        lvOrderItems = (ListView) findViewById(R.id.lvOrderItems);
        lvOrderMeals = (ListView) findViewById(R.id.lvOrderMeals);

        lvOrderItems.setAdapter(new OrderItemsAdapter(this, R.layout.single_order_item, order.getItems()));
        lvOrderMeals.setAdapter(mealsAdapter = new OrderMealsAdapter(this, R.layout.single_order_item, order.getMeals()));

        fabPay = (FloatingActionButton) findViewById(R.id.fabPay);
        fabPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order.getPayment() == 0) {
                    Toast.makeText(OrderActivity.this, getResources().getString(R.string.empty_cart), Toast.LENGTH_LONG).show();
                } else {

                    Intent payPalIntent = new Intent(OrderActivity.this, PayPalActivity.class);
                    payPalIntent.putExtra("language", LocaleHelper.getLanguage(OrderActivity.this));
                    payPalIntent.putExtra("order", order);
                    startActivity(payPalIntent);

                    // here, before clearing the order we need to save the order
                    DataHolder data = DataHolder.getInstance();
                    data.getTheOrder().setPaid(true);
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    Gson gson = new Gson();
                    String customerJSON = sharedPreferences.getString("customer", "");
                    Customer c = gson.fromJson(customerJSON, Customer.class);
                    data.getTheOrder().setCustomer(c);
                    data.getTheOrder().setCalendar(Calendar.getInstance());

                    DataHolder.getInstance().setTheOrder(new Order());
                    DataHolder.getInstance().getTheOrder().setItems(new ArrayList<Item>());
                    DataHolder.getInstance().getTheOrder().setMeals(new ArrayList<OrderedMeal>());
                    OrderActivity.this.recreate();
                }
            }
        });

    }


    @Override
    public void onBackPressed() {
        finish();
        Intent menuActivtiyInent = new Intent(OrderActivity.this, MenuActivity.class);
        startActivity(menuActivtiyInent);
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
        getMenuInflater().inflate(R.menu.order_items_menu, menu);
        itemBill = (MenuItem) menu.findItem(R.id.itemBill);
        itemTIme = (MenuItem) menu.findItem(R.id.itemTIme);
        BigDecimal bd = new BigDecimal(order.getPayment());
        bd = bd.setScale(1, RoundingMode.HALF_DOWN);
        itemBill.setTitle(getResources().getString(R.string.pay_amount) + " - " + bd + " " + nis.getSymbol());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemTimeAction:
                final Calendar currentTime = Calendar.getInstance();
                final int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                final int minute = currentTime.get(Calendar.MINUTE);
                TimePickerDialog timePicker;

                timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        if (selectedHour < hour || (selectedHour == hour && selectedMinute < minute)) {
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
                            order.setPickupTime(selectedHour
                                    + ":" + selectedMinute);
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
private class OrderItemsAdapter<T> extends ArrayAdapter<Item> {

    private int layout;
    private List<Item> items;

    /**
     * @param context the ui context
     * @param layout  the layout for one row
     * @param items   the list of the data
     */
    public OrderItemsAdapter(Context context, int layout, List<Item> items) {
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
                                            order.setPayment(order.getPayment() - items.get(position).getPrice());
                                            // Remove the item from the adapter
                                            OrderItemsAdapter.this.remove(items.get(position));
                                            // Refresh the UI
                                            itemBill.setTitle(getResources().getString(R.string.pay_amount) + " - " + order.getPayment() + " " + nis.getSymbol());
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
        BigDecimal bd = new BigDecimal(items.get(position).getPrice());
        bd = bd.setScale(1, RoundingMode.HALF_DOWN);

        holder.tvOrderItemTitle.setText(items.get(position).getTitle());
        holder.tvOrderItemPrice.setText(bd + " " + nis.getSymbol());

        return convertView;
    }

    private class ViewHolder {
        TextView tvOrderItemTitle;
        ImageButton imgBtnRemoveItem;
        TextView tvOrderItemPrice;
        ImageButton imgBtnEditItem;
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
                                            // Refresh the UI
                                            OrderActivity.this.findViewById(android.R.id.content).getRootView().invalidate();
                                            itemBill.setTitle(getResources().getString(R.string.pay_amount) + " - " + order.getPayment() + " " + nis.getSymbol());
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
        dataHolder.getTheOrder().getMeals().add(meal);

        mealsAdapter.notifyDataSetChanged();
        itemBill.setTitle(getResources().getString(R.string.pay_amount) + " - " + order.getPayment() + " " + nis.getSymbol());

    }

    // keep shopping click
    @Override
    public void onNegativeResult(OrderedMeal meal) {
        Toast.makeText(this, getString(R.string.succesful_edit_meal), Toast.LENGTH_SHORT).show();
        DataHolder dataHolder = DataHolder.getInstance();
        dataHolder.getTheOrder().getMeals().remove(meal);
        dataHolder.getTheOrder().getMeals().add(meal);

        Intent menuActivityIntent = new Intent(OrderActivity.this, MenuActivity.class);
        startActivity(menuActivityIntent);
        finish();
    }

}
