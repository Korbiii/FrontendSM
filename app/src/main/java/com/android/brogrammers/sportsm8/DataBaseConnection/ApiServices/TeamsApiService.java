package com.android.brogrammers.sportsm8.DataBaseConnection.ApiServices;

import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Team;
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

public interface TeamsApiService {
    @GET("Teams/Teams")
    Single<List<Team>> getTeams(@Query("email") String email);

    @GET("Teams/Members")
    Single<List<UserInfo>> getTeamMembers(@Query("teamID") int teamID);

    @FormUrlEncoded
    @POST("Teams/newTeam")
    Completable createTeam(@Field("teamName") String groupName, @Field("longitude") double longitude, @Field("latitude") double latitude, @Field("sportID") int sportID, @FieldMap Map<String, String> members);

}
