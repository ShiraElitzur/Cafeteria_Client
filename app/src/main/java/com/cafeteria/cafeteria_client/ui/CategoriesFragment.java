package com.cafeteria.cafeteria_client.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.Category;
import com.cafeteria.cafeteria_client.data.Item;
import com.cafeteria.cafeteria_client.data.Meal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shira Elitzur on 08/09/2016.
 */
public class CategoriesFragment extends Fragment {


    private GridView grid;
    private ArrayList<Category> categories;

    public CategoriesFragment () {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.categories_fragment, container, false);
        grid = (GridView)v.findViewById(R.id.gridView);

        // Temporary creation of categories list
        // The real list should come from the data base of course
        initCategories();


        grid.setAdapter(new GridViewAdapter(getActivity(),categories,R.layout.category_grid_cell));

        return v;
    }

    private void initCategories(){
        categories = new ArrayList<>();
        Category cat = new Category();
        cat.setTitle("בשרי");
        cat.setDescription("ארוחות בשריות מושקעות");
        // init items in category
        List<Item> items = new ArrayList<>();
        Item item = new Item();
        item.setTitle("שניצל");
        //item.setMeal(true);
        // init meals related to this item
        Meal meal = new Meal();
        meal.setTitle("שניצל בצלחת");
        //item.addMeal(meal);
        meal = new Meal();
        meal.setTitle("שניצל בבאגט");
        //item.addMeal(meal);
        meal = new Meal();
        meal.setTitle("שניצל בפיתה");
        //item.addMeal(meal);
        items.add(item);

        item = new Item();
        item.setTitle("המבורגר");
        //item.setMeal(true);
        // init meals related to this item
        meal = new Meal();
        meal.setTitle("המבורגר בצלחת");
        //item.addMeal(meal);
        meal = new Meal();
        meal.setTitle("המבורגר בבאגט");
        //item.addMeal(meal);
        meal = new Meal();
        meal.setTitle("המבורגר בפיתה");
        //item.addMeal(meal);
        items.add(item);

        item = new Item();
        item.setTitle("פרגית");
        //item.setMeal(true);
        // init meals related to this item
        meal = new Meal();
        meal.setTitle("פרגית בצלחת");
        //item.addMeal(meal);
        meal = new Meal();
        meal.setTitle("פרגית בבאגט");
        //item.addMeal(meal);
        meal = new Meal();
        meal.setTitle("פרגית בפיתה");
        //item.addMeal(meal);
        items.add(item);

        item = new Item();
        item.setTitle("לאפה שווארמה");
        //item.setMeal(false);
        items.add(item);

        // add items to category
        cat.setItems(items);


        categories.add(cat);
        cat = new Category();
        cat.setTitle("חלבי");
        cat.setDescription("ארוחות חלביות מדהימות");
        categories.add(cat);
        cat = new Category();
        cat.setTitle("סלטים");
        cat.setDescription("מבחר סלטים בהרכבה");
        categories.add(cat);
        cat = new Category();
        cat.setTitle("שתיה חמה");
        cat.setDescription("כל סוגי השתייה החמה");
        categories.add(cat);
        cat = new Category();
        cat.setTitle("מאפים");
        cat.setDescription("כל סוגי המאפים");
        categories.add(cat);
        cat = new Category();
        cat.setTitle("שתיה קרה");
        cat.setDescription("כל סוגי השתיה הקרה");
        categories.add(cat);
        cat = new Category();
        cat.setTitle("חטיפים");
        cat.setDescription("במבה,ביסלי,פסק זמן...");
        categories.add(cat);
    }

    /**
     * Adapts between the UI GridView to th Categories list
     */
    private class GridViewAdapter extends BaseAdapter {

        private Context context;
        ArrayList<Category> items;
        int layout;

        public GridViewAdapter(Context context, ArrayList<Category> items, int layout){
            this.context = context;
            this.items = items;
            this.layout = layout;
        }

        @Override
        public int getCount() {
            return categories.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        /**
         * This method determine how one grid cell looks and behaves
         * The method returns the ready view (=cell)
         * @param position
         * @param convertView
         * @param parent
         * @return
         */
        @Override
        public View getView(int position, View convertView, final ViewGroup parent) {
            final ViewHolder holder;
            final Category item;

            if(convertView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(layout, parent, false);

                // find the UI components of the cell
                holder = new ViewHolder();
                holder.title =(TextView)convertView.findViewById(R.id.categoryTitle);
                holder.description =(TextView)convertView.findViewById(R.id.categoryDesc);
                holder.image =(ImageButton) convertView.findViewById(R.id.categoryImage);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();

            }

            // change the components to fit the current item that the cell should display
            item = items.get(position);
            // Title, description and image
            holder.title.setText(item.getTitle());
            holder.description.setText(item.getDescription());
//            // The image currently is from a fictive list pic0...pic5 will be from the database
//            int imageResource = getResources().getIdentifier( "@drawable/pic"+position, null, getActivity().getPackageName());
//            //holder.image.setImageDrawable(getResources().getDrawable(imageResource));
//            holder.image.setImageResource(imageResource);


            // Setting imageButton onCLick function, will pass the clicked category to the next
            // activity
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent categoryItemsIntent = new Intent(context,CategoryItemsActivity.class);
                    categoryItemsIntent.putExtra("category",item);
                    startActivity(categoryItemsIntent);

                }
            });

            return convertView;
        }

        private class ViewHolder {
            private TextView title;
            private TextView description;
            private ImageButton image;
        }
    }
}
