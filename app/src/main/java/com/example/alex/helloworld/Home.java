package com.example.alex.helloworld;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;

import android.view.Display;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.alex.helloworld.CalendarActivity.CalendarActivity;
import com.example.alex.helloworld.CalendarActivity.KalendarActivity;
import com.example.alex.helloworld.GamePicker.Sport;
import com.example.alex.helloworld.activities.AccountPage;
import com.example.alex.helloworld.Friends.Friends;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

/**
 * Created by alex on 10/30/2016.
 */

public class Home extends AppCompatActivity {


    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        navigationView();
        buttons();
    }
    
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.sportart_button:
                intent = new Intent(this, Sport.class);
                startActivity(intent);
                break;
            case R.id.einladungen_button:
                intent = new Intent(this, Invites.class);
                startActivity(intent);
                break;
            case R.id.msg_button:
                intent = new Intent(this, AccountPage.class);
                startActivity(intent);
                break;
            case R.id.calendar_button:
                intent= new Intent(this, KalendarActivity.class);
                startActivity(intent);
                break;
            case R.id.friends_button:
                Bundle bundle = new Bundle();
                bundle.putBoolean("SelectionMode",false);
                intent = new Intent(this,Friends.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            default:
                finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_logout:
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
                Home.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //Implements Navigation View
    private void navigationView() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mNavigationView.getMenu().getItem(1).setChecked(true);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NotNull MenuItem item) {
                int id = item.getItemId();
                Intent intent;
                switch (id) {

                    case R.id.nav_account:
                        intent = new Intent(getApplicationContext(), AccountPage.class);
                        startActivity(intent);
                        closeNavDrawer();
                        return true;
                    case R.id.nav_logout:
                        //delete locally saved userinformation
                        SharedPreferences sharedPrefs = getSharedPreferences("loginInformation", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor =  sharedPrefs.edit();
                        editor.clear();
                        editor.apply();

                        intent = new Intent(getApplicationContext(), LoginScreen.class);
                        startActivity(intent);
                        closeNavDrawer();
                        return true;
                    case R.id.nav_invite:
                        intent = new Intent(getApplicationContext(), Invites.class);
                        startActivity(intent);
                        closeNavDrawer();
                        return true;
                    case R.id.nav_sportart:
                        intent = new Intent(getApplicationContext(), Sport.class);
                        startActivity(intent);
                        closeNavDrawer();
                        return true;
                    case R.id.nav_home:
                        intent = new Intent(getApplicationContext(), Home.class);
                        startActivity(intent);
                        closeNavDrawer();
                        return true;


                }

                return Home.super.onOptionsItemSelected(item);
            }
        });
    }

    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else {
            super.onBackPressed();
        }
    }

    protected boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    protected void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }


    private void buttons() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        double width = size.x;
        width = width * 0.93;
        int widthh = (int) width;
        int height = size.y;
        Button sport = (Button) findViewById(R.id.sportart_button);
        Button einlad = (Button) findViewById(R.id.einladungen_button);
        Button msg = (Button) findViewById(R.id.msg_button);
        Button calendar = (Button) findViewById(R.id.calendar_button);
        sport.setWidth(widthh / 2);
        sport.setHeight(widthh / 2);

        einlad.setWidth(widthh / 2);
        einlad.setHeight(widthh / 2);

        msg.setWidth(widthh / 2);
        msg.setHeight(widthh / 2);

        calendar.setWidth(widthh / 2);
        calendar.setHeight(widthh / 2);
    }

}

