package com.cafeteria.cafeteria_client.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cafeteria.cafeteria_client.LocalDBHandler;
import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.OrderedItem;
import com.cafeteria.cafeteria_client.data.OrderedMeal;
import com.cafeteria.cafeteria_client.utils.ApplicationConstant;
import com.cafeteria.cafeteria_client.data.Order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class OrdersHistoryActivity extends DrawerActivity {

    private ListView lvOrdersHistory;
    private Currency nis;
    private List<Order> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_history);
        super.onCreateDrawer();
        getSupportActionBar().setTitle(this.getTitle());

        lvOrdersHistory = (ListView)findViewById(R.id.lvordersHistory);
        //nis symbol
        Locale israel = new Locale("iw", "IL");
        nis = Currency.getInstance(israel);
        // init Orders list
        MyApplicationClass app = (MyApplicationClass)getApplication();
        LocalDBHandler db = app.getLocalDB();
        orders = db.selectOrders();
        if(orders == null) {
            orders = new ArrayList<>();
        }
        //new HistoryListTask().execute();
        lvOrdersHistory.setAdapter(new OrdersHistoryAdapter(this,R.layout.single_order_history,orders));

        //set checked item on drawer
        navigationView.setCheckedItem(R.id.navigation_item_history);
    }

    public class OrdersHistoryAdapter extends BaseAdapter {
        private List<Order> orders;
        private Context context;
        private int layout;

        public OrdersHistoryAdapter(Context context, int layout, List<Order> orders) {
            this.context = context;
            this.layout = layout;
            this.orders = orders;
        }

        @Override
        public int getCount() {
            return orders.size();
        }

        @Override
        public Object getItem(int i) {
            return orders.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            Order order = (Order) getItem(position);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(layout, parent, false);
                holder = new ViewHolder();
                holder.tvOrderHistoryDate = (TextView) convertView.findViewById(R.id.tvOrderHistoryDate);
                holder.tvOrderHistoryPrice = (TextView) convertView.findViewById(R.id.tvOrderHistoryPrice);
                holder.tvOrderDetails = (TextView) convertView.findViewById(R.id.tvOrderDetails);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            // Date handling
            SimpleDateFormat sdf = new SimpleDateFormat(ApplicationConstant.DATE_FORMAT);
            holder.tvOrderHistoryDate.setText(sdf.format(order.getDate()));
            // Price
            BigDecimal bd = new BigDecimal(order.getPayment());
            bd = bd.setScale(1, RoundingMode.HALF_DOWN);
            holder.tvOrderHistoryPrice.setText(bd + " " + nis.getSymbol());
            // Order Details ( meals & items )
            StringBuilder details = new StringBuilder();
            if(order.getMeals().size() > 0) {
                details.append("\n");
                details.append(getResources().getString(R.string.history_meals_title));
                details.append("\n");
                for(OrderedMeal meal: order.getMeals()) {
                    details.append("* ");
                    details.append(meal.getTitle() + " ");
                    bd = new BigDecimal(meal.getTotalPrice());
                    bd = bd.setScale(1, RoundingMode.HALF_DOWN);
                    details.append(bd + " " + nis.getSymbol());
                    details.append("\n");
                    if(meal.getComment() != null && meal.getComment().length() > 0 ) {
                        details.append("  "+getResources().getString(R.string.history_extra_title)+" ");
                        details.append(meal.getComment());
                        details.append("\n");
                    }
                }
            }

            if(order.getItems().size() > 0) {
                details.append("\n");
                details.append(getResources().getString(R.string.history_items_title));
                for(OrderedItem item: order.getItems()) {
                    details.append("\n");
                    details.append("* "+item.getParentItem().getTitle());
                    details.append(" ");
                    bd = new BigDecimal(item.getParentItem().getPrice());
                    bd = bd.setScale(1, RoundingMode.HALF_DOWN);
                    details.append(bd + " " + nis.getSymbol());
                }
            }

            holder.tvOrderDetails.setText(details.toString());
            return convertView;
        }

        private class ViewHolder {
            TextView tvOrderHistoryPrice;
            TextView tvOrderHistoryDate;
            TextView tvOrderDetails;
        }

    }

    private class HistoryListTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            MyApplicationClass app = (MyApplicationClass)getApplication();
            LocalDBHandler db = app.getLocalDB();
            orders = db.selectOrders();
            if(orders == null) {
                orders = new ArrayList<>();
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent menuIntent = new Intent(OrdersHistoryActivity.this, MenuActivity.class);
        startActivity(menuIntent);
    }

}
