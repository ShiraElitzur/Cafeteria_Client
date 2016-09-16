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

import com.cafeteria.cafeteria_client.interfaces.OnDialogResultListener;
import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.Drink;
import com.cafeteria.cafeteria_client.data.Item;
import com.cafeteria.cafeteria_client.data.Meal;
import com.cafeteria.cafeteria_client.data.OrderedMeal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class MealDetailsDialog extends DialogFragment{

    private Spinner spinnerExtras;
    private Spinner spinnerDrink;
    private Button btnDrink;
    private Button btnExtras;
    private TextView tvMealName;
    private TextView tvExtrasAmount;
    private EditText etComment;
    private TextView tvTotal;
    private Button btnOrder;
    private Button btnKeepShopping;
    private Dialog dialog;
    private Item item;
    private Meal meal;
    private Currency nis;
    private View view;
    private OrderedMeal orderedMeal;
    private OnDialogResultListener mListener;


    public MealDetailsDialog(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mListener = (OnDialogResultListener) context;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCompleteListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_meal_details, container);

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


        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOrder();
                mListener.onPositiveResult(orderedMeal);
                dialog.dismiss();

            }
        });

        btnKeepShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOrder();
                mListener.onNegativeResult(orderedMeal);
                dialog.dismiss();
            }
        });

        return view;
    }

    private void saveOrder(){
        orderedMeal = new OrderedMeal();
        orderedMeal.setComment(etComment.getText().toString());
        orderedMeal.setChosenDrink(new Drink());
        orderedMeal.setChosenExtras(new ArrayList<Item>());
        orderedMeal.setParentMeal(meal);
    }


    private void initComponents() {
        spinnerExtras = (Spinner)view.findViewById(R.id.spinnerExtras);
        spinnerDrink = (Spinner)view.findViewById(R.id.spinnerDrink);
        btnDrink = (Button) view.findViewById(R.id.btnDrink);
        btnExtras = (Button) view.findViewById(R.id.btnExtras);
        tvMealName = (TextView)view.findViewById(R.id.tvMealName);
        tvExtrasAmount = (TextView)view.findViewById(R.id.tvExtrasAmount);
        etComment = (EditText) view.findViewById(R.id.etComment);
        tvTotal = (TextView)view.findViewById(R.id.tvTotal);
        btnOrder = (Button) view.findViewById(R.id.btnOrder);
        btnKeepShopping = (Button) view.findViewById(R.id.btnKeepShopping);

        BigDecimal bd = new BigDecimal(meal.getPrice());
        bd = bd.setScale(2, RoundingMode.HALF_UP);

        String total = String.format(getResources().getString(R.string.dialog_tv_total)
            ,bd.doubleValue(), nis.getSymbol());

        String extrasAmount = String.format(getResources().getString(R.string.dialog_tv_extras_amount)
                ,meal.getExtraAmount());

        tvMealName.setText(meal.getTitle());
        tvExtrasAmount.setText(extrasAmount);
        tvTotal.setText(total);
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
