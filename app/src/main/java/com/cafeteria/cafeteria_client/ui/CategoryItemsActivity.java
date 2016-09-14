package com.cafeteria.cafeteria_client.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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

import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.Category;
import com.cafeteria.cafeteria_client.data.Item;
import com.cafeteria.cafeteria_client.data.Meal;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CategoryItemsActivity extends AppCompatActivity {

    private ExpandableListView explvCategoryItems;
    private CategoryItemsAdapter categoryItemsAdapter;
    /**
     * Holds the name of the item and a list of the meal names
     * Format: Title, child title
     */
    private HashMap<String, List<String>> itemsDetails;
    /**
     * Holds the list of the meal names (same as the hash map)
     */
    private List<String> itemsTitle;
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
                 Toast.makeText(getApplicationContext(),
                 "Group Clicked " + itemsTitle.get(groupPosition),
                 Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Child Listener - listener for the item detail (the meal)
        explvCategoryItems.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        itemsTitle.get(groupPosition)
                                + " : "
                                + itemsDetails.get(
                                itemsTitle.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });

    }

    private void initCategoryItems() {
        itemsDetails = new HashMap<>();
        String title;
        List<String> meals;

        // I filled only the "meat" category in the previous screen
        if (category.getItems() != null) {

            for (Item item : category.getItems()) {

                title = item.getTitle();
                meals = new ArrayList<>();
//                if (item.isMeal()) {
//                    for (Meal meal : item.getMeals()) {
//                        meals.add(meal.getMealName());
//                    }
//                }

                itemsDetails.put(title, meals);
            }
        }else{

        }

    }


    private class CategoryItemsAdapter extends BaseExpandableListAdapter{

        private Context context;
        private HashMap<String,List<String>> itemsDetails;
        private List<String> itemsTitle;

        public CategoryItemsAdapter(Context context, HashMap<String ,List<String>> itemsDetails,
                                    List<String> itemsTitle){
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
            String parentString = (String) getGroup(parentPosition);
            if (convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.category_items_parent,parentView,false);
                tvItemName = (TextView) convertView.findViewById(R.id.tvItemName);
                convertView.setTag(tvItemName);

            } else{
                tvItemName = (TextView) convertView.getTag();
            }

            tvItemName.setTypeface(null, Typeface.BOLD);
            tvItemName.setText(parentString);

            return  convertView;
        }

        @Override
        public View getChildView(int parentPosition, int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parentView) {
            final String child = (String) getChild(parentPosition,childPosition);

            if (convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.category_items_child,parentView,false);
            }
            TextView tvMealName = (TextView) convertView.findViewById(R.id.tvMealName);
            tvMealName.setText(child);

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }
}
