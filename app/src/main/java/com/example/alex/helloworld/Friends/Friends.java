package com.example.alex.helloworld.Friends;

import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.alex.helloworld.Information;
import com.example.alex.helloworld.R;
import com.example.alex.helloworld.SlidingTabLayout.SlidingTabLayout;
import com.example.alex.helloworld.databaseConnection.DBconnection;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class Friends extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private DBconnection dBconnection;
    private EditText editText;
    private ArrayList<String> members;
    private ArrayList<Information> friends;
    private ArrayList<Information> selected;
    private FriendsListFragment friendsListFragment;
    TextView textView_selected_count;
    ImageButton decline_selection;
    RecyclerView recyclerView;
    FriendsListAdapter adapter;
    private Boolean selectionMode;
    FloatingActionButton fab;
    ViewPager pager;
    ViewPagerAdapter viewPagerAdapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"Friends", "Groups"};
    int NumOfTabs = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        selectionMode = bundle.getBoolean("SelectionMode");

        fab = (FloatingActionButton) findViewById(R.id.fab);
        textView_selected_count = (TextView) findViewById(R.id.selected_friends_number);
        decline_selection = (ImageButton) findViewById(R.id.discard_selection_button);
         fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectionMode) {
                        finishSelection();
                    } else {
                        createGroup();
                    }
                }
            });
        if(selectionMode) {
            fab.setVisibility(View.VISIBLE);
        }else{
            fab.setVisibility(View.GONE);
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        editText = (EditText) findViewById(R.id.search_friends);
        members = new ArrayList<>();
        selected = new ArrayList<>();
        friends = new ArrayList<>();

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, NumOfTabs);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(viewPagerAdapter);
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return ContextCompat.getColor(getBaseContext(), R.color.colorAccent);
            }
        });
        tabs.setViewPager(pager);

    }

    public void setReference(FriendsListFragment friendsListFragment) {
        this.friendsListFragment = friendsListFragment;
    }

    public void activateSelectionMode(Boolean bool, int count) {

        textView_selected_count.setVisibility(View.VISIBLE);
        textView_selected_count.setText(count + " ausgewählt");
        decline_selection.setVisibility(View.VISIBLE);
        if (bool) {
            fab.setVisibility(View.VISIBLE);
            fab.setEnabled(true);
        } else {
            fab.setVisibility(View.GONE);
            fab.setEnabled(false);
        }
    }

    public void updateCount(int count) {
        textView_selected_count.setText(count + " ausgewählt");
    }

    public void onClick(View view) {
        textView_selected_count.setVisibility(View.GONE);
        decline_selection.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);
        friendsListFragment.declineSelection();
    }

    public boolean getSelectionMode() {
        return selectionMode;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void finishSelection() {

        ArrayList<Information> selection = new ArrayList<>();
        for (int i = 0;i<friends.size();i++){
            if(friends.get(i).selected){
               selection.add(friends.get(i)) ;
            }
        }


        Bundle bundle = new Bundle();
        bundle.putSerializable("partyList", selection);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void createGroup() {

        ArrayList<Information> selection = new ArrayList<>();
        for (int i = 0;i<friends.size();i++){
            if(friends.get(i).selected){
                selection.add(friends.get(i)) ;
            }
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("GroupList", friends);
        Intent intent = new Intent(this,CreateGroup.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void setData(ArrayList<Information> data) {
        friends = data;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (adapter != null) {
            int pos = adapter.search(newText);
            recyclerView.scrollToPosition(pos);
            return true;
        }
        return false;

    }


}
