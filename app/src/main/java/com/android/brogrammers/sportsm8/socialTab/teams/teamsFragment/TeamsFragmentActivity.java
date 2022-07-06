package com.android.brogrammers.sportsm8.socialTab.teams.teamsFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.brogrammers.sportsm8.R;
import com.android.brogrammers.sportsm8.socialTab.FragmentSocial;
import com.android.brogrammers.sportsm8.socialTab.teams.teamsDetailView.TeamDetailActivity;
import com.android.brogrammers.sportsm8.socialTab.teams.teamsFragment.adapter.TeamsListAdapter;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Team;
import com.android.brogrammers.sportsm8.dataBaseConnection.repositories.impl.DatabaseTeamsRepository;

import java.util.ArrayList;
import java.util.List;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class TeamsFragmentActivity extends Fragment implements TeamsFragmentView {

    private FragmentSocial fragmentSocial;
    private RecyclerView recyclerView;
    TeamsFragmentPresenter presenter;
    TeamsListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teams, container, false);
        fragmentSocial = (FragmentSocial) getParentFragment();
        recyclerView = (RecyclerView) view.findViewById(R.id.teams_recycler_view);
        presenter = new TeamsFragmentPresenter(this, new DatabaseTeamsRepository(), AndroidSchedulers.mainThread());
        adapter = new TeamsListAdapter(getContext(), null, new ArrayList<Team>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        presenter.loadTeams();
        adapter.getPositionClicks()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Team>() {
                    @Override
                    public void accept(@NonNull Team team) throws Exception {
                        Intent intent = new Intent(getContext(), TeamDetailActivity.class);
                        intent.putExtra("Team", team);
                        getContext().startActivity(intent);
                    }
                });
        return view;
    }


    @Override
    public void displayTeams(List<Team> teams) {
        adapter.setTeamList(teams);
        adapter.notifyDataSetChanged();
    }
}
