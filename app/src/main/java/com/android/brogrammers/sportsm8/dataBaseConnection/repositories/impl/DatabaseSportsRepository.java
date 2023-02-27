package com.android.brogrammers.sportsm8.dataBaseConnection.repositories.impl;

import com.android.brogrammers.sportsm8.dataBaseConnection.APIUtils;
import com.android.brogrammers.sportsm8.dataBaseConnection.apiServices.SportsApiService;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Sport;
import com.android.brogrammers.sportsm8.dataBaseConnection.repositories.SportsRepository;
import com.android.brogrammers.sportsm8.userClasses.LoginScreen;

import java.util.List;

import io.reactivex.Single;

public class DatabaseSportsRepository implements SportsRepository {
    private final SportsApiService apiService;
    private String email;

    public DatabaseSportsRepository(){
        apiService = APIUtils.getSportsApiService();
        email = LoginScreen.getRealEmail();
    }


    @Override
    public Single<List<Sport>> getSports() {
        return apiService.getSports();
    }
}
