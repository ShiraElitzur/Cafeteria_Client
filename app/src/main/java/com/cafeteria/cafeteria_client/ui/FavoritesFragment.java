package com.cafeteria.cafeteria_client.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cafeteria.cafeteria_client.R;
import com.cafeteria.cafeteria_client.utils.ApplicationConstant;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shira Elitzur on 08/09/2016.
 */
public class FavoritesFragment extends Fragment {

    private List<String> fakeFavorites;
    private RecyclerView rvFavorites;
    private List<Integer> colors;
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


        new GetFavoritesTask().execute();

        colors = new ArrayList<>();
        colors.add(R.color.colorIconBg0);
        colors.add(R.color.colorIconBg1);
        colors.add(R.color.colorIconBg2);
        colors.add(R.color.colorIconBg3);
        colors.add(R.color.colorIconBg4);

        fakeFavorites = new ArrayList<>();
        fakeFavorites.add("שניצל בצלחת");
        fakeFavorites.add("פרגית בבגט");
        fakeFavorites.add("פסטה בולונז");
        fakeFavorites.add("מעורב בצלחת");
        fakeFavorites.add("נודלס ירקות");
        fakeFavorites.add("המבורגר צמחוני");

        rvFavorites = (RecyclerView) v.findViewById(R.id.rvFavorites);
        rvFavorites.setAdapter(new FavoritesRecyclerViewAdapter(getActivity(),fakeFavorites));

        rvFavorites.setLayoutManager(new LinearLayoutManager(getActivity()));
        return v;
    }


    public class FavoritesRecyclerViewAdapter extends RecyclerView.Adapter<FavoritesRecyclerViewAdapter.CustomViewHolder> {

        private Context context;
        List<String> favorites;

        public FavoritesRecyclerViewAdapter(Context context, List<String> favorites) {
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
            String favorite = favorites.get(position);
            holder.title.setText(favorite);


        }


        @Override
        public int getItemCount() {
            return (null != favorites ? favorites.size() : 0);
        }

        class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, View.OnLongClickListener,
                MenuItem.OnMenuItemClickListener {

            protected TextView title;
            protected ImageView symbol;

            public CustomViewHolder(View view) {
                super(view);
                this.title = (TextView) view.findViewById(R.id.tvFavoriteTitle);
                this.symbol = (ImageView) view.findViewById(R.id.ivSymbol);

                if( colorIndex == 4 ) {
                    colorIndex = -1;
                }

                this.title.setTextColor(getResources().getColor(colors.get(++colorIndex)));
                view.setOnLongClickListener(this);
                view.setOnCreateContextMenuListener(this);
                view.setTag(this);
            }

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return false;
            }

            @Override

            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            }


            @Override
            public boolean onLongClick(View view) {
                int position = getAdapterPosition();

                return true;
            }
        }
    }

    private class GetFavoritesTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder response;
            try {
                URL url = new URL(ApplicationConstant.GET_FAVORITES);
                response = new StringBuilder();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e("DEBUG","getFavorites " +conn.getResponseCode() + " : "+ conn.getResponseMessage() );
                    return null;
                }

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
            return response.toString();
        }
    }

}
