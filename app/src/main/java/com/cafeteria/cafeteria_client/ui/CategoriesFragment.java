package com.cafeteria.cafeteria_client.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.Category;
import com.cafeteria.cafeteria_client.data.DataHolder;
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
    private List<Category> categories;

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
        if (categories == null) {
            initCategories();
        }


        grid.setAdapter(new GridViewAdapter(getActivity(),categories,R.layout.category_grid_cell));
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category category = categories.get(position);
                Intent categoryItemsIntent = new Intent(getContext(),CategoryItemsActivity.class);
                categoryItemsIntent.putExtra("category",category);
                startActivity(categoryItemsIntent);
            }
        });

        return v;
    }

    private void initCategories(){
        // will bring the real list if set or the fake one
        DataHolder dataHolder = DataHolder.getInstance();
        categories = dataHolder.getCategories();
    }

    /**
     * Adapts between the UI GridView to th Categories list
     */
    private class GridViewAdapter extends BaseAdapter {

        private Context context;
        List<Category> items;
        int layout;

        public GridViewAdapter(Context context, List<Category> items, int layout){
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
                //holder.description =(TextView)convertView.findViewById(R.id.categoryDesc);
                holder.image =(ImageView) convertView.findViewById(R.id.categoryImage);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();

            }

            // change the components to fit the current item that the cell should display
            category = items.get(position);
            // Title, description and image
            holder.title.setText(category.getTitle());
//            holder.description.setText(category.getDescription());

            Bitmap bitmap = BitmapFactory.decodeByteArray(category.getIcon() , 0, category.getIcon().length);
            holder.image.setImageBitmap(bitmap);


            // Setting imageButton onCLick function, will pass the clicked category to the next
            // activity
//            holder.image.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent categoryItemsIntent = new Intent(context,CategoryItemsActivity.class);
//                    categoryItemsIntent.putExtra("category",category);
//                    startActivity(categoryItemsIntent);
//
//                }
//            });

            return convertView;
        }

        private class ViewHolder {
            private TextView title;
            private TextView description;
            private ImageView image;
        }
    }
}
