package com.android.brogrammers.sportsm8.DataBaseConnection.Repositories.impl;

import com.android.brogrammers.sportsm8.UserClasses.LoginScreen;
import com.android.brogrammers.sportsm8.DataBaseConnection.ApiServices.APIService;
import com.android.brogrammers.sportsm8.DataBaseConnection.APIUtils;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Match;
import com.android.brogrammers.sportsm8.DataBaseConnection.Repositories.MatchRepository;

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
