package com.example.alex.helloworld.GamePicker;

/**
 * Created by alex on 10/30/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.alex.helloworld.Unused_Inactive.AddSport;
import com.example.alex.helloworld.Unused_Inactive.GamePickerLiga;
import com.example.alex.helloworld.CreateNewMeeting.CreateNewMeeting;
import com.example.alex.helloworld.Home;
import com.example.alex.helloworld.Information;
import com.example.alex.helloworld.Invites;
import com.example.alex.helloworld.LoginScreen;
import com.example.alex.helloworld.R;
import com.example.alex.helloworld.Unused_Inactive.SportAttributes;
import com.example.alex.helloworld.databaseConnection.AsyncResponse;
import com.example.alex.helloworld.databaseConnection.DBconnection;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.example.alex.helloworld.activities.AccountPage;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Sport extends AppCompatActivity {


    ArrayList<Information> sportIDs;
    ArrayList<SportAttributes> attributes;
    Spinner mySpinner;
    int sportID;
    String[] sportArten;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sportart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Sport.this, AddSport.class);
                startActivity(intent);
            }
        });
        try {
            createList();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        navigationView();


    }


    public void funGame(View v) {
        Bundle b = new Bundle();
        b.putInt("sportID", sportID);
        b.putBoolean("liga", false);
        Intent intent = new Intent(this, CreateNewMeeting.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void ligaGame(View v) {
        Bundle b = new Bundle();
        b.putInt("sportID", sportID);
        b.putBoolean("liga", true);
        Intent intent = new Intent(this, GamePickerLiga.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void training(View v) {
        Bundle b = new Bundle();
        b.putInt("sportID", sportID);
    }


    private void initSpinner() {
        mySpinner = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<String> myAdapater = new ArrayAdapter<>(Sport.this, R.layout.item_custom_spinner, sportArten);
        myAdapater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapater);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //update(mySpinner.getSelectedItem().toString());
                update(position);
                sportID = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void update(int sID) {

        Button training = (Button) findViewById(R.id.button_training);
        Button fSpiel = (Button) findViewById(R.id.button_funGame);
        ImageView iV = (ImageView) findViewById(R.id.imageView_sportart);

        Information temp = sportIDs.get(sID);
        Resources res = getResources();
        TypedArray draws = res.obtainTypedArray(R.array.sportDrawables);
        iV.setImageDrawable(draws.getDrawable(Integer.parseInt(temp.sportID)));

        fSpiel.setVisibility(View.GONE);
        training.setVisibility(View.GONE);

        if (Integer.valueOf(sportIDs.get(sID).team) == 1) {
            fSpiel.setVisibility(View.VISIBLE);
            training.setVisibility(View.GONE);
        } else {
            fSpiel.setVisibility(View.GONE);
            training.setVisibility(View.VISIBLE);
        }

    }

    private void createList() throws ExecutionException, InterruptedException {
        String[] params = {"/IndexSports.php", "function", "getData"};
        new DBconnection(new AsyncResponse() {
            @Override
            public void processFinish(String output) throws ParseException, JSONException {
                parseToArrayList(output);
            }
        }).execute(params);


    }

    private void parseToArrayList(String jsonObjectSring) throws JSONException {
       if (jsonObjectSring!=null) {
           ArrayList<Information> data = new ArrayList<>();
           JSONObject jsonObject = new JSONObject(jsonObjectSring);

           int i = 0;
           while (jsonObject.has("" + i)) {
               String meetingString = jsonObject.get("" + i).toString();
               Gson gson = new Gson();
               Information current = gson.fromJson(meetingString, Information.class);
               data.add(current);
               i++;
           }

           sportArten = new String[data.size()];
           for (int j = 0; j < data.size(); j++) {
               sportArten[j] = data.get(j).sportname;
           }
           sportIDs = data;
           initSpinner();
       }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return false;
    }

    //Implements Navigation View
    private void navigationView() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout2);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNavigationView.getMenu().getItem(3).setChecked(true);

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

                return Sport.super.onOptionsItemSelected(item);
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
}
