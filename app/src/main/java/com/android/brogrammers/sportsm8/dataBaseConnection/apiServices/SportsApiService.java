package com.android.brogrammers.sportsm8.dataBaseConnection.apiServices;

import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Sport;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface SportsApiService {

    @GET("Sports/sportlist")
    Single<List<Sport>> getSports();
}
