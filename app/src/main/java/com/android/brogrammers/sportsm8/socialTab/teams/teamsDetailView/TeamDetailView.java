package com.android.brogrammers.sportsm8.socialTab.teams.teamsDetailView;

import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.UserInfo;

import java.util.List;

/**
 * Created by Korbi on 20.06.2017.
 */

public interface TeamDetailView {
    void displayMembers(List<UserInfo> members);

}
