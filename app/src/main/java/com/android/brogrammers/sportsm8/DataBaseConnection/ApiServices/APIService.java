package com.android.brogrammers.sportsm8.DataBaseConnection.ApiServices;

import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Match;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Sport;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Korbi on 03.06.2017.
 */

public interface APIService {
    ///////////Sports
    @GET("Sports/sportList")
    Single<List<Sport>> getSports();


    //////////////Matches
    @GET("Matches/Matches")
    Single<List<Match>> getFriendsMatches(@Query("email") String email);

    //ACCOUNTS
    @FormUrlEncoded
    @POST("Accounts/createNewAccount")
    Completable createNewaccount(@Field("email") String email,@Field("username") String username);



}
