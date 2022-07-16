package com.android.brogrammers.sportsm8.DataBaseConnection.ApiServices;

import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Group;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.UserInfo;

import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Korbi on 08.09.2017.
 */

public interface GroupsApiService {
    @GET("Groups/Groups")
    Single<List<Group>> getGroups(@Query("email") String email);

    @GET("Groups/GroupMembers")
    Single<List<UserInfo>> getGroupMembers(@Query("GroupID") int GroupID);

    @FormUrlEncoded
    @POST("Groups/addMembersToGroup")
    Completable addMembersToGroup(@Field("GroupID") int groupID, @FieldMap Map<String, String> members);

    @FormUrlEncoded
    @POST("Groups/newGroup")
    Completable createGroup(@Field("groupName") String groupName, @FieldMap Map<String, String> members);

    @FormUrlEncoded
    @POST("Groups/leaveGroup")
    Completable leaveGroup(@Field("GroupID") int groupID, @Field("email") String email);
}
