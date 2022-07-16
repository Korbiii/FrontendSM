package com.android.brogrammers.sportsm8.SocialTab.Teams.TeamsFragment;

import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Team;

import java.util.List;

/**
 * Created by Korbi on 16.06.2017.
 */

public interface TeamsFragmentView {

    void displayTeams(List<Team> teams);
}
