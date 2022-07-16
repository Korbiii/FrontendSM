package com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;

/**
 * Created by Korbi on 22.06.2017.
 */

public class Match implements Serializable {
    public int matchID,teamID_1,teamID_2,activity_ID,score_team_1,score_team_2;
    public String time,team1,team2;


    public Match(int matchID, int teamID_1, int teamID_2, int activity_ID, int score_team_1, int score_team_2, String time, String team1, String team2) {
        this.matchID = matchID;
        this.teamID_1 = teamID_1;
        this.teamID_2 = teamID_2;
        this.activity_ID = activity_ID;
        this.score_team_1 = score_team_1;
        this.score_team_2 = score_team_2;
        this.time = time;
        this.team1 = team1;
        this.team2 = team2;
    }

    public DateTime getTime() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss");
        return formatter.parseDateTime(time);
    }
}
