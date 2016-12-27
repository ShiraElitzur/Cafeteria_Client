package com.cafeteria.cafeteria_client.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.cafeteria.cafeteria_client.data.Customer;
import com.cafeteria.cafeteria_client.utils.LocalDBHandler;
import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.OrderedItem;
import com.cafeteria.cafeteria_client.data.OrderedMeal;
import com.cafeteria.cafeteria_client.utils.ApplicationConstant;
import com.cafeteria.cafeteria_client.data.Order;
import com.google.gson.Gson;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class OrdersHistoryActivity extends DrawerActivity implements DatePickerDialog.OnDateSetListener{

    private ListView lvOrdersHistory;
    private Currency nis;
    private List<Order> orders;
    private TextView tvDate;
    private Button btnPickDate;
    private LocalDBHandler db;
    private OrdersHistoryAdapter ordersHistoryAdapter;
    private Customer customer;
    private GraphView graph;
    private HashMap<String,Double> hashPayments;
    String[] months = new String[]
    {
        "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_history);
        super.onCreateDrawer();
        getSupportActionBar().setTitle(this.getTitle());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson=  new Gson();
        String customerJSON = sharedPreferences.getString("customer", "");
        customer = gson.fromJson(customerJSON, Customer.class);

        lvOrdersHistory = (ListView)findViewById(R.id.lvordersHistory);
        //nis symbol
        Locale israel = new Locale("iw", "IL");
        nis = Currency.getInstance(israel);
        // init Orders list
        MyApplicationClass app = (MyApplicationClass)getApplication();
        db = app.getLocalDB();
        orders = db.selectLastOrders(customer.getId());

        if(orders == null) {
            orders = new ArrayList<>();
        }
        //new HistoryListTask().execute();
        if( orders.size() < 1 ) {
            findViewById(R.id.tvEmptyList).setVisibility(View.VISIBLE);
        }
        ordersHistoryAdapter = new OrdersHistoryAdapter(this,R.layout.single_order_history,orders);
        lvOrdersHistory.setAdapter(ordersHistoryAdapter);


        //set checked item on drawer
        navigationView.setCheckedItem(R.id.navigation_item_history);

        btnPickDate = (Button) findViewById(R.id.btnPickDate);
        btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                        OrdersHistoryActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.setStartTitle(getString(R.string.history_start_date));
                datePickerDialog.setEndTitle(getString(R.string.history_end_date));
                datePickerDialog.show(getFragmentManager(),"Date Picker Dialog");
            }
        });

        tvDate = (TextView) findViewById(R.id.tvDate);
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        findViewById(R.id.ibShowGraph).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( graph.getVisibility() == View.GONE ) {
                    graph.setVisibility(View.VISIBLE);
                } else {
                    graph.setVisibility(View.GONE);
                }
            }
        });

        graph = (GraphView)findViewById(R.id.graph);
        new GetGraphDataFromSQLiteTask().execute();

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        String dateStartTxt = dayOfMonth + "/"  + (monthOfYear+1) + "/" + yearEnd;
        String dateEndTxt = dayOfMonthEnd + "/" + (monthOfYearEnd+1) + "/" + yearEnd;
        tvDate.setText(getResources().getString(R.string.history_show_date) + "\n" +
                dateStartTxt + " - " + dateEndTxt);
        Calendar dateStart = Calendar.getInstance();
        Calendar dateEnd = Calendar.getInstance();
        dateStart.set(Calendar.YEAR,year);
        dateStart.set(Calendar.MONTH,monthOfYear);
        dateStart.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        dateStart.set(Calendar.HOUR_OF_DAY,0);
        dateStart.set(Calendar.MINUTE,0);
        dateStart.set(Calendar.SECOND,0);
        dateStart.set(Calendar.MILLISECOND,0);

        dateEnd.set(Calendar.YEAR,yearEnd);
        dateEnd.set(Calendar.MONTH,monthOfYearEnd);
        dateEnd.set(Calendar.DAY_OF_MONTH,dayOfMonthEnd);
        dateEnd.set(Calendar.HOUR_OF_DAY,0);
        dateEnd.set(Calendar.MINUTE,0);
        dateEnd.set(Calendar.SECOND,0);
        dateEnd.set(Calendar.MILLISECOND,0);

        orders.clear();
        orders.addAll(db.selectOrdersByDate(customer.getId(),dateStart,dateEnd));
        if (orders!=null) {
            Log.e("ORDERS", "ORDERS SIZE: " + orders.size());
        }
        ordersHistoryAdapter.notifyDataSetChanged();

    }

    private class GetGraphDataFromSQLiteTask extends AsyncTask<Void,Void,Void> {

        List<Integer> mons;
        DataPoint[] dataPoints;
        BarGraphSeries<DataPoint> series;
        String[] labels;

        @Override
        protected void onPreExecute() {
            Calendar calendar = Calendar.getInstance();
            mons = new ArrayList<>(5);
            for( int i = 0; i < 5; i++ ) {
                mons.add(calendar.getTime().getMonth());
                calendar.add(Calendar.MONTH, -1);
            }

            graph.setTitle(getResources().getString(R.string.graph_title));

            dataPoints = new DataPoint[5];

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for( int i = 0,k=4; i < 5; i++,k-- ) {
                double pay = db.getPaymentForMonth( mons.get(k),customer.getId());
                dataPoints[i] = new DataPoint(i,pay);
            }

            series = new BarGraphSeries<>(dataPoints);
            labels = new String[5];
            for( int i = 0, k = 4; i < 5 ; i++, k--) {
                labels[i] = months[mons.get(k)];
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {


            graph.addSeries(series);
//        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
            graph.getGridLabelRenderer().setNumHorizontalLabels(5); // only 4 because of the space
            graph.getGridLabelRenderer().setNumVerticalLabels(5);

// set manual x bounds to have nice steps
            graph.getViewport().setMinX(0);
            graph.getViewport().setMaxX(4);
            graph.getViewport().setXAxisBoundsManual(true);

            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(5000);
            graph.getViewport().setYAxisBoundsManual(true);

            StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
            //staticLabelsFormatter.setHorizontalLabels(new String[] {"נובמבר", "דצמבר", "ינואר"});
            staticLabelsFormatter.setHorizontalLabels(labels);
            graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

            // styling
            series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                @Override
                public int get(DataPoint data) {
                    return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
                }
            });

            series.setSpacing(20);

            // draw values on top
            series.setDrawValuesOnTop(true);
            series.setValuesOnTopColor(Color.RED);

            graph.getGridLabelRenderer().setHumanRounding(false);

            super.onPostExecute(aVoid);
        }
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
            final ViewHolder holder;

            Order order = (Order) getItem(position);
            java.util.Date date= order.getDate();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int month = cal.get(Calendar.MONTH);

            Log.e("DEBUG","order's month : "+ month);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(layout, parent, false);
                holder = new ViewHolder();
                holder.tvOrderHistoryDate = (TextView) convertView.findViewById(R.id.tvOrderHistoryDate);
                holder.tvOrderHistoryPrice = (TextView) convertView.findViewById(R.id.tvOrderHistoryPrice);
                holder.tvOrderDetails = (TextView) convertView.findViewById(R.id.tvOrderDetails);
                holder.imgViewDate = (ImageView) convertView.findViewById(R.id.imgViewDate);
                holder.imgViewMore = (ImageView) convertView.findViewById(R.id.imgViewMore);
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
                    Log.e("MEAL",meal.toString());
                    details.append(getString(R.string.star) + " ");
                    details.append(meal.getTitle() + " ");
                    bd = new BigDecimal(meal.getTotalPrice());
                    bd = bd.setScale(1, RoundingMode.HALF_DOWN);
                    details.append(bd + " " + nis.getSymbol());
                    details.append("\n");

                    if (meal.getChosenDrink().getTitle() != null ){
                        Log.e("DEBUG","Drink? "+meal.getChosenDrink());
                        details.append("  "+getResources().getString(R.string.history_drink_title)+" ");
                        details.append(meal.getChosenDrink().getTitle());
                        if (!meal.getParentMeal().isIncludesDrink()){
                            details.append(" " + meal.getChosenDrink().getPrice() + " " + nis.getSymbol());
                        }
                        details.append("\n");
                    }
                    if (meal.getChosenExtras() != null && meal.getChosenExtras().size() > 0){

                    }
                    if(meal.getComment() != null && meal.getComment().length() > 0 ) {
                        details.append("  "+getResources().getString(R.string.history_comment_title)+" ");
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
                    details.append(getString(R.string.star) + " " + item.getParentItem().getTitle());
                    details.append(" ");
                    bd = new BigDecimal(item.getParentItem().getPrice());
                    bd = bd.setScale(1, RoundingMode.HALF_DOWN);
                    details.append(bd + " " + nis.getSymbol());
                }
            }

            holder.imgViewDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.tvOrderDetails.performClick();
                }
            });

            holder.imgViewMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.tvOrderDetails.performClick();
                }
            });

            holder.tvOrderHistoryDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.tvOrderDetails.performClick();
                }
            });
            holder.tvOrderHistoryPrice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.tvOrderDetails.performClick();
                }
            });

            holder.tvOrderDetails.setText(details.toString());
            holder.tvOrderDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   if (holder.tvOrderDetails.getVisibility() == View.VISIBLE){
                       holder.tvOrderDetails.setVisibility(View.GONE);
                   } else{
                       holder.tvOrderDetails.setVisibility(View.VISIBLE);
                   }
                }
            });
            return convertView;
        }

        private class ViewHolder {
            TextView tvOrderHistoryPrice;
            TextView tvOrderHistoryDate;
            TextView tvOrderDetails;
            ImageView imgViewDate;
            ImageView imgViewMore;
        }

    }

    private class HistoryListTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            MyApplicationClass app = (MyApplicationClass)getApplication();
            LocalDBHandler db = app.getLocalDB();
            orders = db.selectOrders(customer.getId());
            if(orders == null) {
                orders = new ArrayList<>();
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else{
            finish();
            Intent menuIntent = new Intent(OrdersHistoryActivity.this, MainActivity.class);
            startActivity(menuIntent);
        }

    }
}
