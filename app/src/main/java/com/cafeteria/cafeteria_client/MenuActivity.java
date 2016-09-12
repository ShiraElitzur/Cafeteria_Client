package com.cafeteria.cafeteria_client;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cafeteria.cafeteria_client.data.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shira Elitzur on 08/09/2016.
 *
 * The activity that shows the menu items. Contains three tabs, as options how to display the items:
 * 1. Sorted by categories 2. 3.
 * This activity extends the DrawerActivity to support the application navigation drawer
 */
public class MenuActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories); // The layout with the tabs
        super.onCreateDrawer();
        getSupportActionBar().setTitle("");
        // Sets the title as empty string because the default behavior is to display as title the app name.
        // Right now we have a background image on the action bar that contains the app name -
        // Need to think about that point
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.logo));

        CustomPagerAdapter adapter = new CustomPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        // Adding to the adapter the three fragments and their titles
        adapter.addFragment(new FavoritesFragment(), getResources().getString(R.string.favorites_tab_title));
        adapter.addFragment(new CategoriesFragment(), getResources().getString(R.string.categories_tab_title));
        adapter.addFragment(new SpecialsFragment(), getResources().getString(R.string.specials_tab_title));
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * Pager is the element that manages and displays the tabs
     */
    static class CustomPagerAdapter extends FragmentStatePagerAdapter {

        /**
         * List of the fragments in this pager
         */
        private final List<Fragment> fragmentList = new ArrayList<>();

        /**
         * List of titles to display at the head of each fragment tab
         */
        private final List<String> fragmentTitleList = new ArrayList<>();


        public CustomPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Returns the fragment in a given position
         * @param position
         * @return
         */
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        /**
         * Returns the amount of tabs in the pager
         * @return
         */
        @Override
        public int getCount() {
            return fragmentList.size();
        }

        /**
         * Returns the page title of specific tab (by position)
         * @param position
         * @return
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }

        /**
         * Adds fragment to the pager
         * @param fragment
         * @param title
         */
        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }
    }


}
