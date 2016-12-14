package com.example.alex.helloworld.Friends;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.alex.helloworld.GroupDetailView;
import com.example.alex.helloworld.Information;
import com.example.alex.helloworld.R;
import com.example.alex.helloworld.SlidingTabLayout.SlidingTabLayout;

import java.util.ArrayList;

//Implements ClickListenerInterface
public class Friends extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, ViewPager.OnPageChangeListener, ClickListener {
    private ArrayList<Information> friends, groups;
    private FriendsListFragment friendsListFragment;
    private GroupsListFragment groupsListFragment;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textView_selected_count;
    private ImageButton decline_selection, page_button;
    private Boolean addToMeetingMode, newGroupMode = false;
    private ViewPager pager;
    private ViewPagerAdapter viewPagerAdapter;
    private SlidingTabLayout tabs;
    private CharSequence Titles[] = {"Friends", "Groups"};
    private int NumOfTabs = 2;

    Toolbar toolbar;
    AppBarLayout.LayoutParams params;
    AppBarLayout.LayoutParams params2;

    //Actionmode
    private ActionMode actionMode;
    //private Class ActionModeCallBack is for handling action for selected Items
    private Friends.ActionModeCallBack actionModeCallBack = new Friends.ActionModeCallBack();
    private FriendsListAdapter adapterReference;
    private GroupListAdapter adapterReferenceGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Declarations Variables
        Bundle bundle = getIntent().getExtras();
        addToMeetingMode = bundle.getBoolean("SelectionMode");
        if (addToMeetingMode) {
            actionMode = startSupportActionMode(actionModeCallBack);
        }

        friends = new ArrayList<>();
        groups = new ArrayList<>();
        //Declarations Views
        page_button = (ImageButton) findViewById(R.id.add_new_friend);
        textView_selected_count = (TextView) findViewById(R.id.selected_friends_number);
        decline_selection = (ImageButton) findViewById(R.id.discard_selection_button);
        pager = (ViewPager) findViewById(R.id.pager);
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.friends_refresh);
        //Hiding Keyboard on Startup
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //Setting OnRefreshListener
        swipeRefreshLayout.setOnRefreshListener(this);
        //Setting up ViewPager
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, NumOfTabs);
        pager.addOnPageChangeListener(this);
        pager.setAdapter(viewPagerAdapter);
        tabs.setDistributeEvenly(true);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return ContextCompat.getColor(getBaseContext(), R.color.colorAccent);
            }
        });
        tabs.setViewPager(pager);
        page_button.setImageResource(R.drawable.ic_person_add_white_24dp);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params2 = (AppBarLayout.LayoutParams) tabs.getLayoutParams();
    }

    /**
     * Handles clicks inside activity
     *
     * @param view
     */
    public void onClick(View view) {
        Bundle b = new Bundle();
        b.putBoolean("search", true);
        Intent intent = new Intent(this, OnlyFriendsView.class);
        intent.putExtras(b);
        startActivity(intent);

    }

    /**
     * For when Activity is called, from CreateNewMeeting
     */
    private void finishSelection() {
        ArrayList<Information> selectionfriends = new ArrayList<>();
        ArrayList<Information> selectiongroups = new ArrayList<>();
        for (int i = 0; i < friends.size(); i++) {
            if (friends.get(i).selected) {
                selectionfriends.add(friends.get(i));
            }
        }
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).selected) {
                selectiongroups.add(groups.get(i));
            }
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("partyList", selectionfriends);
        bundle.putSerializable("groupList", selectiongroups);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Creates new Group from Friendselection
     */
    private void createGroup() {
        ArrayList<Information> selection = new ArrayList<>();
        for (int i = 0; i < friends.size(); i++) {
            if (friends.get(i).selected) {
                selection.add(friends.get(i));
            }
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("GroupList", selection);
        Intent intent = new Intent(this, CreateGroup.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, 1);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Refreshes friends and groups on swipe down
     */
    @Override
    public void onRefresh() {
        friendsListFragment.updateFriendsList();
        groupsListFragment.updateGroupList();
    }

    /**
     * Exits actionMode, when Grouppage is selected.
     *
     * @param position friends = 1, groups = 2
     */
    @Override
    public void onPageSelected(int position) {
        if (position == 1 && actionMode != null && !addToMeetingMode) {
            adapterReference.clearSelection();
            actionMode.finish();
            actionMode = null;
        }

    }

    /**
     * Help function, for stopping loadinganimation, when updateUI from FriendslistFragment is finished
     *
     * @param bool false = stop loading; true = continue loading
     */
    public void setSwipeRefreshLayout(boolean bool) {
        swipeRefreshLayout.setRefreshing(bool);
    }

    /**
     * Clicklistener Interface Method. Gets called, when a friendcard is clicked
     *
     * @param position  position of clicked Item
     * @param fromGroup origin of methodCall. true: GroupAdapter.java; false: FriendsAdapter
     */
    @Override
    public void onItemClicked(int position, Boolean fromGroup) {
        if (!fromGroup) {
            if (actionMode != null && Integer.valueOf(friends.get(position).confirmed) == 1) {
                friends.get(position).selected ^= true;
                toggleSelection(position, fromGroup);
            }
        } else if (actionMode != null && fromGroup) {
            groups.get(position).selected ^= true;
            toggleSelection(position, fromGroup);
        }
    }

    /**
     * Clicklistener Interface Method. Gets called, when a friendcard is long clicked. Starts selectionMode (actionmode), when a confirmed friend is clicked.
     *
     * @param position  position of clicked Item
     * @param fromGroup origin of methodCall. true: GroupAdapter.java; false: FriendsAdapter
     * @return
     */
    @Override
    public boolean onItemLongClicked(int position, Boolean fromGroup) {
        if (actionMode == null && Integer.valueOf(friends.get(position).confirmed) == 1) {
            int i = 0;
            while (Integer.valueOf(friends.get(i).confirmed) == 0) {
                i++;
            }
            adapterReference.removeRange(0, i);
            actionMode = startSupportActionMode(actionModeCallBack);
        }
        return false;
    }

    /**
     * Handles the Itemselection. Updates count and colors the card.
     *
     * @param position  position of clicked Item
     * @param fromGroup origin of methodCall. true: GroupAdapter.java; false: FriendsAdapter
     */
    private void toggleSelection(int position, Boolean fromGroup) {
        if (!fromGroup) adapterReference.toggleSelection(position);
        else adapterReferenceGroup.toggleSelection(position);
        int count = adapterReference.getSelectedItemCount() + adapterReferenceGroup.getSelectedItemCount();
        if (count == 0 && !addToMeetingMode) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    /**
     * This method is for setting the friend references. It gets called from Friendslistfragment with getActivity()
     *
     * @param friends             data of Friends
     * @param friendsListFragment Reference to friendsListFragment
     * @param adapter             Reference to FriendsListAdapter
     */
    public void setReferencesFriends(ArrayList<Information> friends, FriendsListFragment friendsListFragment, FriendsListAdapter adapter) {
        this.adapterReference = adapter;
        //throws error
        //adapterReference.setClicklistener(this);
        this.friendsListFragment = friendsListFragment;
        this.friends = friends;
        if (addToMeetingMode) {
            int i = 0;
            while (Integer.valueOf(friends.get(i).confirmed) == 0) {
                i++;
            }
            adapter.removeRange(0, i);
        }
    }

    /**
     * This method is for setting the friend references. It gets called from Friendslistfragment with getActivity()
     *
     * @param groups             data of groups
     * @param groupsListFragment Reference to groupListFragment
     * @param adapter            Reference to GroupListAdapter
     */
    public void setReferencesGroups(ArrayList<Information> groups, GroupsListFragment groupsListFragment, GroupListAdapter adapter) {
        this.adapterReferenceGroup = adapter;
        this.adapterReferenceGroup.setSelectionMode(addToMeetingMode);
        //throws error
        //this.adapterReferenceGroup.setClickListener(this);
        this.groups = groups;
        this.groupsListFragment = groupsListFragment;
    }

    /**
     * Is creating an contextual Actionbar.
     */
    private class ActionModeCallBack implements android.support.v7.view.ActionMode.Callback {

        /**
         * Called when action mode is first created.
         * inflates menu layout inside contextual actionbar
         *
         * @param mode
         * @param menu
         * @return
         */
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.selected_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        /**
         * Called to report a user click on an action button.
         *
         * @param mode
         * @param item clicked Button
         * @return
         */
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_add:
                    if (addToMeetingMode) {
                        finishSelection();
                    } else {
                        createGroup();
                    }
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        /**
         * Called when an action mode is about to be exited and destroyed.
         *
         * @param mode
         */
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (addToMeetingMode) {
                finish();
            }
            adapterReference.clearSelection();
            actionMode = null;
        }
    }

    //Unused Override Methods
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }


    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
