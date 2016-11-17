package com.cafeteria.cafeteria_client.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cafeteria.cafeteria_client.data.OrderedItem;
import com.cafeteria.cafeteria_client.utils.DataHolder;
import com.cafeteria.cafeteria_client.data.Main;
import com.cafeteria.cafeteria_client.interfaces.OnDialogResultListener;
import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.Category;
import com.cafeteria.cafeteria_client.data.Item;
import com.cafeteria.cafeteria_client.data.Meal;
import com.cafeteria.cafeteria_client.data.OrderedMeal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class CategoryItemsActivity extends AppCompatActivity implements OnDialogResultListener
    ,SearchView.OnQueryTextListener {

    private ExpandableListView explvCategoryItems;
    private ListView lvCategoryItems;
    private CategoryItemsAdapter categoryItemsAdapter;
    private CategoryStandAloneItemsAdapter categoryStandAloneItemsAdapter;
    private Currency nis;
    private SearchView searchView;
    private List<Item> backup;
    private boolean explvList;
    private boolean list;
    private List<Main> backupMain;


    /**
     * Holds the name of the item and a list of the meal names
     * Format: Title, child title
     */
    private LinkedHashMap<Main, List<Meal>> mainsItemsDetails;
    private LinkedHashMap<Item, List<Meal>> itemsDetails;
    /**
     * Holds the list of the meal names (same as the hash map)
     */
    private List<Item> itemsTitle;
    private List<Main> mainsTitle;
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
        //getSupportActionBar().setLogo(ContextCompat.getDrawable(this,R.drawable.logo_transparent));
        getSupportActionBar().setTitle(category.getTitle());

        // Temporary creation of categories items according to the chosen category
        initCategoryItems();
        itemsTitle = new ArrayList<>(itemsDetails.keySet());
        mainsTitle = new ArrayList<>(mainsItemsDetails.keySet());

        if (category.getMeals() != null && category.getMeals().size() > 0) {
            initExpandableList();
            explvList = true;
        }
        if (category.getItems() != null && category.getItems().size() > 0){
            Log.e("LIST","normal list");
            initList();
            list = true;
        }

        searchView = (SearchView) findViewById(R.id.search); // inititate a search view
        searchView.setOnQueryTextListener(this);

    }

    private void initList() {
        lvCategoryItems = (ListView) findViewById(R.id.lvCategoryItems);
        lvCategoryItems.setVisibility(View.VISIBLE);

        categoryStandAloneItemsAdapter = new CategoryStandAloneItemsAdapter(this, R.layout.category_items_child,itemsTitle);
        lvCategoryItems.setAdapter(categoryStandAloneItemsAdapter);
        TextView itemsHeadline = new TextView(this);
        itemsHeadline.setTextAppearance(this,android.R.style.TextAppearance_Material_Menu);
        itemsHeadline.setText(getString(R.string.categotry_items_items));
        lvCategoryItems.addHeaderView(itemsHeadline,null,false);

        lvCategoryItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                Item selectedItem = (Item) parent.getItemAtPosition(position);

                Toast.makeText(CategoryItemsActivity.this
                        ,getString(R.string.dialog_btn_keep_shopping_pressed),Toast.LENGTH_SHORT).show();
                DataHolder dataHolder = DataHolder.getInstance();
                OrderedItem orderedItem = new OrderedItem();
                orderedItem.setParentItem(selectedItem);
                dataHolder.getTheOrder().getItems().add(orderedItem);
            }
        });
    }

    private void initExpandableList() {

        explvCategoryItems = (ExpandableListView) findViewById(R.id.explvCategoryItems);
        explvCategoryItems.setVisibility(View.VISIBLE);

        categoryItemsAdapter = new CategoryItemsAdapter(this, mainsItemsDetails,mainsTitle);

        explvCategoryItems.setAdapter(categoryItemsAdapter);
        TextView mealsHeadline = new TextView(this);
        mealsHeadline.setText(getString(R.string.category_items_meals));
        mealsHeadline.setTextAppearance(this,android.R.style.TextAppearance_Material_Menu);
        explvCategoryItems.addHeaderView(mealsHeadline,null,false);


        // Parent Listener - listener for the items Title
        explvCategoryItems.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            // LIstener for when an item click (there's also listener for expanding item and collapsing
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
//                if (mainsTitle.get(groupPosition) instanceof Item){
//
//                    //nedd to be another dialog or be added straight to order activity "sal"
//
//                }
                return false;
            }
        });

        // Child Listener - listener for the item detail (the meal)
        explvCategoryItems.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Meal meal = mainsItemsDetails.get(mainsTitle.get(groupPosition)).get(childPosition);
                //Item item = itemsTitle.get(groupPosition);

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
        mainsItemsDetails = new LinkedHashMap<>();
        itemsDetails = new LinkedHashMap<>();

        boolean existMain;
        List<Main> mains = new ArrayList<>();

        if (category.getMeals() != null) {
            for (Meal meal : category.getMeals()) {
                existMain = false;
                for (Main main : mains) {
                    if (main.getTitle().equalsIgnoreCase(meal.getMain().getTitle())) {
                        existMain = true;
                        break;
                    }
                }
                if (!existMain) {
                    mains.add(meal.getMain());
                }

            }

            for (Main main : mains) {
                List<Meal> mealsWithMain = new ArrayList<>();
                for (Meal meal : category.getMeals()) {
                    if (meal.getMain().getTitle().equalsIgnoreCase(main.getTitle())) {
                        mealsWithMain.add(meal);
                    }
                }
                mainsItemsDetails.put(main, mealsWithMain);
            }
        }

        if (category.getItems() != null) {
            for (Item item : category.getItems()) {
                itemsDetails.put(item, new ArrayList<Meal>());
            }
        }
    }

    // btn order clicked, add the chosen meal and go to order activity
    @Override
    public void onPositiveResult(OrderedMeal orderedMeal) {
        DataHolder dataHolder = DataHolder.getInstance();
        dataHolder.getTheOrder().getMeals().add(orderedMeal);
        Intent orderActivityIntent = new Intent(CategoryItemsActivity.this,OrderActivity.class);
        startActivity(orderActivityIntent);
    }

    // btn keep shopping clicked, add the chosen meal
    @Override
    public void onNegativeResult(OrderedMeal orderedMeal) {
        Toast.makeText(this,getString(R.string.dialog_btn_keep_shopping_pressed),Toast.LENGTH_SHORT).show();
        DataHolder dataHolder = DataHolder.getInstance();
        dataHolder.getTheOrder().getMeals().add(orderedMeal);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (explvList){
            categoryItemsAdapter.filter(newText);
        }
        if (list){
            categoryStandAloneItemsAdapter.filter(newText);
        }
        return false;
    }

    private class CategoryItemsAdapter extends BaseExpandableListAdapter{

        private Context context;
        private HashMap<Main,List<Meal>> itemsDetails;
        private List<Main> mains;

        public CategoryItemsAdapter(Context context, HashMap<Main ,List<Meal>> itemsDetails,
                                    List<Main> mains){
            this.context = context;
            this.itemsDetails = itemsDetails;
            this.mains = mains;
            backupMain = new ArrayList<>();
            backupMain.addAll(mains);
        }

        @Override
        public int getGroupCount() {
            return mains.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return mainsItemsDetails.get(mains.get(i)).size();
        }

        @Override
        public Object getGroup(int i) {
            return  mains.get(i);
        }

        @Override
        public Object getChild(int parent, int child) {
            return mainsItemsDetails.get(mains.get(parent)).get(child);
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
            final Main main = (Main) getGroup(parentPosition);
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
            holder.tvItemName.setText(main.getTitle());
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

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.imgBtnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Meal meal = (Meal) getChild(parentPosition,childPosition);
                    initMealDetailsDialog(meal);
                }
            });

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

        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            mains.clear();

            if (charText.length() == 0){
                mains.addAll(backupMain);
            } else {
                for (Main main : backupMain) {
                    if (main.getTitle().toLowerCase(Locale.getDefault()).contains(charText)) {
                        mains.add(main);
                    }
                }
            }
            notifyDataSetChanged();
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
            backup = new ArrayList<>();
            backup.addAll(items);
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
            final ViewHolder holder;
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
                        int qty = Integer.parseInt(holder.tvQty.getText().toString());

                        Toast.makeText(CategoryItemsActivity.this
                                ,getString(R.string.dialog_btn_keep_shopping_pressed),Toast.LENGTH_SHORT).show();
                        DataHolder dataHolder = DataHolder.getInstance();
                        for (int i=0; i < qty;i++){

                            OrderedItem orderedItem = new OrderedItem();
                            orderedItem.setParentItem(selectedItem);
                            dataHolder.getTheOrder().getItems().add(orderedItem);
                        }
                        //dataHolder.addOrderdItem(selectedItem);
                    }
                });
                holder.tvQty = (TextView) convertView.findViewById(R.id.tvQty);
                holder.tvQty.setVisibility(View.VISIBLE);

                holder.imgBtnPlus = (ImageButton) convertView.findViewById(R.id.imgBtnPlus);
                holder.imgBtnPlus.setVisibility(View.VISIBLE);
                holder.imgBtnPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       int qty = Integer.parseInt(holder.tvQty.getText().toString());
                        if (qty >= 1) {
                            qty++;
                            holder.tvQty.setText(String.valueOf(qty));
                        }
                    }
                });

                holder.imgBtnMinus = (ImageButton) convertView.findViewById(R.id.imgBtnMinus);
                holder.imgBtnMinus.setVisibility(View.VISIBLE);
                holder.imgBtnMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       int qty = Integer.parseInt(holder.tvQty.getText().toString());
                        if (qty > 1){
                            qty--;
                            holder.tvQty.setText(String.valueOf(qty));
                        }
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

        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            items.clear();

            if (charText.length() == 0){
                items.addAll(backup);
            } else {
                for (Item it : backup) {
                    if (it.getTitle().toLowerCase(Locale.getDefault()).contains(charText)) {
                        items.add(it);
                    }
                }
            }
            notifyDataSetChanged();
        }

    }

    private class ViewHolderPrimary {
        TextView tvItemName;

    }


    private class ViewHolder {
        TextView tvMealName;
        TextView tvTotal;
        ImageButton imgBtnAdd;
        TextView tvQty;
        ImageButton imgBtnPlus;
        ImageButton imgBtnMinus;
    }
}
