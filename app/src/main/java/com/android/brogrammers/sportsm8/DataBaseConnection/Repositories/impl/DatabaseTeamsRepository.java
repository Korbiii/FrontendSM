package com.android.brogrammers.sportsm8.DataBaseConnection.Repositories.impl;

import com.android.brogrammers.sportsm8.DataBaseConnection.ApiServices.TeamsApiService;
import com.android.brogrammers.sportsm8.UserClasses.LoginScreen;
import com.android.brogrammers.sportsm8.DataBaseConnection.APIUtils;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Team;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.UserInfo;
import com.android.brogrammers.sportsm8.DataBaseConnection.Repositories.TeamsRepository;

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


