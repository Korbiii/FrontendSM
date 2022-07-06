package com.android.brogrammers.sportsm8.dataBaseConnection.repositories.impl;

import com.android.brogrammers.sportsm8.dataBaseConnection.apiServices.TeamsApiService;
import com.android.brogrammers.sportsm8.userClasses.LoginScreen;
import com.android.brogrammers.sportsm8.dataBaseConnection.APIUtils;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Team;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.UserInfo;
import com.android.brogrammers.sportsm8.dataBaseConnection.repositories.TeamsRepository;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by Korbi on 16.06.2017.
 */

public class DatabaseTeamsRepository implements TeamsRepository {

    private TeamsApiService apiService;

    public DatabaseTeamsRepository() {
        apiService = APIUtils.getTeamsAPIService();
    }

    @Override
    public Single<List<Team>> getTeams() {
        return apiService.getTeams(LoginScreen.getRealEmail());
    }

    @Override
    public Single<List<UserInfo>> getTeamMembers(final Team team) {
        return apiService.getTeamMembers(team.teamID);
    }


}


