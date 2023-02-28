package com.android.brogrammers.sportsm8.socialTab.friends;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.brogrammers.sportsm8.dataBaseConnection.apiServices.FriendshipsApiService;
import com.android.brogrammers.sportsm8.R;
import com.android.brogrammers.sportsm8.dataBaseConnection.repositories.AccountRepository;
import com.android.brogrammers.sportsm8.dataBaseConnection.repositories.FriendshipRepository;
import com.android.brogrammers.sportsm8.dataBaseConnection.repositories.impl.DatabaseAccountRepository;
import com.android.brogrammers.sportsm8.dataBaseConnection.repositories.impl.DatabaseFriendshipRepository;
import com.android.brogrammers.sportsm8.databinding.ActivitySearchNewFriendsBinding;
import com.android.brogrammers.sportsm8.databinding.ContentSearchNewFriendsBinding;
import com.android.brogrammers.sportsm8.socialTab.ClickListener;
import com.android.brogrammers.sportsm8.dataBaseConnection.APIUtils;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.UserInfo;
import com.android.brogrammers.sportsm8.dataBaseConnection.RetroFitClient;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class OnlyFriendsView extends AppCompatActivity implements SearchView.OnQueryTextListener, ClickListener, OnlyFriendsViewInterface {


    private Boolean search = false;
    private FriendsListAdapter adapter;
    private ActionMode actionMode;
    private final OnlyFriendsView.ActionModeCallBack actionModeCallBack = new OnlyFriendsView.ActionModeCallBack();

    private OnlyFriendsPresenter presenter;

    private ActivitySearchNewFriendsBinding binding;
    private ContentSearchNewFriendsBinding include;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new OnlyFriendsPresenter(this, new DatabaseFriendshipRepository(),AndroidSchedulers.mainThread());
        binding = ActivitySearchNewFriendsBinding.inflate(getLayoutInflater());
        include = binding.include;
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.toolbar);

        adapter = new FriendsListAdapter(getBaseContext(),null,presenter.friends,search);
        binding.searchviewNewFriends.setOnQueryTextListener(this);
        binding.searchviewNewFriends.setIconified(false);

        include.addNewFriendRecyclerview.setAdapter(adapter);
        include.addNewFriendRecyclerview.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        Bundle b = getIntent().getExtras();
        search = b.getBoolean("search");
        if (!search) {
            binding.searchviewNewFriends.setVisibility(View.GONE);
            binding.imageButtonAddGroupmembers.setVisibility(View.VISIBLE);
            actionMode = startActionMode((ActionMode.Callback) actionModeCallBack);
            presenter.getFriends();
        }
    }

    public void onClick(View view) {
        List<UserInfo> selectionFriends = new ArrayList<>();
        for(UserInfo friend : presenter.friends){
            if(friend.selected) selectionFriends.add(friend);
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("partyList", new ArrayList<>(selectionFriends));
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    public void createAdapter(){
        adapter = new FriendsListAdapter(getBaseContext(), this, presenter.friends, search);
        include.addNewFriendRecyclerview.setAdapter(adapter);
        include.addNewFriendRecyclerview.setLayoutManager(new LinearLayoutManager(getBaseContext()));
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        presenter.getSearchResults(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


    @Override
    public void onItemClicked(int position, Boolean fromGroup) {
        presenter.friends.get(position).selected ^= true;
        toggleSelection(position, false);
    }

    private void toggleSelection(int position, Boolean fromGroup) {
        adapter.toggleSelection(position);
        int count = adapter.getSelectedItemCount();
        actionMode.setTitle(String.valueOf(count));
        actionMode.invalidate();
    }


    private class ActionModeCallBack implements android.view.ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.selected_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.view.ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(android.view.ActionMode actionMode, MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.menu_add) {
                List<UserInfo> selectionFriends = new ArrayList<>();
                for (UserInfo friend : presenter.friends) {
                    if (friend.selected) selectionFriends.add(friend);
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("partyList", new ArrayList<>(selectionFriends));
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(android.view.ActionMode actionMode) {
                finish();
        }

    }


    @Override
    public boolean onItemLongClicked(int position, Boolean fromGroup) {
        return false;
    }

}
