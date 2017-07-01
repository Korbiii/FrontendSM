package com.android.brogrammers.sportsm8.SocialTab.Groups;

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

import com.android.brogrammers.sportsm8.R;
import com.android.brogrammers.sportsm8.SocialTab.FragmentSocial;
import com.android.brogrammers.sportsm8.DataBaseConnection.RetroFitDatabase.APIService;
import com.android.brogrammers.sportsm8.DataBaseConnection.RetroFitDatabase.APIUtils;
import com.android.brogrammers.sportsm8.DataBaseConnection.RetroFitDatabase.DatabaseClasses.Group;
import com.android.brogrammers.sportsm8.DataBaseConnection.RetroFitDatabase.RetroFitClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupsListFragment extends Fragment {
    private List<Group> groups;
    private RecyclerView recyclerView;
    private GroupListAdapter adapter;
    private FragmentSocial fragmentSocial;
    private APIService apiService = APIUtils.getAPIService();
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
        apiService.getGroups("getGroups", email).enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                RetroFitClient.storeObjectList(new ArrayList<Object>(response.body()), "groups", getContext());
                updateUI();
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
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