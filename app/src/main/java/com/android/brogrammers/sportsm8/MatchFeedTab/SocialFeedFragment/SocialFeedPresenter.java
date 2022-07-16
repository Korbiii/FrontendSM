package com.android.brogrammers.sportsm8.MatchFeedTab.SocialFeedFragment;

import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Match;
import com.android.brogrammers.sportsm8.DataBaseConnection.Repositories.impl.DatabaseMatchRepository;

import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Korbi on 22.06.2017.
 */

public class SocialFeedPresenter {

    private DatabaseMatchRepository repository;
    private SocialFeedView view;
    private final Scheduler mainScheduler;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public SocialFeedPresenter(DatabaseMatchRepository repository, SocialFeedView view, Scheduler mainScheduler) {
        this.repository = repository;
        this.view = view;
        this.mainScheduler = mainScheduler;
    }

    public void loadMatches() {
        compositeDisposable.add(repository.getMatches()
                .subscribeOn(Schedulers.io())
                .observeOn(mainScheduler)
                .subscribeWith(new DisposableSingleObserver<List<Match>>() {
            @Override
            public void onSuccess(List<Match> matches) {
                view.displayMatches(matches);
            }

            @Override
            public void onError(Throwable e) {

            }
        }));

    }
}
