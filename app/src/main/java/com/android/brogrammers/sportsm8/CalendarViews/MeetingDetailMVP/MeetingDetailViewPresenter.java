package com.android.brogrammers.sportsm8.CalendarViews.MeetingDetailMVP;

import android.util.Log;

import com.android.brogrammers.sportsm8.databaseConnection.RetroFitDatabase.DatabaseClasses.Meeting;
import com.android.brogrammers.sportsm8.databaseConnection.RetroFitDatabase.DatabaseClasses.UserInfo;
import com.android.brogrammers.sportsm8.repositories.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class MeetingDetailViewPresenter {

    private MeetingDetailView view;
    private UserRepository userRepository;
    private Scheduler mainScheduler;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final String TAG = MeetingDetailActivity.class.getSimpleName();

    public MeetingDetailViewPresenter(MeetingDetailView view, UserRepository userRepository, Scheduler mainScheduler) {
        this.view = view;
        this.userRepository = userRepository;
        this.mainScheduler = mainScheduler;
    }

    public void loadMembers(final Meeting meeting) {
        compositeDisposable.add(userRepository.getUsers(meeting.MeetingID)
                .subscribeOn(Schedulers.io())
                .observeOn(mainScheduler)
                .subscribeWith(new DisposableSingleObserver<List<UserInfo>>() {
                    @Override
                    public void onSuccess(@NonNull List<UserInfo> userInfos) {
                        if (userInfos.isEmpty()) {
                            view.displayNoMembers();
                        } else {
                            int count = 0;
                            for (int i = 0; i < userInfos.size(); i++) {
                                if (userInfos.get(i).confirmed == 1) {
                                    count++;
                                }
                            }
                            view.displayMembers(userInfos);
                            view.setUpprogressBar(count, meeting.minParticipants);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        view.showError();
                        Log.d(TAG, "Members could not be laoded");
                    }
                }));
    }

    public void addMembers(final Meeting meeting, List<UserInfo> selection) {
        Map<String, String> membersMap = new HashMap<>();
        for (int i = 0; i < selection.size(); i++) {
            membersMap.put("member" + i, selection.get(i).email);
        }
        compositeDisposable.add(userRepository.addUsersToMeeting(meeting.MeetingID, membersMap)
                .subscribeOn(Schedulers.io())
                .observeOn(mainScheduler)
                .subscribeWith(new DisposableSingleObserver<ResponseBody>() {
                    @Override
                    public void onSuccess(@NonNull ResponseBody responseBody) {
                        view.updateMemberList();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        view.showError();
                        Log.d(TAG, "Members could not be added");
                    }
                }));
    }


    public void unsubscribe() {
        compositeDisposable.clear();
    }


}
