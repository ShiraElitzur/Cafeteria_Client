package com.cafeteria.cafeteria_client.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cafeteria.cafeteria_client.interfaces.MultiSpinnerListener;
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

public class MealDetailsDialog extends DialogFragment implements MultiSpinnerListener{

    private MultiSpinner spinnerExtras;
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
    private Meal meal;
    private Currency nis;
    private View view;
    private OrderedMeal orderedMeal;
    private OnDialogResultListener mListener;
    private double drinkPrice = 0;
    private double extraPrice = 0;
    private Drink chosenDrink;
    private List<Item> chosenExtra = new ArrayList<>();

    public MealDetailsDialog() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mListener = (OnDialogResultListener) context;
        } catch (final ClassCastException e) {
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
        List<Drink> drinks = new ArrayList<Drink>(meal.getDrinkOptions());
        Drink defaultText = new Drink();
        defaultText.setTitle(getString(R.string.dialog_spinner_default_text));
        drinks.add(0,defaultText);

        spinnerDrink.setAdapter(new MyAdapter(getActivity(), drinks, R.layout.dialog_spinner_item));
        spinnerDrink.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenDrink = (Drink) parent.getItemAtPosition(position);
                drinkPrice = chosenDrink.getPrice();
                BigDecimal bd = new BigDecimal(meal.getPrice() + drinkPrice + extraPrice);
                bd = bd.setScale(2, RoundingMode.HALF_UP);

                String total = String.format(getResources().getString(R.string.dialog_tv_total)
                        , bd.doubleValue(), nis.getSymbol());

                tvTotal.setText(total);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                chosenDrink = null;
                drinkPrice = 0;
                BigDecimal bd = new BigDecimal(meal.getPrice() + drinkPrice + extraPrice);
                bd = bd.setScale(2, RoundingMode.HALF_UP);

                String total = String.format(getResources().getString(R.string.dialog_tv_total)
                        , bd.doubleValue(), nis.getSymbol());

                tvTotal.setText(total);
            }
        });

        List<String> extrasTitle = new ArrayList<>();
        for (Item extra : meal.getExtras()){
            extrasTitle.add(extra.getTitle());
        }
        spinnerExtras.setItems(extrasTitle,getString(R.string.dialog_multi_spinner_default_text), this);

        btnDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinnerDrink.getVisibility() == View.GONE) {
                    spinnerDrink.setVisibility(View.VISIBLE);
                } else {
                    spinnerDrink.setVisibility(View.GONE);
                }
            }
        });


        btnExtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinnerExtras.getVisibility() == View.GONE) {
                    spinnerExtras.setVisibility(View.VISIBLE);
                } else {
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

    private void saveOrder() {
        orderedMeal = new OrderedMeal();
        orderedMeal.setComment(etComment.getText().toString());
        if (chosenDrink != null) {
            orderedMeal.setChosenDrink(chosenDrink);
        }
        orderedMeal.setChosenExtras(chosenExtra);
        orderedMeal.setParentMeal(meal);
    }


    private void initComponents() {
        spinnerExtras = (MultiSpinner) view.findViewById(R.id.spinnerExtras);
        spinnerDrink = (Spinner) view.findViewById(R.id.spinnerDrink);
        btnDrink = (Button) view.findViewById(R.id.btnDrink);
        btnExtras = (Button) view.findViewById(R.id.btnExtras);
        tvMealName = (TextView) view.findViewById(R.id.tvMealName);
        tvExtrasAmount = (TextView) view.findViewById(R.id.tvExtrasAmount);
        etComment = (EditText) view.findViewById(R.id.etComment);
        tvTotal = (TextView) view.findViewById(R.id.tvTotal);
        btnOrder = (Button) view.findViewById(R.id.btnOrder);
        btnKeepShopping = (Button) view.findViewById(R.id.btnKeepShopping);


        BigDecimal bd = new BigDecimal(meal.getPrice());
        bd = bd.setScale(2, RoundingMode.HALF_UP);

        String total = String.format(getResources().getString(R.string.dialog_tv_total)
                , bd.doubleValue(), nis.getSymbol());

        String extrasAmount = String.format(getResources().getString(R.string.dialog_tv_extras_amount)
                , meal.getExtraAmount());

        tvMealName.setText(meal.getTitle());
        tvExtrasAmount.setText(extrasAmount);
        tvTotal.setText(total);
    }

    //get items selceted
    @Override
    public void onItemsSelected(boolean[] selected) {
        List<String> extrasTitle = new ArrayList<>();
        chosenExtra = new ArrayList<>();

        for (int i = 0; i< selected.length;i++){
            if (selected[i] == true){
                chosenExtra.add(meal.getExtras().get(i));
            }
        }
        for (Item item: chosenExtra){
            extrasTitle.add(item.getTitle());
        }

        int extrasLeft = meal.getExtraAmount() - chosenExtra.size();
        String extrasAmount;
        if (extrasLeft >= 0){
            extrasAmount = String.format(getResources().getString(R.string.dialog_tv_extras_amount)
                    , extrasLeft);
            extraPrice = 0;
        }else{
            extrasAmount = String.format(getResources().getString(R.string.dialog_no_extra_left),extrasLeft*(-1));

            extraPrice = chosenExtra.get(chosenExtra.size()-1).getPrice();
            BigDecimal bd = new BigDecimal(meal.getPrice() + drinkPrice + extraPrice);
            bd = bd.setScale(2, RoundingMode.HALF_UP);

            String total = String.format(getResources().getString(R.string.dialog_tv_total)
                    , bd.doubleValue(), nis.getSymbol());

            tvTotal.setText(total);
        }
        tvExtrasAmount.setText(extrasAmount);

    }


    public class MyAdapter extends BaseAdapter {

        Context context;
        int resource;
        List<Item> items;

        public MyAdapter(Context context, int resource, List<Item> items) {
            this.context = context;
            this.resource = resource;
            this.items = items;
        }

        public MyAdapter(Context context, List<Drink> drinks, int resource) {
            this.context = context;
            this.resource = resource;
            List<Item> items = (List) new ArrayList<Drink>(drinks);
            this.items = items;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getView(position, convertView, parent);
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
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(resource, parent, false);
                tvListItem = (TextView) convertView.findViewById(R.id.tvListItem);
                convertView.setTag(tvListItem);

            } else {
                tvListItem = (TextView) convertView.getTag();
            }

            tvListItem.setText(item.getTitle());
            tvListItem.setTypeface(null, Typeface.BOLD);

            return convertView;
        }

    }



}
