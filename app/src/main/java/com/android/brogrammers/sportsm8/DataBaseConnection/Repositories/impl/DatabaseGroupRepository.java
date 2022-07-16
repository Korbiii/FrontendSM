package com.android.brogrammers.sportsm8.DataBaseConnection.Repositories.impl;

import com.android.brogrammers.sportsm8.DataBaseConnection.ApiServices.GroupsApiService;
import com.android.brogrammers.sportsm8.DataBaseConnection.Repositories.GroupRepository;
import com.android.brogrammers.sportsm8.DataBaseConnection.APIUtils;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.UserInfo;
import com.android.brogrammers.sportsm8.UserClasses.LoginScreen;

import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by Korbi on 29.06.2017.
 */

public class DatabaseGroupRepository implements GroupRepository {

    private GroupsApiService apiService;

    public DatabaseGroupRepository() {
        this.apiService = APIUtils.getGroupsAPIService();
    }

    @Override
    public Single<List<UserInfo>> getGroupMembers(final int groupID) {
        return apiService.getGroupMembers(groupID);
    }

    @Override
    public Completable leaveGroup(final int groupID) {
        return apiService.leaveGroup(groupID, LoginScreen.getRealEmail());
    }

    @Override
    public Completable addMembersToGroup(final int groupID, final Map<String, String> newMembers) {
        return apiService.addMembersToGroup(groupID,newMembers);
//        return Single.fromCallable(new Callable<ResponseBody>() {
//            @Override
//            public ResponseBody call() throws Exception {
//                return apiService.addMembersToGroup("adMembersToGroup",groupID,newMembers).execute().body();
//            }
//        });
    }
}
