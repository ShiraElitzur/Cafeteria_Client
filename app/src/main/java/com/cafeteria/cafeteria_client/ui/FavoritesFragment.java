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
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.ImageButton;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.RelativeLayout;
        import android.widget.TextView;

        import com.cafeteria.cafeteria_client.R;
        import com.cafeteria.cafeteria_client.data.Customer;
        import com.cafeteria.cafeteria_client.data.Item;
        import com.cafeteria.cafeteria_client.data.Meal;
        import com.cafeteria.cafeteria_client.data.OrderedItem;
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
public class FavoritesFragment extends Fragment {

    private List<Meal> favoriteMeals;
    private List<Item> favoriteItems;
    private RecyclerView rvMeals;
    private RecyclerView rvItems;
    //    private ListView lvSpecials;
    private List<Integer> colors;
    private LinearLayout llFavorites;
    private RelativeLayout rlEmptyView;
    //    private LinearLayout llSpecials;
    private int colorIndex = -1;
    private View v;
    private TextView tvFavoriteItems;
    private TextView tvFavoriteMeals;

    public FavoritesFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.favorites_fragment, container, false);

        favoriteMeals = DataHolder.getInstance().getFavoriteMeals();
        favoriteItems = DataHolder.getInstance().getFavoriteItems();

        llFavorites = (LinearLayout) v.findViewById(R.id.llFavorites);
        rvMeals = (RecyclerView) v.findViewById(R.id.rvFavorites);
        rvItems = (RecyclerView) v.findViewById(R.id.rvItems);
        rlEmptyView = (RelativeLayout) v.findViewById(R.id.rlEmptyView);
        tvFavoriteItems = (TextView) v.findViewById(R.id.tvFavoriteItems);
        tvFavoriteMeals = (TextView) v.findViewById(R.id.tvFavoriteMeals);

        colors = new ArrayList<>();
        colors.add(R.color.colorIconBg0);
        colors.add(R.color.colorIconBg1);
        colors.add(R.color.colorIconBg2);
        colors.add(R.color.colorIconBg3);
        colors.add(R.color.colorIconBg4);

        if (favoriteMeals == null && favoriteItems == null) {
            GetFavoriteItemsTask itemsTask = new GetFavoriteItemsTask(getActivity());
            itemsTask.execute();
            return v;
        }

        if (favoriteMeals != null) {
            rvMeals.setAdapter(new FavoritesRecyclerViewAdapter(getActivity(), favoriteMeals));
            rvMeals.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        if (favoriteItems != null) {
            rvItems.setAdapter(new FavoriteItemsRecyclerViewAdapter(getActivity(), favoriteItems));
            rvItems.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        }
        if ((favoriteMeals == null && favoriteItems == null) ||
                (favoriteMeals.size() == 0 && favoriteItems.size() == 0)) {
            rlEmptyView.setVisibility(View.VISIBLE);
        } else {
            if (favoriteItems != null && favoriteItems.size() == 0) {
                tvFavoriteItems.setVisibility(View.GONE);
            }
            if (favoriteMeals != null && favoriteMeals.size() == 0) {
                tvFavoriteMeals.setVisibility(View.GONE);
            }
        }
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_meal_card, parent, false);
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
            protected LinearLayout llFavoriteCard;

            public CustomViewHolder(View view) {
                super(view);
                this.title = (TextView) view.findViewById(R.id.tvFavoriteTitle);
                this.llFavoriteCard = (LinearLayout) view.findViewById(R.id.llFavoriteCard);

                if (colorIndex == 4) {
                    colorIndex = -1;
                }

//                Drawable background = this.ivItemBorder.getBackground();
//
//                if (background instanceof ShapeDrawable) {
//                    // cast to 'ShapeDrawable'
//                    ShapeDrawable shapeDrawable = (ShapeDrawable) background;
//                    shapeDrawable.getPaint().setColor(getResources().getColor(colors.get(++colorIndex)));
//                }

                this.title.setTextColor(getResources().getColor(colors.get(++colorIndex)));
                this.llFavoriteCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        Meal meal = favoriteMeals.get(position);
                        Log.e("FAVORITES", "Click on meal " + meal.getTitle());
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
                Log.e("FAVORITES", "Click on meal " + meal.getTitle());
                FragmentManager fm = getFragmentManager();
                MealDetailsDialog mealDetailsDialog = new MealDetailsDialog();
                Bundle args = new Bundle();
                args.putSerializable("meal", meal);
                mealDetailsDialog.setArguments(args);
                mealDetailsDialog.show(fm, "");
            }
        }
    }


    public class FavoriteItemsRecyclerViewAdapter extends RecyclerView.Adapter<FavoriteItemsRecyclerViewAdapter.CustomViewHolder> {

        private Context context;
        List<Item> favoriteItems;

        public FavoriteItemsRecyclerViewAdapter(Context context, List<Item> favoriteItems) {
            this.favoriteItems = favoriteItems;
            this.context = context;
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_item_card, parent, false);
            CustomViewHolder viewHolder = new CustomViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, int position) {
            Item favorite = favoriteItems.get(position);
            holder.title.setText(favorite.getTitle());
            holder.tvItemPrice.setText("" + favorite.getPrice());
        }


        @Override
        public int getItemCount() {
            return (null != favoriteItems ? favoriteItems.size() : 0);
        }

        class CustomViewHolder extends RecyclerView.ViewHolder {

            protected TextView title;
            protected LinearLayout rlFavoriteItem;
            protected TextView tvItemPrice;
            protected TextView tvQty;
            protected ImageButton imgBtnPlus;
            protected ImageButton imgBtnMinus;

            public CustomViewHolder(View view) {
                super(view);
                this.title = (TextView) view.findViewById(R.id.tvFavoriteItem);
                this.rlFavoriteItem = (LinearLayout) view.findViewById(R.id.rlFavoriteItem);
                this.tvItemPrice = (TextView) view.findViewById(R.id.tvItemPrice);

                if (colorIndex == 4) {
                    colorIndex = -1;
                }

                this.tvQty = (TextView) view.findViewById(R.id.tvQty);
                this.imgBtnPlus = (ImageButton) view.findViewById(R.id.imgBtnPlus);
                this.imgBtnPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int qty = Integer.parseInt(CustomViewHolder.this.tvQty.getText().toString());
                        if (qty >= 1) {
                            qty++;
                            CustomViewHolder.this.tvQty.setText(String.valueOf(qty));
                        }
                    }
                });

                this.imgBtnMinus = (ImageButton) view.findViewById(R.id.imgBtnMinus);
                this.imgBtnMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int qty = Integer.parseInt(CustomViewHolder.this.tvQty.getText().toString());
                        if (qty > 1) {
                            qty--;
                            CustomViewHolder.this.tvQty.setText(String.valueOf(qty));
                        }
                    }
                });

                this.title.setTextColor(getResources().getColor(colors.get(++colorIndex)));
                this.rlFavoriteItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        Item item = favoriteItems.get(position);
                        int qty = Integer.parseInt(tvQty.getText().toString());

                        showSnackBar();

                        DataHolder dataHolder = DataHolder.getInstance();
                        for (int i = 0; i < qty; i++) {

                            OrderedItem orderedItem = new OrderedItem();
                            orderedItem.setParentItem(item);
                            dataHolder.addItemToOrder(orderedItem);
                            updateOrderInSharedPreferences();

                        }

                    }
                });
                view.setTag(this);
            }
        }
    }


    private class GetFavoriteMealsTask extends AsyncTask<Void, Void, String> {

        Customer customer;

        @Override
        protected void onPreExecute() {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            String customerString = sharedPreferences.getString("customer", "");
            if (customerString != null && !customerString.equals("")) {
                Gson gson = new Gson();
                customer = gson.fromJson(customerString, Customer.class);
                Log.e("DEBUG", " in fragment! customer: " + customerString);
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder response;
            try {
                URL url = new URL(ApplicationConstant.getAddress(ApplicationConstant.GET_FAVORITE_MEALS) + "?userId=" + customer.getId());
                response = new StringBuilder();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e("DEBUG", " in fragment! getFavorites " + conn.getResponseCode() + " : " + conn.getResponseMessage());
                    return null;
                }

                Log.e("DEBUG", " in fragment! after connection");
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));

                String line;
                while ((line = input.readLine()) != null) {
                    response.append(line + "\n");
                }

                input.close();

                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            Log.e("DEBUG", " in fragment! before return");
            return response.toString();
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<Meal>>() {
                }.getType();
                List<Meal> favoriteMeals = (ArrayList<Meal>) gson.fromJson(response, listType);
                DataHolder.getInstance().setFavoriteMeals(favoriteMeals);
                Log.e("FAVORITES", " in fragment! meals size: " + favoriteMeals.size());
                for (Meal meal : favoriteMeals) {
                    Log.e("FAVORITES", " in fragment! meal - " + meal.getTitle());
                }

                favoriteItems = DataHolder.getInstance().getFavoriteItems();

                if (favoriteItems != null) {
                    rvItems.setAdapter(new FavoriteItemsRecyclerViewAdapter(getActivity(), favoriteItems));
                    rvItems.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                }

                favoriteMeals = DataHolder.getInstance().getFavoriteMeals();

                if (favoriteMeals != null) {
                    rvMeals.setAdapter(new FavoritesRecyclerViewAdapter(getActivity(), favoriteMeals));
                    rvMeals.setLayoutManager(new LinearLayoutManager(getActivity()));
                }

                if ((favoriteMeals == null && favoriteItems == null) ||
                        (favoriteMeals != null && favoriteMeals.size() == 0
                                && favoriteItems != null && favoriteItems.size() == 0)) {
                    rlEmptyView.setVisibility(View.VISIBLE);
                } else {
                    if (favoriteItems != null && favoriteItems.size() == 0) {
                        tvFavoriteItems.setVisibility(View.GONE);
                    }

                    if (favoriteMeals != null && favoriteMeals.size() == 0) {
                        tvFavoriteMeals.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

        private class GetFavoriteItemsTask extends AsyncTask<Void, Void, String> {

            Customer customer;
            Context c;

            public GetFavoriteItemsTask(Context c) {
                this.c = c;
            }

            @Override
            protected void onPreExecute() {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                String customerString = sharedPreferences.getString("customer", "");
                if (customerString != null && !customerString.equals("")) {
                    Gson gson = new Gson();
                    customer = gson.fromJson(customerString, Customer.class);
                    Log.e("DEBUG", " in fragment! customer: " + customerString);
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                StringBuilder response;
                try {
                    URL url = new URL(ApplicationConstant.getAddress(ApplicationConstant.GET_FAVORITE_ITEMS) + "?userId=" + customer.getId());
                    response = new StringBuilder();
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Log.e("DEBUG", " in fragment! getFavoriteItems " + conn.getResponseCode() + " : " + conn.getResponseMessage());
                        return null;
                    }

                    Log.e("DEBUG", " in fragment! after connection");

                    BufferedReader input = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));

                    String line;
                    while ((line = input.readLine()) != null) {
                        response.append(line + "\n");
                    }

                    input.close();

                    conn.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                Log.e("DEBUG", " in fragment! before return");
                return response.toString();
            }

            @Override
            protected void onPostExecute(String response) {
                Log.e("DEBUG", " in fragment! on post before check if null");
                if (response != null) {
                    Log.e("DEBUG", " in fragment! on post after check if null");
                    Gson gson = new Gson();
                    Type listType = new TypeToken<ArrayList<Item>>() {
                    }.getType();
                    List<Item> favoriteItems = (ArrayList<Item>) gson.fromJson(response, listType);
                    DataHolder.getInstance().setFavoriteItems(favoriteItems);
                    Log.e("FAVORITES", " in fragment! items size: " + favoriteItems.size());
                    for (Item item : favoriteItems) {
                        Log.e("FAVORITES", " in fragment! item - " + item.getTitle());
                    }


                    GetFavoriteMealsTask mealsTask = new GetFavoriteMealsTask();
                    mealsTask.execute();

                }
            }
        }

        public class ItemsListAdapter extends BaseAdapter {

            List<Item> items;
            Context context;
            int layout;

            public ItemsListAdapter(Context context, List<Item> items, int layout) {
                this.context = context;
                this.items = items;
                this.layout = layout;
            }

            @Override
            public int getCount() {
                if (items != null) {
                    return items.size();
                } else {
                    return 0;
                }
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int position, View view, ViewGroup viewGroup) {
                final ViewHolder holder;
                final Item item;

                if (view == null) {
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    view = inflater.inflate(layout, viewGroup, false);

                    // find the UI components of the cell
                    holder = new ViewHolder();
                    holder.title = (TextView) view.findViewById(R.id.tvFavoriteItem);

                    view.setTag(holder);
                } else {
                    holder = (ViewHolder) view.getTag();

                }

                // change the components to fit the current item that the cell should display
                item = items.get(position);
                holder.title.setText(item.getTitle());
                return view;
            }

            private class ViewHolder {
                TextView title;
            }
        }

        private void showSnackBar() {
            Snackbar snackbar = Snackbar
                    .make(v, getString(R.string.dialog_btn_keep_shopping_pressed), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.snack_bar_action_text), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent orderActivityIntent = new Intent(getActivity(), OrderActivity.class);
                            startActivity(orderActivityIntent);
                        }
                    });
            snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary));
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.white));
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.BLACK);
            snackbar.show();
        }


        private void updateOrderInSharedPreferences() {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("order", new Gson().toJson(DataHolder.getInstance().getTheOrder()));
            editor.apply();
        }
}


