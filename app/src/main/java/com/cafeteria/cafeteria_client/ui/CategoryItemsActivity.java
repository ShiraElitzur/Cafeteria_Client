package com.cafeteria.cafeteria_client.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cafeteria.cafeteria_client.data.DataHolder;
import com.cafeteria.cafeteria_client.interfaces.OnDialogResultListener;
import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.Category;
import com.cafeteria.cafeteria_client.data.Item;
import com.cafeteria.cafeteria_client.data.Meal;
import com.cafeteria.cafeteria_client.data.OrderedMeal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CategoryItemsActivity extends AppCompatActivity implements OnDialogResultListener{

    private ExpandableListView explvCategoryItems;
    private ListView lvCategoryItems;
    private CategoryItemsAdapter categoryItemsAdapter;
    private CategoryStandAloneItemsAdapter categoryStandAloneItemsAdapter;
    private Currency nis;

    /**
     * Holds the name of the item and a list of the meal names
     * Format: Title, child title
     */
    private HashMap<Item, List<Meal>> itemsDetails;
    /**
     * Holds the list of the meal names (same as the hash map)
     */
    private List<Item> itemsTitle;
    /**
     * The selected category
     */
    private Category category;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_items);

        // Here we get the category object selected by the user from the previous screen
        Intent previousIntent = getIntent();
        category = (Category) previousIntent.getSerializableExtra("category");

        //nis symbol
        Locale israel = new Locale("iw", "IL");
        nis = Currency.getInstance(israel);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(ContextCompat.getDrawable(this,R.drawable.logo_transparent));
        getSupportActionBar().setTitle("");

        // Temporary creation of categories items according to the chosen category
        initCategoryItems();
        itemsTitle = new ArrayList<>(itemsDetails.keySet());

        if (category.getMeals() != null) {
            initExpandableList();
        } else {
            initList();
        }

    }

    private void initList() {
        lvCategoryItems = (ListView) findViewById(R.id.lvCategoryItems);
        lvCategoryItems.setVisibility(View.VISIBLE);

        categoryStandAloneItemsAdapter = new CategoryStandAloneItemsAdapter(this, R.layout.category_items_child,itemsTitle);
        lvCategoryItems.setAdapter(categoryStandAloneItemsAdapter);

        lvCategoryItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                Item selectedItem = (Item) parent.getItemAtPosition(position);

                Toast.makeText(CategoryItemsActivity.this
                        ,getString(R.string.dialog_btn_keep_shopping_pressed),Toast.LENGTH_SHORT).show();
                DataHolder dataHolder = DataHolder.getInstance();
                dataHolder.addOrderdItem(selectedItem);
            }
        });
    }

    private void initExpandableList() {

        explvCategoryItems = (ExpandableListView) findViewById(R.id.explvCategoryItems);
        explvCategoryItems.setVisibility(View.VISIBLE);

        categoryItemsAdapter = new CategoryItemsAdapter(this, itemsDetails,itemsTitle);

        explvCategoryItems.setAdapter(categoryItemsAdapter);

        // Parent Listener - listener for the items Title
        explvCategoryItems.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            // LIstener for when an item click (there's also listener for expanding item and collapsing
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                if (itemsTitle.get(groupPosition).isStandAlone()){

                    //nedd to be another dialog or be added straight to order activity "sal"

                }
                return false;
            }
        });

        // Child Listener - listener for the item detail (the meal)
        explvCategoryItems.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Meal meal = itemsDetails.get(itemsTitle.get(groupPosition)).get(childPosition);
                Item item = itemsTitle.get(groupPosition);

                initMealDetailsDialog(meal);

                return false;

            }
        });

    }

    private void initMealDetailsDialog(Meal meal) {
        FragmentManager fm = getSupportFragmentManager();
        MealDetailsDialog mealDetailsDialog = new MealDetailsDialog();
        Bundle args = new Bundle();
        args.putBoolean("isMeal",true);
        args.putSerializable("meal",meal);
        mealDetailsDialog.setArguments(args);
        mealDetailsDialog.show(fm, "");
    }

    private void initCategoryItems() {
        itemsDetails = new HashMap<>();
        String title;
        List<Meal> meals;

        // I filled only the "meat" category in the previous screen
        if (category.getItems() != null) {

            for (Item item : category.getItems()) {

                title = item.getTitle(); //i.e. schnizel in zalacht
                meals = new ArrayList<>();

                if (category.getMeals() != null) {
                    for (Meal meal : category.getMeals()) {

                        if (title.equals(meal.getMain().getTitle())) {
                            meals.add(meal);
                        }
                    }
                }

                itemsDetails.put(item, meals);
            }
        }else{

        }

    }

    // btn order clicked, add the chosen meal and go to order activity
    @Override
    public void onPositiveResult(OrderedMeal orderedMeal) {
        DataHolder dataHolder = DataHolder.getInstance();
        dataHolder.addOrderdMeal(orderedMeal);
        Intent orderActivityIntent = new Intent(CategoryItemsActivity.this,OrderActivity.class);
        startActivity(orderActivityIntent);
    }

    // btn keep shopping clicked, add the chosen meal
    @Override
    public void onNegativeResult(OrderedMeal orderedMeal) {
        Toast.makeText(this,getString(R.string.dialog_btn_keep_shopping_pressed),Toast.LENGTH_SHORT).show();
        DataHolder dataHolder = DataHolder.getInstance();
        dataHolder.addOrderdMeal(orderedMeal);
    }

    private class CategoryItemsAdapter extends BaseExpandableListAdapter{

        private Context context;
        private HashMap<Item,List<Meal>> itemsDetails;
        private List<Item> itemsTitle;

        public CategoryItemsAdapter(Context context, HashMap<Item ,List<Meal>> itemsDetails,
                                    List<Item> itemsTitle){
            this.context = context;
            this.itemsDetails = itemsDetails;
            this.itemsTitle = itemsTitle;
        }

        @Override
        public int getGroupCount() {
            return itemsTitle.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return itemsDetails.get(itemsTitle.get(i)).size();
        }

        @Override
        public Object getGroup(int i) {
            return  itemsTitle.get(i);
        }

        @Override
        public Object getChild(int parent, int child) {
            return itemsDetails.get(itemsTitle.get(parent)).get(child);
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int parent, int child) {
            return child;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int parentPosition, boolean isExpanded, View convertView,
                                 ViewGroup parentView) {
            final ViewHolderPrimary holder;
            final Item item = (Item) getGroup(parentPosition);
            if (convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.category_items_parent,parentView,false);

                holder = new ViewHolderPrimary();
                holder.tvItemName = (TextView) convertView.findViewById(R.id.tvItemName);


                convertView.setTag(holder);

            } else{
                holder = (ViewHolderPrimary) convertView.getTag();
            }

            holder.tvItemName.setTypeface(null, Typeface.BOLD);
            holder.tvItemName.setText(item.getTitle());

            return  convertView;
        }

        @Override
        public View getChildView(final int parentPosition, final int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parentView) {
            ViewHolder holder;
            Meal meal = (Meal) getChild(parentPosition,childPosition);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.category_items_child, parentView, false);
                holder = new ViewHolder();
                holder.tvMealName = (TextView) convertView.findViewById(R.id.tvMealName);
                holder.tvTotal = (TextView) convertView.findViewById(R.id.tvTotal);
                holder.imgBtnAdd = (ImageButton) convertView.findViewById(R.id.imgBtnAdd);
                holder.imgBtnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Meal meal = (Meal) getChild(parentPosition,childPosition);
                        initMealDetailsDialog(meal);
                    }
                });

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvMealName.setTypeface(null, Typeface.BOLD);
            holder.tvMealName.setText(meal.getTitle());

            BigDecimal bd = new BigDecimal(meal.getPrice());
            bd = bd.setScale(1, RoundingMode.HALF_DOWN);
            holder.tvTotal.setText(bd + " " + nis.getSymbol());

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }

    public class CategoryStandAloneItemsAdapter extends BaseAdapter {
        private List<Item> items;
        private Context context;
        private int layout;

        public CategoryStandAloneItemsAdapter(Context context, int layout, List<Item> items) {
            this.context = context;
            this.layout = layout;
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            Item item = (Item) getItem(position);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.category_items_child, parent, false);
                holder = new ViewHolder();
                holder.tvMealName = (TextView) convertView.findViewById(R.id.tvMealName);
                holder.tvTotal = (TextView) convertView.findViewById(R.id.tvTotal);
                holder.imgBtnAdd = (ImageButton) convertView.findViewById(R.id.imgBtnAdd);
                holder.imgBtnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Item selectedItem = (Item) getItem(position);

                        Toast.makeText(CategoryItemsActivity.this
                                ,getString(R.string.dialog_btn_keep_shopping_pressed),Toast.LENGTH_SHORT).show();
                        DataHolder dataHolder = DataHolder.getInstance();
                        dataHolder.addOrderdItem(selectedItem);
                    }
                });

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvMealName.setTypeface(null, Typeface.BOLD);
            holder.tvMealName.setText(item.getTitle());

            BigDecimal bd = new BigDecimal(item.getPrice());
            bd = bd.setScale(1, RoundingMode.HALF_DOWN);
            holder.tvTotal.setText(bd + " " + nis.getSymbol());

            return convertView;
        }

    }

    private class ViewHolderPrimary {
        TextView tvItemName;

    }

    private class ViewHolder {
        TextView tvMealName;
        TextView tvTotal;
        ImageButton imgBtnAdd;
    }
}
