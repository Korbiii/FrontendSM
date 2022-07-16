package com.android.brogrammers.sportsm8.SocialTab.Groups.GroupDetail;

import com.android.brogrammers.sportsm8.DataBaseConnection.Repositories.impl.DatabaseGroupRepository;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.UserInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Korbi on 29.06.2017.
 */

class GroupDetailPresenter {
    Scheduler mainScheduler;
    DatabaseGroupRepository repository;
    GroupDetailView view;
    private CompositeDisposable disposable = new CompositeDisposable();

    GroupDetailPresenter(Scheduler mainScheduler, DatabaseGroupRepository repository, GroupDetailView view) {
        this.mainScheduler = mainScheduler;
        this.repository = repository;
        this.view = view;
    }


    void loadMembers(int groupID) {
        disposable.add(repository.getGroupMembers(groupID)
                .subscribeOn(Schedulers.io())
                .observeOn(mainScheduler)
                .subscribeWith(new DisposableSingleObserver<List<UserInfo>>() {
                    @Override
                    public void onSuccess(List<UserInfo> value) {
                        view.displayMembers(value);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }));
    }

    void leaveGroup(int groupID) {
        repository.leaveGroup(groupID)
                .observeOn(mainScheduler)
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        view.leave();
                    }
                });
    }

    void addMembersToGroup(final int groupID, List<UserInfo> Selection) {
        Map<String, String> membersMap = new HashMap<>();
        for (int i = 0; i < Selection.size(); i++) {
            membersMap.put("member" + i, Selection.get(i).email);
        }
        repository.addMembersToGroup(groupID, membersMap)
                .observeOn(mainScheduler)
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        loadMembers(groupID);
                    }
                });
    }
}
