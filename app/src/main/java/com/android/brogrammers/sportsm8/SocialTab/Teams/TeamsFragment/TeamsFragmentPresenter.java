package com.android.brogrammers.sportsm8.SocialTab.Teams.TeamsFragment;

import android.support.annotation.NonNull;

import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Team;
import com.android.brogrammers.sportsm8.DataBaseConnection.Repositories.impl.DatabaseTeamsRepository;

import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * Created by Korbi on 16.06.2017.
 */

class TeamsFragmentPresenter {


    private final TeamsFragmentView view;
    private DatabaseTeamsRepository teamsRepository;
    private final Scheduler mainScheduler;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    TeamsFragmentPresenter(TeamsFragmentView teamsFragmentView, DatabaseTeamsRepository databaseTeamsRepository, Scheduler mainScheduler) {
        this.view = teamsFragmentView;
        this.teamsRepository = databaseTeamsRepository;
        this.mainScheduler = mainScheduler;
    }

    void loadTeams() {
        compositeDisposable.add(teamsRepository.getTeams()
                .observeOn(mainScheduler)
                .subscribeWith(new DisposableSingleObserver<List<Team>>() {
                    @Override
                    public void onSuccess(@NonNull List<Team> teams) {
                        view.displayTeams(teams);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                }));
    }



    public void unsubscribe(){
        compositeDisposable.clear();
    }

}

