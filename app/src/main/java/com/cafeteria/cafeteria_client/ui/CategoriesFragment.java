package com.cafeteria.cafeteria_client.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.Category;
import com.cafeteria.cafeteria_client.utils.DataHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shira Elitzur on 08/09/2016.
 */
public class CategoriesFragment extends Fragment {


    private GridView grid;
    private List<Category> categories;
    private List<Integer> colors;
    private int colorIndex = -1;
    int picColorIndex = 2;
    String [] pics = {"can","kebab","mixer","pizza","toffee"};
    int picIndex = -1;
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

        if (categories == null) {
            initCategories();
        }

        colors = new ArrayList<>();
        colors.add(R.color.colorIconBg0);
        colors.add(R.color.colorIconBg1);
        colors.add(R.color.colorIconBg2);
        colors.add(R.color.colorIconBg3);
        colors.add(R.color.colorIconBg4);


        grid.setAdapter(new GridViewAdapter(getActivity(),categories,R.layout.category_card_item));
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent categoryItemsIntent = new Intent(getContext(),CategoryItemsActivity.class);
                categoryItemsIntent.putExtra("category",categories.get(i));
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

                holder.rlCardView = (RelativeLayout) convertView.findViewById(R.id.rlCardView);
                holder.categoryBtn = (TextView) convertView.findViewById(R.id.categoryBtn);
                holder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
                holder.ivOvalBg = (ImageView) convertView.findViewById(R.id.ivOvalBg);

                if( colorIndex == 4 ) {
                    colorIndex = -1;
                }

                int color = ++colorIndex;
                GradientDrawable bgShape = (GradientDrawable)holder.ivOvalBg.getBackground();
                bgShape.setColor(getResources().getColor(colors.get(color)));
//                holder.rlCardView.setBackgroundColor(getResources().getColor(colors.get(++colorIndex)));
                holder.categoryBtn.setTextColor(getResources().getColor(colors.get(color)));
//               holder.categoryBtn.setBackgroundColor(getResources().getColor(R.color.colorBackground));
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();

            }

            // change the components to fit the current item that the cell should display
            category = items.get(position);
            holder.categoryBtn.setText(category.getTitle());

            if(category.getIcon() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(category.getIcon() , 0, category.getIcon().length);
                Drawable image = new BitmapDrawable(getResources(), bitmap);
                //image.setBounds( 0, 0, 60, 60 );
                //holder.categoryBtn.setCompoundDrawables( image, null, null, null );
//                if( picColorIndex == 4 ) {
//                    picColorIndex = 0;
//                }
//                if( picIndex == 4) {
//                    picIndex = -1;
//                }
                holder.ivIcon.setImageDrawable(image);
//                Resources resources = context.getResources();
//                final int resourceId = resources.getIdentifier(pics[++picIndex]+(picColorIndex++), "drawable",
//                        context.getPackageName());
//                holder.ivIcon.setImageDrawable(getResources().getDrawable(resourceId));

            } else {
                //holder.categoryBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star, 0, 0, 0);
                //holder.ivIcon.setBackground(getResources().getDrawable(R.drawable.star));

            }
            holder.categoryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent categoryItemsIntent = new Intent(getContext(),CategoryItemsActivity.class);
                    categoryItemsIntent.putExtra("category",category);
                    startActivity(categoryItemsIntent);
                }
            });


            return convertView;
        }

        private class ViewHolder {
            private TextView title;
            private TextView description;
            private ImageView ivIcon;
            private RelativeLayout rlCardView;
            private TextView categoryBtn;
            private ImageView ivOvalBg;
        }
    }
}
