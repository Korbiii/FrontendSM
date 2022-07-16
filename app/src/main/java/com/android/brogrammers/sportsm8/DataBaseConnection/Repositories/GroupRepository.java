package com.android.brogrammers.sportsm8.DataBaseConnection.Repositories;

import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.UserInfo;

import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by Korbi on 29.06.2017.
 */

public interface GroupRepository {
    Single<List<UserInfo>> getGroupMembers(int groupID);
    Completable leaveGroup(int groupID);
    Completable addMembersToGroup(int groupID, Map<String,String> newMembers);
}
