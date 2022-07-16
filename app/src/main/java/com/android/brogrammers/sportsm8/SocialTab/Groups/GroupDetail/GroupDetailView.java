package com.android.brogrammers.sportsm8.SocialTab.Groups.GroupDetail;

import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.UserInfo;

import java.util.List;

/**
 * Created by Korbi on 29.06.2017.
 */

public interface GroupDetailView {
    void displayMembers(List<UserInfo> memberList);
    void leave();

}
