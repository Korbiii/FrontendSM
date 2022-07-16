package com.android.brogrammers.sportsm8.SocialTab.Teams.TeamsFragment;

import android.content.Intent;
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
import com.android.brogrammers.sportsm8.SocialTab.Teams.TeamsDetailView.TeamDetailActivity;
import com.android.brogrammers.sportsm8.SocialTab.Teams.TeamsFragment.Adapter.TeamsListAdapter;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Team;
import com.android.brogrammers.sportsm8.DataBaseConnection.Repositories.impl.DatabaseTeamsRepository;

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
