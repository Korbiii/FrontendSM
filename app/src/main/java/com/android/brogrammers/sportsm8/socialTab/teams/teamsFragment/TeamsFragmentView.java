package com.android.brogrammers.sportsm8.socialTab.teams.teamsFragment;

import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Team;

import java.util.List;

/**
 * Created by Korbi on 16.06.2017.
 */

public interface TeamsFragmentView {

    void displayTeams(List<Team> teams);
}
