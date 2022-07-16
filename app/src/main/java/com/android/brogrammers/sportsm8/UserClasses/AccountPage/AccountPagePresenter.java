package com.android.brogrammers.sportsm8.UserClasses.AccountPage;

import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.UserInfo;
import com.android.brogrammers.sportsm8.DataBaseConnection.Repositories.impl.DatabaseAccountRepository;
import com.android.brogrammers.sportsm8.DataBaseConnection.Repositories.impl.DatabaseUserRepository;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * Created by Korbi on 13.09.2017.
 */

public class AccountPagePresenter{

    private Scheduler mainScheduler;
    private DatabaseAccountRepository repository;
    private AccountPageView view;

    public AccountPagePresenter(Scheduler mainScheduler, DatabaseAccountRepository repository, AccountPageView view) {
        this.mainScheduler = mainScheduler;
        this.repository = repository;
        this.view = view;
    }

    public void loadUserData(){
        repository.loadAccountInfo()
                .observeOn(mainScheduler)
                .subscribeWith(new DisposableSingleObserver<UserInfo>() {
                    @Override
                    public void onSuccess(@NonNull UserInfo userInfo) {
                        view.displayUserInfo(userInfo);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });

    }
}
