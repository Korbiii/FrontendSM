package com.android.brogrammers.sportsm8.calendarTab.createNewMeetingMVP;


import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;

import com.android.brogrammers.sportsm8.R;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Group;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Meeting;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Sport;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.UserInfo;
import com.android.brogrammers.sportsm8.dataBaseConnection.repositories.GroupRepository;
import com.android.brogrammers.sportsm8.dataBaseConnection.repositories.MeetingsRepository;
import com.android.brogrammers.sportsm8.dataBaseConnection.repositories.SportsRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;

public class CreateNewMeetingViewPresenter {
    private final CreateNewMeetingView view;
    private final MeetingsRepository meetingsRepository;
    private final GroupRepository groupRepository;
    private final SportsRepository sportsRepository;
    private final Scheduler mainScheduler;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final String TAG = CreateNewMeetingActivity.class.getSimpleName();

    List<UserInfo> Selection = new ArrayList<>();
    List<Group> SelectionGroup = new ArrayList<>();
    List<Sport> sportsList;
    List<String> sportNames;

    public Meeting meeting = new Meeting(4,1);


    public CreateNewMeetingViewPresenter(CreateNewMeetingView view,
                                         MeetingsRepository meetingsRepository,
                                         GroupRepository groupRepository,
                                         SportsRepository sportsRepository,
                                         Scheduler mainScheduler){
        this.view = view;
        this.meetingsRepository = meetingsRepository;
        this.groupRepository = groupRepository;
        this.sportsRepository = sportsRepository;
        this.mainScheduler = mainScheduler;

        setupSportsList();
    }

    private void setupSportsList() {
        compositeDisposable.add(sportsRepository.getSports()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Sport>>() {
                    @Override
                    public void onSuccess(@NonNull List<Sport> sports) {
                        sportsList = sports;
                        sportNames = new ArrayList<>();
                        for(Sport sport : sportsList) {
                            sportNames.add(sport.sportname);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e){
                        Log.d("TAG",e.toString());
                        Log.d("TAG","Error");
                    }
                }));
    }

    private void mergeGroupsAndFriends() {
        for (int i = 0; i < SelectionGroup.size(); i++) {
            compositeDisposable.add(groupRepository.getGroupMembers(SelectionGroup.get(i).GroupID)
                    .observeOn(mainScheduler)
                    .subscribeWith(new DisposableSingleObserver<List<UserInfo>>() {
                        @Override
                        public void onSuccess(@NonNull List<UserInfo> response) {
                            for (int j = 0; j < response.size(); j++) {
                                boolean tempBool = false;
                                for (int h = 0; h < Selection.size(); h++) {
                                    if (response.get(j).email.equals(Selection.get(h).email)) {
                                        tempBool = true;
                                    }
                                }
                                if (!tempBool) {
                                    Selection.add(response.get(j));
                                }
                            }
                            checkIfEnoughParticipants();
                            dispose();
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            dispose();
                        }
                    }));
        }
    }

    public void checkIfEnoughParticipants() {
        view.displayIfEnoughParticipantsSelected(Selection.size() > meeting.minParticipants+1,Selection.size());
    }
    public void setMinTime(View v) {
        view.createNumberPickerDialog("Wie viele Stunden?", meeting.duration, 24,"minHours");
    }

    public void setMinPartysize(View v){
        view.createNumberPickerDialog("Ab wie vielen Leuten?", meeting.minParticipants, 30,"minParticipantsCount");
    }

    public void setStartTime(int hourOfDay,int minute){
        meeting.startTime = meeting.startTime.withTime(hourOfDay,minute,0,0);
        view.updateDateTimeTextviews(meeting);
    }

    public void setEndTime(int hourOfDay,int minute){
        meeting.endTime = meeting.endTime.withTime(hourOfDay,minute,0,0);
        view.updateDateTimeTextviews(meeting);
    }

    public void setDate(int year, int month, int dayOfMonth){
        meeting.startTime = meeting.startTime.withDate(year,month+1,dayOfMonth);
        meeting.endTime = meeting.endTime.withDate(year,month+1,dayOfMonth);
        view.updateDateTimeTextviews(meeting);
    }

    public void resultOfAddingFriends(ActivityResult result) {
        if (result.getData() != null) {
            Bundle bundle = result.getData().getExtras();
            Selection = (ArrayList<UserInfo>) bundle.getSerializable("partyList");
            SelectionGroup = (ArrayList<Group>) bundle.getSerializable("groupList");
        }
        mergeGroupsAndFriends();
        checkIfEnoughParticipants();
    }

    List<String> getSearchResults(String searchString){
        List<String> result = new ArrayList<>();
        for (String name : sportNames) {
            if(name.toLowerCase().contains(searchString)){
                result.add(name);
            }
        }
        return result;
    }


    public void createMeeting(View v) {
        if (Selection.size() < meeting.minParticipants) {
            view.showErrorToast("Not Enough Participants selected");
            return;
        }
        if (meeting.endTime.isBefore(meeting.startTime)) {
            view.showErrorToast("The meeting ends before it starts!");
            return;
        }
        Map<String, String> participants = new HashMap<>();
        for (int i = 0; i < Selection.size(); i++) {
            participants.put("members" + i, Selection.get(i).email);
        }

        meetingsRepository.createMeeting(meeting,participants)
                .observeOn(mainScheduler)
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        view.closeActivity();
                        dispose();
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.showErrorToast("Something went wrong while creating the Meeting");
                        dispose();
                    }
                });

    }

    public void chooseSport(String choosenSport) {
        for(Sport sport:sportsList){
            if(sport.sportname.equals(choosenSport)){
                meeting.minParticipants = sport.minPartySize;
                meeting.sportID = sport.sportID;
                meeting.meetingActivity = choosenSport;
            }
        }
        view.setMinimumNumberOfParticipants(meeting.minParticipants);
        checkIfEnoughParticipants();
    }
}
