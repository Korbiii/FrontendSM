package com.android.brogrammers.sportsm8.SocialViews.Teams.TeamsDetailView;

import com.android.brogrammers.sportsm8.R;
import com.android.brogrammers.sportsm8.databaseConnection.RetroFitDatabase.DatabaseClasses.Team;
import com.android.brogrammers.sportsm8.databaseConnection.RetroFitDatabase.DatabaseClasses.UserInfo;
import com.android.brogrammers.sportsm8.repositories.impl.DatabaseTeamsRepository;
import com.mypopsy.maps.StaticMap;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Korbi on 20.06.2017.
 */

public class TeamDetailPresenter {


    private final DatabaseTeamsRepository teamsRepository;
    private final TeamDetailView view;
    private final Scheduler mainScheduler;
    private CompositeDisposable disposable = new CompositeDisposable();

    public TeamDetailPresenter(DatabaseTeamsRepository databaseTeamsRepository, TeamDetailView teamDetailView, Scheduler mainScheduler) {
        this.teamsRepository = databaseTeamsRepository;
        this.view = teamDetailView;
        this.mainScheduler = mainScheduler;
    }


    public void loadTeamMembers(Team team) {
        disposable.add(teamsRepository.getTeamMembers(team)
                .subscribeOn(Schedulers.io())
                .observeOn(mainScheduler)
                .subscribeWith(new DisposableSingleObserver<List<UserInfo>>() {
                    @Override
                    public void onSuccess(List<UserInfo> members) {
                        view.displayMembers(members);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                }));
    }



}
