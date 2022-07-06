package com.android.brogrammers.sportsm8.dataBaseConnection.repositories;

import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Team;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.UserInfo;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by Korbi on 16.06.2017.
 */

public interface TeamsRepository {
    Single<List<Team>> getTeams();
    Single<List<UserInfo>> getTeamMembers(final Team team);
}
