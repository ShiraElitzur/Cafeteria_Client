package com.cafeteria.cafeteria_client.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.Drink;
import com.cafeteria.cafeteria_client.data.Item;
import com.cafeteria.cafeteria_client.data.Meal;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class MealDetailsDialog extends DialogFragment {

    private Spinner spinnerExtras;
    private Spinner spinnerDrink;
    private Button btnDrink;
    private Button btnExtras;
    private TextView tvMealName;
    private TextView tvExtrasAmount;
    private EditText etNote;
    private TextView tvTotal;
    private Button btnPay;
    private Button btnKeepShopping;
    private Dialog dialog;
    private Item item;
    private Meal meal;
    Currency nis;

    public MealDetailsDialog(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_meal_details, container);

        meal = (Meal) getArguments().getSerializable("meal");

        //nis symbol
        Locale israel = new Locale("iw", "IL");
        nis = Currency.getInstance(israel);

        dialog = getDialog();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_meal_details);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);

        initComponents();
        List<Item> drinks = (List)new ArrayList<Drink>(meal.getDrinkOptions());


        spinnerExtras.setAdapter(new MyAdapter(getActivity(),R.layout.dialog_spinner_item,meal.getExtras()));

        spinnerDrink.setAdapter(new MyAdapter(getActivity(),meal.getDrinkOptions(), R.layout.dialog_spinner_item));

        btnDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinnerDrink.getVisibility() == View.GONE){
                    spinnerDrink.setVisibility(View.VISIBLE);}
                else{
                    spinnerDrink.setVisibility(View.GONE);
                }
            }
        });

        btnExtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinnerExtras.getVisibility() == View.GONE){
                    spinnerExtras.setVisibility(View.VISIBLE);}
                else{
                    spinnerExtras.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }

        private void initComponents() {
        spinnerExtras = (Spinner)dialog.findViewById(R.id.spinnerExtras);
        spinnerDrink = (Spinner)dialog.findViewById(R.id.spinnerDrink);
        btnDrink = (Button) dialog.findViewById(R.id.btnDrink);
        btnExtras = (Button) dialog.findViewById(R.id.btnExtras);
        tvMealName = (TextView)dialog.findViewById(R.id.tvMealName);
        tvExtrasAmount = (TextView)dialog.findViewById(R.id.tvExtrasAmount);
        etNote = (EditText) dialog.findViewById(R.id.etNote);
        tvTotal = (TextView)dialog.findViewById(R.id.tvTotal);
        btnPay = (Button) dialog.findViewById(R.id.btnPay);
        btnKeepShopping = (Button) dialog.findViewById(R.id.btnKeepShopping);

        tvMealName.setText(meal.getTitle());
        tvExtrasAmount.setText("Extras Available: " + meal.getExtraAmount());
            tvTotal.setText("Total: " + Double.toString(meal.getPrice())  + " " + nis.getSymbol());

    }

        public class MyAdapter extends BaseAdapter{

        Context context;
        int resource;
        List<Item> items;

        public MyAdapter(Context context, int resource, List<Item> items){
            this.context = context;
            this.resource = resource;
            this.items = items;
        }

        public MyAdapter(Context context, List<Drink> drinks,int resource){
            this.context = context;
            this.resource = resource;
            List<Item> items = (List)new ArrayList<Drink>(drinks);
            this.items = items;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getView( position, convertView, parent);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return items.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tvListItem;
            final Item item = items.get(position);
            if (convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(resource,parent,false);
                tvListItem = (TextView) convertView.findViewById(R.id.tvListItem);
                convertView.setTag(tvListItem);

            } else{
                tvListItem = (TextView) convertView.getTag();
            }

            tvListItem.setTypeface(null, Typeface.BOLD);
            tvListItem.setText(item.getTitle());

            return  convertView;
        }

    }


}
