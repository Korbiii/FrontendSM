package com.android.brogrammers.sportsm8.SocialTab.Teams.TeamsDetailView;

import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.UserInfo;

import java.util.List;

/**
 * Created by Korbi on 20.06.2017.
 */

public interface TeamDetailView {
    void displayMembers(List<UserInfo> members);

}
