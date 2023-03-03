package com.android.brogrammers.sportsm8.socialTab.friends;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.brogrammers.sportsm8.dataBaseConnection.RetroFitClient;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.UserInfo;
import com.android.brogrammers.sportsm8.dataBaseConnection.repositories.FriendshipRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;

public class OnlyFriendsPresenter {

    private final OnlyFriendsViewInterface view;
    private final FriendshipRepository friendshipRepository;
    private Scheduler mainScheduler;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public List<UserInfo> friends = new ArrayList<>();

    public OnlyFriendsPresenter(OnlyFriendsViewInterface view,FriendshipRepository friendshipRepository,Scheduler mainScheduler){
        this.view = view;
        this.friendshipRepository = friendshipRepository;
        this.mainScheduler = mainScheduler;
    }

    public void getFriends(){
        compositeDisposable.add(friendshipRepository.getFriends()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<UserInfo>>() {
                    @Override
                    public void onSuccess(List<UserInfo> friendList) {
                        friends=friendList;
                        view.createAdapter();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }));
    }

    public void getSearchResults(String search){
        compositeDisposable.add(friendshipRepository.searchFriends(search)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<UserInfo>>() {
                    @Override
                    public void onSuccess(@NonNull List<UserInfo> searchResults) {

                        //updateUI("");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                }));
    }

}
