package com.cafeteria.cafeteria_client.ui;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cafeteria.cafeteria_client.interfaces.OnDialogResultListener;
import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.Category;
import com.cafeteria.cafeteria_client.data.Item;
import com.cafeteria.cafeteria_client.data.Meal;
import com.cafeteria.cafeteria_client.data.OrderedMeal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CategoryItemsActivity extends AppCompatActivity implements OnDialogResultListener{

    private ExpandableListView explvCategoryItems;
    private CategoryItemsAdapter categoryItemsAdapter;
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        // Sets the title as empty string because the default behavior is to display as title the app name.
        // Right now we have a background image on the action bar that contains the app name -
        // Need to think about that point.

        //getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.logo));
        getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.logo));



        // Temporary creation of categories items according to the chosen category
        initCategoryItems();

        explvCategoryItems = (ExpandableListView) findViewById(R.id.explvCategoryItems);

        itemsTitle = new ArrayList<>(itemsDetails.keySet());

        categoryItemsAdapter = new CategoryItemsAdapter(this, itemsDetails,itemsTitle);

        explvCategoryItems.setAdapter(categoryItemsAdapter);

        // Parent Listener - listener for the items Title
        explvCategoryItems.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            // LIstener for when an item click (there's also listener for expanding item and collapsing
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                Item item = itemsTitle.get(groupPosition);
                 Toast.makeText(getApplicationContext(),
                 "Group Clicked " + item,
                 Toast.LENGTH_SHORT).show();

                if (itemsTitle.get(groupPosition).isStandAlone()){

                    //nedd to be another dialog or be added straight to order activity "sal"

//                    FragmentManager fm = getSupportFragmentManager();
//                    MealDetailsDialog mealDetailsDialog = new MealDetailsDialog();
//                    Bundle args = new Bundle();
//                    args.putSerializable("meal",item);
//                    mealDetailsDialog.setArguments(args);
//                    mealDetailsDialog.show(fm, "");
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
                Toast.makeText(
                        getApplicationContext(),
                        item + " : " + meal, Toast.LENGTH_SHORT)
                        .show();

                FragmentManager fm = getSupportFragmentManager();
                MealDetailsDialog mealDetailsDialog = new MealDetailsDialog();
                Bundle args = new Bundle();
                args.putSerializable("meal",meal);
                mealDetailsDialog.setArguments(args);
                mealDetailsDialog.show(fm, "");

                return false;

            }
        });
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

                for (Meal meal : category.getMeals()){

                    if (title.equals(meal.getMain().getTitle())) {
                            meals.add(meal);
                        }
                }

                itemsDetails.put(item, meals);
            }
        }else{

        }

    }

    @Override
    public void onPositiveResult(OrderedMeal orderedMeal) {
        Toast.makeText(this,orderedMeal.toString(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNegativeResult(OrderedMeal orderedMeal) {
        Toast.makeText(this,orderedMeal.toString(),Toast.LENGTH_LONG).show();

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
            TextView tvItemName;
            final Item item = (Item) getGroup(parentPosition);
            if (convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.category_items_parent,parentView,false);
                tvItemName = (TextView) convertView.findViewById(R.id.tvItemName);
                convertView.setTag(tvItemName);

            } else{
                tvItemName = (TextView) convertView.getTag();
            }

            tvItemName.setTypeface(null, Typeface.BOLD);
            tvItemName.setText(item.getTitle());

            return  convertView;
        }

        @Override
        public View getChildView(int parentPosition, int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parentView) {
            TextView tvMealName;
            Meal meal = (Meal) getChild(parentPosition,childPosition);

            if (convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.category_items_child,parentView,false);
                tvMealName = (TextView) convertView.findViewById(R.id.tvMealName);
                convertView.setTag(tvMealName);

            }else{
                tvMealName = (TextView) convertView.getTag();
            }

            tvMealName.setTypeface(null, Typeface.BOLD);
            tvMealName.setText(meal.getTitle());


            return convertView;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }
}
