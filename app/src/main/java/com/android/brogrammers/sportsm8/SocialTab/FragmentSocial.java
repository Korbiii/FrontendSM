package com.android.brogrammers.sportsm8.SocialTab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.android.brogrammers.sportsm8.SocialTab.Adapter.ViewPagerAdapter;
import com.android.brogrammers.sportsm8.SocialTab.Friends.FriendsListAdapter;
import com.android.brogrammers.sportsm8.SocialTab.Friends.FriendsListFragment;
import com.android.brogrammers.sportsm8.SocialTab.Friends.OnlyFriendsView;
import com.android.brogrammers.sportsm8.SocialTab.Groups.GroupListAdapter;
import com.android.brogrammers.sportsm8.SocialTab.Groups.GroupsListFragment;
import com.android.brogrammers.sportsm8.R;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Group;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.UserInfo;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentSocial.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentSocial#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSocial extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ViewPager.OnPageChangeListener, ClickListener{
    Activity parentActivity;
    private List<UserInfo> friends, selection;
    private List<Group> groups;
    private FriendsListFragment friendsListFragment;
    private GroupsListFragment groupsListFragment;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Boolean addToMeetingMode = false, newGroupMode = false;
    private ViewPager pager;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabs;


    private CharSequence Titles[] = {"Friends", "Groups", "Teams"};
    private int NumOfTabs = 3;

    Toolbar toolbar;

    //Actionmode
    private ActionMode actionMode;
    //private Class ActionModeCallBack is for handling action for selected Items

    private FragmentSocial.ActionModeCallBack actionModeCallBack = new FragmentSocial.ActionModeCallBack();
    private FriendsListAdapter adapterReference;
    private GroupListAdapter adapterReferenceGroup;


    private OnFragmentInteractionListener mListener;

    public FragmentSocial() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentSocial.
     */
    public static FragmentSocial newInstance() {

        return new FragmentSocial();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = getActivity();

        //Declarations Variables
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            addToMeetingMode = bundle.getBoolean("addToMeetingMode");
            try {
                selection = (ArrayList<UserInfo>) bundle.getSerializable("Selection");
            } catch (NullPointerException e) {
            }
        }
        if (addToMeetingMode) {
            actionMode = ((AppCompatActivity) parentActivity).startSupportActionMode(actionModeCallBack);
            NumOfTabs=2;
        }
        friends = new ArrayList<>();
        groups = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_social, container, false);
        //Declarations Views
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        //  ((AppCompatActivity) parentActivity).setSupportActionBar(toolbar);


        pager = (ViewPager) rootView.findViewById(R.id.pager);
        tabs = (TabLayout) rootView.findViewById(R.id.tabs);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.friends_refresh);
        //Hiding Keyboard on Startup
        parentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //Setting OnRefreshListener
        swipeRefreshLayout.setOnRefreshListener(this);

        //Setting up ViewPager
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), Titles, NumOfTabs);
        pager.addOnPageChangeListener(this);
        pager.setAdapter(viewPagerAdapter);
        tabs.setupWithViewPager(pager);
//        page_button.setImageResource(R.drawable.ic_person_add_white_24dp);

//        params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
//        params2 = (AppBarLayout.LayoutParams) tabs.getLayoutParams();

        return rootView;
    }

    public void setAddToMeetingMode(Boolean bool) {
        addToMeetingMode = true;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Handles clicks inside activity
     *
     * @param view
     */
    public void onClick(View view) {
        Bundle b = new Bundle();
        b.putBoolean("search", true);
        Intent intent = new Intent(parentActivity, OnlyFriendsView.class);
        intent.putExtras(b);
        startActivity(intent);

    }

    /**
     * For when Activity is called, from CreateNewMeetingOld
     */
    private void finishSelection() {
        List<UserInfo> selectionfriends = new ArrayList<>();
        List<Group> selectiongroups = new ArrayList<>();
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
        bundle.putSerializable("partyList", new ArrayList<>(selectionfriends));
        bundle.putSerializable("groupList", new ArrayList<>(selectiongroups));
        Intent intent = new Intent();
        intent.putExtras(bundle);
        parentActivity.setResult(RESULT_OK, intent);
        parentActivity.finish();
    }

    /**
     * Creates new Group from Friendselection
     */
    private void createGroup() {
        List<UserInfo> selection = new ArrayList<>();
        for (int i = 0; i < friends.size(); i++) {
            if (friends.get(i).selected) {
                selection.add(friends.get(i));
            }
        }

        CreateTeamGroup createGroupDialog = new CreateTeamGroup();
        Bundle bundle = new Bundle();
        bundle.putSerializable("GroupList", new ArrayList<>(selection));
        createGroupDialog.setArguments(bundle);
        createGroupDialog.show(getChildFragmentManager(), "createGroup");


       /* Bundle bundle = new Bundle();
        bundle.putSerializable("GroupList", selection);
        // change this
        Intent intent = new Intent(parentActivity, CreateGroup.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, 1);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        int position = tabs.getSelectedTabPosition();
        if (position == 0) friendsListFragment.updateFriendsList();
        else if (position == 1) groupsListFragment.updateGroupList();
        else if (position==2) swipeRefreshLayout.setRefreshing(false);
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
            friends.get(position).selected ^= true;
            adapterReference.toggleSelection(position);
            actionMode = ((AppCompatActivity) parentActivity).startSupportActionMode(actionModeCallBack);
        }
        return true;
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
    public void setReferencesFriends(List<UserInfo> friends, FriendsListFragment friendsListFragment, FriendsListAdapter adapter) {
        this.adapterReference = adapter;
        adapterReference.setClicklistener(this);
        this.friendsListFragment = friendsListFragment;
        this.friends = friends;
        if (addToMeetingMode) {
            int number = 0;
            for (int i = 0;i<friends.size();i++){
                if(friends.get(i).confirmed==0) number++;
            }
            adapter.removeRange(0,number);
        }
        adapterReference.setSelected(selection);
        if (selection != null) {
            for (int i = 0; i < selection.size(); i++) {
                for (int j = 0; j < friends.size(); j++) {
                    if (friends.get(j).email.equals(selection.get(i).email)) {
                        friends.get(j).selected = true;
                    }
                }
            }
        }
    }

    /**
     * This method is for setting the friend references. It gets called from Friendslistfragment with getActivity()
     *
     * @param groups             data of groups
     * @param groupsListFragment Reference to groupListFragment
     * @param adapter            Reference to GroupListAdapter
     */
    public void setReferencesGroups(List<Group> groups, GroupsListFragment groupsListFragment, GroupListAdapter adapter) {
        this.adapterReferenceGroup = adapter;
        this.adapterReferenceGroup.setSelectionMode(addToMeetingMode);

        this.adapterReferenceGroup.setClickListener(this);
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
                parentActivity.finish();
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
