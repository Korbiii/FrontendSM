package com.android.brogrammers.sportsm8.dataBaseConnection.apiServices;

import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Match;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Sport;

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
    @GET("Sports/sportlist.php")
    Single<List<Sport>> getSports();


    //////////////Matches
    @GET("Matches/Matches")
    Single<List<Match>> getFriendsMatches(@Query("email") String email);

    //ACCOUNTS
    @FormUrlEncoded
    @POST("Accounts/createNewAccount")
    Completable createNewaccount(@Field("email") String email,@Field("username") String username);



}
