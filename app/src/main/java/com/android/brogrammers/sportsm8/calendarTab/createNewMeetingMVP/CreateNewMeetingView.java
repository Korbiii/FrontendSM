package com.android.brogrammers.sportsm8.calendarTab.createNewMeetingMVP;

import android.view.View;

import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Meeting;

import org.joda.time.DateTime;

public interface CreateNewMeetingView {
    void displayIfEnoughMembersSelected(boolean enough,int size);
    void createNumberPickerDialog(String message, int current, int max,String target);
    void setMinTimeHours(int minTimeHours);
    void setMinMemberCount(int minMemberCount);
    void updateDateTimeTextviews(Meeting meeting);
    void closeActivity();
    void showErrorToast(String message);
}
