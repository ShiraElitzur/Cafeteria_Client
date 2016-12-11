package com.cafeteria.cafeteria_client.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.data.Customer;
import com.cafeteria.cafeteria_client.data.Meal;
import com.cafeteria.cafeteria_client.data.OrderedMeal;
import com.cafeteria.cafeteria_client.interfaces.OnDialogResultListener;
import com.cafeteria.cafeteria_client.utils.ApplicationConstant;
import com.cafeteria.cafeteria_client.utils.DataHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shira Elitzur on 08/09/2016.
 */
public class FavoritesFragment extends Fragment{

    private List<Meal> favoriteMeals;
    private RecyclerView rvFavorites;
//    private ListView lvSpecials;
    private List<Integer> colors;
    private LinearLayout llFavorites;
//    private LinearLayout llSpecials;
    private int colorIndex = -1;

    public FavoritesFragment () {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.favorites_fragment, container, false);

        favoriteMeals = DataHolder.getInstance().getFavorites();

        llFavorites = (LinearLayout)v.findViewById(R.id.llFavorites);
//        llSpecials = (LinearLayout)v.findViewById(R.id.llSpecials);
//        lvSpecials = (ListView) v.findViewById(R.id.lvSpecials);
        rvFavorites = (RecyclerView) v.findViewById(R.id.rvFavorites);

        if( favoriteMeals != null ) {
            rvFavorites.setAdapter(new FavoritesRecyclerViewAdapter(getActivity(), favoriteMeals));
            rvFavorites.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        colors = new ArrayList<>();
        colors.add(R.color.colorIconBg0);
        colors.add(R.color.colorIconBg1);
        colors.add(R.color.colorIconBg2);
        colors.add(R.color.colorIconBg3);
        colors.add(R.color.colorIconBg4);


//        lvSpecials.setAdapter( new SpecialsListAdapter(getActivity(),fakeSpecials,R.layout.special_card_item));
        if( favoriteMeals != null ) {
            Log.e("FAVORITES","favorites size - "+ favoriteMeals.size());
            if(favoriteMeals.isEmpty() || favoriteMeals.size() < 1 ) {
                llFavorites.setVisibility(View.GONE);
            }
        } else {
            llFavorites.setVisibility(View.GONE);
        }

//        if(fakeSpecials.isEmpty() || fakeSpecials.size() < 1){
//            llSpecials.setVisibility(View.GONE);
//        }
        return v;
    }




    public class FavoritesRecyclerViewAdapter extends RecyclerView.Adapter<FavoritesRecyclerViewAdapter.CustomViewHolder> {

        private Context context;
        List<Meal> favorites;

        public FavoritesRecyclerViewAdapter(Context context, List<Meal> favorites) {
            this.favorites = favorites;
            this.context = context;
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_card_item, parent, false);
            CustomViewHolder viewHolder = new CustomViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, int position) {
            Meal favorite = favorites.get(position);
            holder.title.setText(favorite.getTitle());
        }


        @Override
        public int getItemCount() {
            return (null != favorites ? favorites.size() : 0);
        }

        class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            protected TextView title;
            protected ImageView symbol;
            protected LinearLayout llFavoriteCard;

            public CustomViewHolder(View view) {
                super(view);
                this.title = (TextView) view.findViewById(R.id.tvFavoriteTitle);
                this.symbol = (ImageView) view.findViewById(R.id.ivSymbol);
                this.llFavoriteCard = (LinearLayout) view.findViewById(R.id.llFavoriteCard);

                if( colorIndex == 4 ) {
                    colorIndex = -1;
                }

                this.title.setTextColor(getResources().getColor(colors.get(++colorIndex)));
                this.llFavoriteCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        Meal meal = favoriteMeals.get(position);
                        Log.e("FAVORITES","Click on meal " + meal.getTitle());
                        FragmentManager fm = getFragmentManager();
                        MealDetailsDialog mealDetailsDialog = new MealDetailsDialog();
                        Bundle args = new Bundle();
                        args.putSerializable("meal", meal);
                        mealDetailsDialog.setArguments(args);
                        mealDetailsDialog.show(fm, "");
                    }
                });
                view.setOnClickListener(this);
                view.setTag(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                Meal meal = favoriteMeals.get(position);
                Log.e("FAVORITES","Click on meal " + meal.getTitle());
                FragmentManager fm = getFragmentManager();
                MealDetailsDialog mealDetailsDialog = new MealDetailsDialog();
                Bundle args = new Bundle();
                args.putSerializable("meal", meal);
                mealDetailsDialog.setArguments(args);
                mealDetailsDialog.show(fm, "");
            }
        }
    }

//    public class SpecialsListAdapter extends BaseAdapter {
//
//        List<Meal> meals;
//        Context context;
//        int layout;
//
//        public SpecialsListAdapter(Context context, List<Meal> meals, int layout){
//            this.context = context;
//            this.meals = meals;
//            this.layout = layout;
//        }
//
//        @Override
//        public int getCount() {
//            if( meals != null ){
//                return meals.size();
//            } else {
//                return 0;
//            }
//        }
//
//        @Override
//        public Object getItem(int i) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return 0;
//        }
//
//        @Override
//        public View getView(int position, View view, ViewGroup viewGroup) {
//            final ViewHolder holder;
//            final Meal meal;
//
//            if(view == null) {
//                LayoutInflater inflater = getActivity().getLayoutInflater();
//                view = inflater.inflate(layout, viewGroup, false);
//
//                // find the UI components of the cell
//                holder = new ViewHolder();
//                holder.title = (TextView) view.findViewById(R.id.tvSpecialTitle);
//
//                view.setTag(holder);
//            } else {
//                holder = (ViewHolder)view.getTag();
//
//            }
//
//            // change the components to fit the current item that the cell should display
//            meal = meals.get(position);
//            holder.title.setText(meal.getTitle());
//            return view;
//        }
//
//        private class ViewHolder {
//            TextView title;
//        }
//    }


}
