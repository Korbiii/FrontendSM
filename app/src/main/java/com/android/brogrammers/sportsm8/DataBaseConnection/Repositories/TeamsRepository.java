package com.android.brogrammers.sportsm8.DataBaseConnection.Repositories;

import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Team;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.UserInfo;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by Korbi on 16.06.2017.
 */

public interface TeamsRepository {
    Single<List<Team>> getTeams();
    Single<List<UserInfo>> getTeamMembers(final Team team);
}
