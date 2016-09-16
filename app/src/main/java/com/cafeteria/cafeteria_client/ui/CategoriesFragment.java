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
import com.cafeteria.cafeteria_client.data.Drink;
import com.cafeteria.cafeteria_client.data.Item;
import com.cafeteria.cafeteria_client.data.Main;
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
        List<Meal> meals = new ArrayList<>();
        Meal meal = new Meal();
        Main main = new Main();
        Item item = new Item();
        List<Drink> drinks = new ArrayList<>();
        Drink drink = new Drink();
        drink.setTitle("קוקה קולה");
        drinks.add(drink);
        drink = new Drink();
        drink.setTitle("פאנטה");
        drinks.add(drink);
        drink = new Drink();
        drink.setTitle("ענבים");
        drinks.add(drink);
        drink = new Drink();
        drink.setTitle("ספרייט");
        drinks.add(drink);

        List<Item> extras = new ArrayList<Item>();
        Item extra = new Item();
        extra.setTitle("אורז");
        extras.add(extra);
        extra = new Item();
        extra.setTitle("צ'יפס");
        extras.add(extra);
        extra = new Item();
        extra.setTitle("ירקות");
        extras.add(extra);

        item.setTitle("שניצל");
        item.setStandAlone(false);
        meal.setTitle("שניצל בצלחת");
        meal.setExtraAmount(2);
        meal.setExtras(extras);
        meal.setDrinkOptions(drinks);
        meal.setPrice(35.66657);
        main.setTitle("שניצל");
        meal.setMain(main);
        meals.add(meal);
        meal = new Meal();
        meal.setTitle("שניצל בבאגט");
        main = new Main();
        main.setTitle("שניצל");
        meal.setMain(main);
        meals.add(meal);
        meal = new Meal();
        meal.setTitle("שניצל בפיתה");
        main = new Main();
        main.setTitle("שניצל");
        meal.setMain(main);
        meals.add(meal);
        items.add(item);

        item = new Item();
        item.setTitle("המבורגר");
        item.setStandAlone(false);
        meal = new Meal();
        meal.setTitle("המבורגר בצלחת");
        main = new Main();
        main.setTitle("המבורגר");
        meal.setMain(main);
        meals.add(meal);
        meal = new Meal();
        meal.setTitle("המבורגר בבאגט");
        main = new Main();
        main.setTitle("המבורגר");
        meal.setMain(main);
        meals.add(meal);
        meal = new Meal();
        meal.setTitle("המבורגר בפיתה");
        main = new Main();
        main.setTitle("המבורגר");
        meal.setMain(main);
        meals.add(meal);
        items.add(item);

        item = new Item();
        item.setTitle("פרגית");
        item.setStandAlone(false);
        meal = new Meal();
        meal.setTitle("פרגית בצלחת");
        main = new Main();
        main.setTitle("פרגית");
        meal.setMain(main);
        meals.add(meal);
        meal = new Meal();
        meal.setTitle("פרגית בבאגט");
        main = new Main();
        main.setTitle("פרגית");
        meal.setMain(main);
        meals.add(meal);
        meal = new Meal();
        meal.setTitle("פרגית בפיתה");
        main = new Main();
        main.setTitle("פרגית");
        meal.setMain(main);
        meals.add(meal);
        items.add(item);

        item = new Item();
        item.setTitle("לאפה שווארמה");
        item.setStandAlone(true);
        items.add(item);

        // set items and meals to category
        cat.setItems(items);
        cat.setMeals(meals);


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
            final Category category;

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
            category = items.get(position);
            // Title, description and image
            holder.title.setText(category.getTitle());
            holder.description.setText(category.getDescription());
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
                    categoryItemsIntent.putExtra("category",category);
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
