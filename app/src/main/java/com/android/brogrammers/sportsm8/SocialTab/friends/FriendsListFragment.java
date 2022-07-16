package com.android.brogrammers.sportsm8.SocialTab.Friends;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.brogrammers.sportsm8.DataBaseConnection.ApiServices.FriendshipsApiService;
import com.android.brogrammers.sportsm8.R;
import com.android.brogrammers.sportsm8.SocialTab.FragmentSocial;
import com.android.brogrammers.sportsm8.DataBaseConnection.APIUtils;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.UserInfo;
import com.android.brogrammers.sportsm8.DataBaseConnection.RetroFitClient;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableSingleObserver;

public class FriendsListFragment extends Fragment {

    private List<UserInfo> friends;
    private RecyclerView recyclerView;
    private FriendsListAdapter adapter;
    private FragmentSocial fragmentSocial;
    private FriendshipsApiService apiService = APIUtils.getFriendshipsAPIService();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        //Declaration Variables
        friends = new ArrayList<>();
        fragmentSocial = (FragmentSocial) getParentFragment();
        //Declaration Views
        recyclerView = (RecyclerView) view.findViewById(R.id.friends_recycler_view);
        updateUI();
        return view;
    }

    public void updateFriendsList() {
        SharedPreferences sharedPrefs = getContext().getSharedPreferences("loginInformation", Context.MODE_PRIVATE);
        String email = sharedPrefs.getString("email", "");
        apiService.getFriends(email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<UserInfo>>() {
                    @Override
                    public void onSuccess(@NonNull List<UserInfo> userInfos) {
                        RetroFitClient.storeObjectList(new ArrayList<Object>(userInfos), "friends", getContext());
                        updateUI();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
    }

    public void updateUI() {

        friends = (ArrayList<UserInfo>) RetroFitClient.retrieveObjectList("friends", getContext(), new TypeToken<ArrayList<UserInfo>>() {
        }.getType());
        if (friends == null)  friends = new ArrayList<>();
        adapter = new FriendsListAdapter(getContext(), null, friends, false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentSocial.setReferencesFriends(friends, this, adapter);
        adapter.notifyDataSetChanged();
        fragmentSocial.setSwipeRefreshLayout(false);
    }
}