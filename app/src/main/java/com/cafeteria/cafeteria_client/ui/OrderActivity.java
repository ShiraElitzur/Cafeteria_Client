package com.cafeteria.cafeteria_client.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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

import com.cafeteria.cafeteria_client.data.DataHolder;
import com.cafeteria.cafeteria_client.interfaces.OnDialogResultListener;
import com.cafeteria.cafeteria_client.interfaces.OrderItem;
import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.Item;
import com.cafeteria.cafeteria_client.data.Meal;
import com.cafeteria.cafeteria_client.data.Order;
import com.cafeteria.cafeteria_client.data.OrderedMeal;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

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
    // temp vars
    private List<OrderedMeal> orderedMeals;
    private List<Item> orderedItems;
    /**
     * The ActionBar MenuItem that displays the amount to pay
     */
    private MenuItem itemBill;
    /**
     * The Order object that this activity displays its details
     */
    private Order order;
    // temp var
    double pay;

    /**
     * put the tow lists in one list in OrderItem type which is an interface that makes sure
     * that the classes that implements him will implement the methods that an OrderItem must
     * have here in the below adapter
     */
    List<OrderItem> items = new ArrayList<OrderItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        super.onCreateDrawer();

        // TODO: 15/09/2016 getOrder from local memory or some global class. meals & items will come with it of course
        order = new Order();

        DataHolder dataHolder = DataHolder.getInstance();
        orderedItems = dataHolder.getOrderedItems();
        orderedMeals = dataHolder.getOrderedMeals();

        order.setItems(orderedItems);
        order.setMeals(orderedMeals);

        items.addAll(orderedMeals);
        items.addAll(orderedItems);
        lvOrderItems = (ListView) findViewById(R.id.lvOrderItems);
        lvOrderItems.setAdapter(new OrderItemsAdapter(this, R.layout.single_order_item, items));
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
        BigDecimal bd = new BigDecimal(order.getPayment());
        bd = bd.setScale(1, RoundingMode.HALF_DOWN);
        itemBill.setTitle(getResources().getString(R.string.pay_amount) + " - " + bd);
        pay = order.getPayment();
        // TODO: 15/09/2016 handle press on pay item. move to payment screen
        return super.onCreateOptionsMenu(menu);
    }

    public void addMeal(OrderedMeal meal) {
        items.add(meal);
    }

    // temp method
    private void initMeals() {

    }

    @Override
    public void onPositiveResult(OrderedMeal meal) {

    }

    @Override
    public void onNegativeResult(OrderedMeal meal) {

    }

    /**
     * This inner class adapts between the ListView of the items and the actual list of <OrderItem>
     * OrderItem is an interface that with it we force on the items classes to implement the method we use here
     * getTitle() and getPrice()
     *
     * @param <Item>
     */
    private class OrderItemsAdapter<Item> extends ArrayAdapter<OrderItem> {

        private int layout;
        private List<OrderItem> items;

        /**
         * @param context the ui context
         * @param layout  the layout for one row
         * @param items   the list of the data
         */
        public OrderItemsAdapter(Context context, int layout, List<OrderItem> items) {
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
                holder.imgBtnEditItem = (ImageButton) convertView.findViewById(R.id.imgBtnEditItem);
                holder.imgBtnEditItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentManager fm = getSupportFragmentManager();
                        MealDetailsDialog mealDetailsDialog = new MealDetailsDialog();
                        Bundle args = new Bundle();
                        args.putSerializable("orderedMeal", items.get(position));
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
                                                pay -= items.get(position).getPrice();
                                                // Remove the item from the adapter
                                                OrderItemsAdapter.this.remove(items.get(position));
                                                // Refresh the UI
                                                OrderActivity.this.findViewById(android.R.id.content).getRootView().invalidate();
                                                itemBill.setTitle(getResources().getString(R.string.pay_amount) + " - " + pay);
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
            holder.tvOrderItemPrice.setText(bd + "");

            return convertView;
        }

        private class ViewHolder {
            TextView tvOrderItemTitle;
            ImageButton imgBtnRemoveItem;
            TextView tvOrderItemPrice;
            ImageButton imgBtnEditItem;
        }


    }
}
