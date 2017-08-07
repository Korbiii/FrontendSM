package com.android.brogrammers.sportsm8.DataBaseConnection.Repositories;

import com.android.brogrammers.sportsm8.DataBaseConnection.RetroFitDatabase.DatabaseClasses.UserInfo;

import java.security.acl.Group;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.ResponseBody;

/**
 * Created by Korbi on 29.06.2017.
 */

public interface GroupRepository {
    Single<List<UserInfo>> getGroupMembers(int groupID);
    Completable leaveGroup(int groupID);
    Completable addMembersToGroup(int groupID, Map<String,String> newMembers);
}
