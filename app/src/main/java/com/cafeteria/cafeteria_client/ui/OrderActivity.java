package com.cafeteria.cafeteria_client.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.Item;
import com.cafeteria.cafeteria_client.data.Meal;
import com.cafeteria.cafeteria_client.data.OrderedMeal;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Shira Elitzur
 */
public class OrderActivity extends DrawerActivity {

    private ListView lvOrderMeals;
    private ListView lvOrderItems;
    private List<OrderedMeal> orderedMeals;
    private List<Item> orderedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        super.onCreateDrawer();

        // TODO: 14/09/2016 get meals and items from local storge 
        initMeals();
        initItems();
        lvOrderMeals = (ListView)findViewById(R.id.lvOrderMeals);
        lvOrderItems = (ListView)findViewById(R.id.lvOrderItems);
        lvOrderItems.setAdapter( new OrderItemsAdapter(this,R.layout.single_order_item,orderedItems));
    }

    private void initItems() {
        orderedItems = new ArrayList<Item>();
        Item item = new Item();
        item.setTitle("במבה");
        item.setPrice(4.5);
        orderedItems.add(item);
        item = new Item();
        item.setTitle("פחית קולה");
        item.setPrice(5.0);
        orderedItems.add(item);
    }

    private void initMeals() {
        orderedMeals = new ArrayList<OrderedMeal>();
        OrderedMeal meal = new OrderedMeal();
        Meal parentMeal = new Meal();
        parentMeal.setTitle("שניצל בצלחת");
        meal.setParentMeal(parentMeal);
        List<Item> mealItems = new ArrayList<Item>();
        Item item = new Item();
        item.setTitle("אורז");
        mealItems.add(item);
        item = new Item();
        item.setTitle("אפונה");
        mealItems.add(item);
        meal.setChosenExtras(mealItems);
    }

    private class OrderItemsAdapter extends ArrayAdapter<Item>{

        private int layout;
        private List<Item> items;

        public OrderItemsAdapter(Context context, int layout, List<Item> items) {
            super(context, layout, items);
            this.layout = layout;
            this.items = items;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {

                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(layout, parent, false);

                holder = new ViewHolder();
                holder.tvOrderItemTitle = (TextView)convertView.findViewById(R.id.tvOrderItemTitle);
                holder.imgBtnRemoveItem = (ImageButton)convertView.findViewById(R.id.imgBtnRemoveItem);
                holder.imgBtnRemoveItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        orderedItems.remove(position);
                        items.remove(position);
                    }
                });

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            holder.tvOrderItemTitle.setText(items.get(position).getTitle());

            return convertView;
        }

        private class ViewHolder {
            TextView tvOrderItemTitle;
            ImageButton imgBtnRemoveItem;
        }


    }
}
