package com.android.brogrammers.sportsm8.socialTab.groups.groupDetail;

import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.UserInfo;

import java.util.List;

/**
 * Created by Korbi on 29.06.2017.
 */

public interface GroupDetailView {
    void displayMembers(List<UserInfo> memberList);
    void leave();

}
