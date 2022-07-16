package com.android.brogrammers.sportsm8.CalendarTab.MeetingDetailMVP;

import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.UserInfo;

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
