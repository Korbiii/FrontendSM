package com.android.brogrammers.sportsm8.socialTab.groups;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.brogrammers.sportsm8.dataBaseConnection.apiServices.GroupsApiService;
import com.android.brogrammers.sportsm8.R;
import com.android.brogrammers.sportsm8.socialTab.FragmentSocial;
import com.android.brogrammers.sportsm8.dataBaseConnection.APIUtils;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Group;
import com.android.brogrammers.sportsm8.dataBaseConnection.RetroFitClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableSingleObserver;

public class GroupsListFragment extends Fragment {
    private List<Group> groups;
    private RecyclerView recyclerView;
    private GroupListAdapter adapter;
    private FragmentSocial fragmentSocial;
    private GroupsApiService apiService = APIUtils.getGroupsAPIService();
    private Gson gson;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        //Views
        recyclerView = (RecyclerView) view.findViewById(R.id.group_recycler_view);
        //variables
        groups = new ArrayList<>();
        fragmentSocial = (FragmentSocial) getParentFragment();
        adapter = new GroupListAdapter(getContext(), null, groups);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        updateUI();
        return view;
    }

    public void updateGroupList() {
        final SharedPreferences sharedPrefs = getContext().getSharedPreferences("loginInformation", Context.MODE_PRIVATE);
        String email = sharedPrefs.getString("email", "");
        apiService.getGroups(email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Group>>() {
                    @Override
                    public void onSuccess(@NonNull List<Group> groups) {
                        RetroFitClient.storeObjectList(new ArrayList<Object>(groups), "groups", getContext());
                        updateUI();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
    }

    public void updateUI() {
        groups = (ArrayList<Group>) RetroFitClient.retrieveObjectList("groups", getContext(), new TypeToken<ArrayList<Group>>() {
        }.getType());
        if (groups != null) {
            adapter = new GroupListAdapter(getContext(), null, groups);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter.notifyDataSetChanged();
            fragmentSocial.setReferencesGroups(groups, this, adapter);
            fragmentSocial.setSwipeRefreshLayout(false);
        } else updateGroupList();
    }


}