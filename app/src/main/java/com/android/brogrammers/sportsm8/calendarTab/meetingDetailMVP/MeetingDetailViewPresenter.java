package com.android.brogrammers.sportsm8.calendarTab.meetingDetailMVP;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import android.util.Log;

import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Meeting;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.UserInfo;
import com.android.brogrammers.sportsm8.dataBaseConnection.repositories.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.observers.DisposableSingleObserver;

public class MeetingDetailViewPresenter extends BaseObservable {

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
        compositeDisposable.add(userRepository.getUsers(meeting.meetingID)
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
                        Log.d(TAG, "Members could not be loaded");
                    }
                }));
    }

    public void addMembers(final Meeting meeting, List<UserInfo> selection) {
        Map<String, String> membersMap = new HashMap<>();
        for (int i = 0; i < selection.size(); i++) {
            membersMap.put("member" + i, selection.get(i).email);
        }
        compositeDisposable.add(userRepository.addUsersToMeeting(meeting.meetingID,membersMap)
                .observeOn(mainScheduler)
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        view.updateMemberList();

                    }
                }));
    }


    public void unsubscribe() {
        compositeDisposable.clear();
    }


}
