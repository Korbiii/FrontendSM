package com.android.brogrammers.sportsm8.CalendarViews.MeetingDetailMVP;

import com.android.brogrammers.sportsm8.databaseConnection.RetroFitDatabase.DatabaseClasses.UserInfo;

import java.util.List;

/**
 * Created by Korbi on 07.06.2017.
 */

public interface MeetingDetailView {
    void displayMembers(List<UserInfo> members);
    void displayNoMembers();
    void setUpprogressBar(int accepted,int total);
    void updateMemberList();

    void showError();
}
