package com.android.brogrammers.sportsm8.DataBaseConnection.ApiServices;

import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.UserInfo;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Korbi on 08.09.2017.
 */

public interface FriendshipsApiService {
    @GET("Friendships/Friends")
    Single<List<UserInfo>> getFriends(@Query("email") String email);

    @GET("Friendships/searchFriends")
    Single<List<UserInfo>> searchFriends( @Query("email") String email, @Query("friendname") String searchText);

    @FormUrlEncoded
    @POST("Friendships/confirmFriend")
    Completable confirmFriend(@Field("email") String email, @Field("friendemail") String friendemail);

    @FormUrlEncoded
    @POST("Friendships/setFriend")
    Completable setFriend(@Field("email") String email,@Field("friendemail") String friendemail);

    @FormUrlEncoded
    @POST("Friendships/removeFriend")
    Completable removeFriend(@Field("email") String email,@Field("friendemail") String friendemail);

    @FormUrlEncoded
    @POST("Friendships/confirmPending")
    Completable confirmPending(@Field("email") String email);
}
