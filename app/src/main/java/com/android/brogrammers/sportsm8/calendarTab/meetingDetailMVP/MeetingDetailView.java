package com.android.brogrammers.sportsm8.calendarTab.meetingDetailMVP;

import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.UserInfo;

import java.util.List;

/**
 * Created by Korbi on 07.06.2017.
 */

public interface MeetingDetailView {
    void displayMembers(List<UserInfo> members);
    void displayNoMembers();
    void setUpProgressBar(int accepted, int total);
    void updateMemberList();
    void showError();
}
