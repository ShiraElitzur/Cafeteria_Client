package com.cafeteria.cafeteria_client.ui;

import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.cafeteria.cafeteria_client.R;

/**
 * An activity with Navigation Drawer
 * An activity that extends that class must have in it's layout xml file the following components, with the specified ids:
 * 1. DrawerLayout - drawer_layout
 * 2. ToolBar - toolbar
 * 3. NavigationView - navigation_view
 */
public abstract class DrawerActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    protected void onCreateDrawer() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        // Sets the title as empty string because the default behavior is to display as title the app name.
        // Right now we have a background image on the action bar that contains the app name -
        // Need to think about that point.
        getSupportActionBar().setTitle("");
        getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.logo));

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                Toast.makeText(DrawerActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
//            case R.id.action_settings:
//                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
