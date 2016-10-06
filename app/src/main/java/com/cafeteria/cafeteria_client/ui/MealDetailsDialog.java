package com.cafeteria.cafeteria_client.ui;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.cafeteria.cafeteria_client.data.DataHolder;
import com.cafeteria.cafeteria_client.data.Extra;
import com.cafeteria.cafeteria_client.interfaces.MultiSpinnerListener;
import com.cafeteria.cafeteria_client.interfaces.OnDialogResultListener;
import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.Drink;
import com.cafeteria.cafeteria_client.data.Meal;
import com.cafeteria.cafeteria_client.data.OrderedMeal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class MealDetailsDialog extends DialogFragment implements MultiSpinnerListener{

    // UI Components
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
    private Currency nis;
    private View view;

    // Data Objects
    private Drink chosenDrink;
    private List<Extra> chosenExtra = new ArrayList<>();
    //private Meal meal;
    private OrderedMeal orderedMeal;

    private OnDialogResultListener mListener;
    private double drinkPrice = 0;
    private double extraPrice = 0;
    private boolean edit = false;

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

        if(getArguments().getSerializable("meal") instanceof  Meal) {
            //meal = (Meal) getArguments().getSerializable("meal");
            orderedMeal = new OrderedMeal();
            orderedMeal.setParentMeal((Meal) getArguments().getSerializable("meal"));
        } else if (getArguments().getSerializable("meal") instanceof OrderedMeal) {
            orderedMeal = (OrderedMeal) getArguments().getSerializable("meal");
            drinkPrice = orderedMeal.getDrinkPrice();
            extraPrice = orderedMeal.getExtraPrice();
            if(orderedMeal.getChosenDrink() != null) {
                chosenDrink = orderedMeal.getChosenDrink();
            }
            edit = true;
            //meal = orderedMeal.getParentMeal();
        }

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
        initSpinnerAndButtonDrink();

        // Init extras spinner
        if ( orderedMeal.getParentMeal().getExtras() != null) {
            initExtrasSpinner();
        }

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

    private void initSpinnerAndButtonDrink() {
        List<Drink> drinks = new ArrayList<Drink>(DataHolder.getInstance().getDrinksList());
        Drink defaultText = new Drink();
        defaultText.setTitle(getString(R.string.dialog_spinner_default_text));
        drinks.add(0,defaultText);

        spinnerDrink.setAdapter(new MyAdapter(getActivity(), drinks, R.layout.dialog_spinner_item));
        if( chosenDrink != null ) {
            for( int i = 0 ; i < drinks.size(); i++) {
                if( drinks.get(i).getTitle().equalsIgnoreCase(chosenDrink.getTitle())) {
                    spinnerDrink.setSelection(i);
                    break;
                }
            }
            spinnerDrink.setVisibility(View.VISIBLE);
        }
        spinnerDrink.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenDrink = (Drink) parent.getItemAtPosition(position);
                drinkPrice = chosenDrink.getPrice();
                orderedMeal.setDrinkPrice(drinkPrice);
                BigDecimal bd = new BigDecimal(orderedMeal.getParentMeal().getPrice() + drinkPrice + extraPrice);
                bd = bd.setScale(2, RoundingMode.HALF_UP);

                String total = String.format(getResources().getString(R.string.dialog_tv_total)
                        , bd.doubleValue(), nis.getSymbol());
                tvTotal.setText(total);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                chosenDrink = null;
                drinkPrice = 0;
                // because i may also work on an existing orderedMeal i must reduce the drink price from the object
                orderedMeal.setDrinkPrice(0);
                BigDecimal bd = new BigDecimal(orderedMeal.getParentMeal().getPrice() + drinkPrice + extraPrice);
                bd = bd.setScale(2, RoundingMode.HALF_UP);

                String total = String.format(getResources().getString(R.string.dialog_tv_total)
                        , bd.doubleValue(), nis.getSymbol());

                tvTotal.setText(total);
            }
        });

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
    }

    private void initExtrasSpinner() {
        List<String> extrasTitle = new ArrayList<>();
        for (Extra extra : orderedMeal.getParentMeal().getExtras()) {
            extrasTitle.add(extra.getTitle());
        }
        if( edit && orderedMeal.getChosenExtras().size() > 0) {
            StringBuffer spinnerBuffer = new StringBuffer();
            boolean [] selections = new boolean[orderedMeal.getParentMeal().getExtras().size()];
            for( int i = 0; i < selections.length; i++) {
                for( Extra extra : orderedMeal.getChosenExtras()) {
                    if( orderedMeal.getParentMeal().getExtras().get(i).getTitle().equals(extra.getTitle())) {
                        selections[i] = true;
                        spinnerBuffer.append(extra.getTitle());
                        spinnerBuffer.append(", ");
                        break;
                    }
                }
            }

            spinnerExtras.setItems(extrasTitle, spinnerBuffer.toString(), this,selections);
        } else {
            spinnerExtras.setItems(extrasTitle, getString(R.string.dialog_multi_spinner_default_text), this);
        }

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
    }

    private void saveOrder() {
        orderedMeal.setComment(etComment.getText().toString());
        if (chosenDrink != null) {
            orderedMeal.setChosenDrink(chosenDrink);
            orderedMeal.setDrinkPrice(chosenDrink.getPrice());
        }
        orderedMeal.setChosenExtras(chosenExtra);
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

        if( edit ) {
            btnOrder.setText(getResources().getString(R.string.update_meal));
            if(orderedMeal.getChosenDrink() != null) {
                chosenDrink = orderedMeal.getChosenDrink();
            }

            if(orderedMeal.getChosenExtras().size() > 0) {
                spinnerExtras.setVisibility(View.VISIBLE);

            }

            if(orderedMeal.getComment() != null || !orderedMeal.getComment().equals("")) {
                etComment.setText(orderedMeal.getComment());
            }
        }

        BigDecimal bd = new BigDecimal(orderedMeal.getParentMeal().getPrice() + drinkPrice + extraPrice );
        bd = bd.setScale(1, RoundingMode.HALF_DOWN);

        String total = String.format(getResources().getString(R.string.dialog_tv_total)
                , bd.doubleValue(), nis.getSymbol());

        String extrasAmount = String.format(getResources().getString(R.string.dialog_tv_extras_amount)
                , orderedMeal.getParentMeal().getExtraAmount());

        tvMealName.setText(orderedMeal.getParentMeal().getTitle());
        tvExtrasAmount.setText(extrasAmount);
        tvTotal.setText(total);
    }

    //get items selected in the *extras spinner*
    @Override
    public void onItemsSelected(boolean[] selected) {
        List<String> extrasTitle = new ArrayList<>();
        chosenExtra = new ArrayList<>();

        for (int i = 0; i< selected.length;i++){
            if (selected[i] == true){
                chosenExtra.add(orderedMeal.getParentMeal().getExtras().get(i));
            }
        }

        for (Extra extra: chosenExtra){
            extrasTitle.add(extra.getTitle());
        }

        int extrasLeft = orderedMeal.getParentMeal().getExtraAmount() - chosenExtra.size();
        String extrasAmount;
        if (extrasLeft >= 0){
            extrasAmount = String.format(getResources().getString(R.string.dialog_tv_extras_amount)
                    , extrasLeft);
            extraPrice = 0;
        }else{
            extrasAmount = String.format(getResources().getString(R.string.dialog_no_extra_left),extrasLeft*(-1));
            //extraPrice = chosenExtra.get(chosenExtra.size()-1).getPrice();
            BigDecimal bd = new BigDecimal(orderedMeal.getParentMeal().getPrice() + drinkPrice + extraPrice);
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
        List<Drink> drinks;


//        public MyAdapter(Context context, int resource, List<Item> items) {
//            this.context = context;
//            this.resource = resource;
//            this.items = items;
//        }

        public MyAdapter(Context context, List<Drink> drinks, int resource) {
            this.context = context;
            this.resource = resource;
//            List<Item> items = (List) new ArrayList<Drink>(drinks);
            this.drinks = drinks;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getView(position, convertView, parent);
        }

        @Override
        public int getCount() {
            return drinks.size();
        }

        @Override
        public Object getItem(int position) {
            return drinks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return drinks.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tvListItem;
            final Drink drink = drinks.get(position);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(resource, parent, false);
                tvListItem = (TextView) convertView.findViewById(R.id.tvListItem);
                convertView.setTag(tvListItem);

            } else {
                tvListItem = (TextView) convertView.getTag();
            }

            tvListItem.setText(drink.getTitle());

            return convertView;
        }

    }

}
