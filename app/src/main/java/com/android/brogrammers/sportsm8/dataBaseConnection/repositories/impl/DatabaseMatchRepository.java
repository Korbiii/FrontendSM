package com.android.brogrammers.sportsm8.dataBaseConnection.repositories.impl;

import com.android.brogrammers.sportsm8.userClasses.LoginScreen;
import com.android.brogrammers.sportsm8.dataBaseConnection.apiServices.APIService;
import com.android.brogrammers.sportsm8.dataBaseConnection.APIUtils;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Match;
import com.android.brogrammers.sportsm8.dataBaseConnection.repositories.MatchRepository;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by Korbi on 22.06.2017.
 */

public class DatabaseMatchRepository implements MatchRepository {

    private APIService apiService;

    public DatabaseMatchRepository() {
        apiService = APIUtils.getAPIService();
    }

    @Override
    public Single<List<Match>> getMatches() {
        return apiService.getFriendsMatches(LoginScreen.getRealEmail());
    }
}
