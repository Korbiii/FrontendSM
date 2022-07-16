package com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses;

import java.io.Serializable;

/**
 * Created by Korbi on 16.06.2017.
 */

public class Team implements Serializable {
    public int teamID,wins,losses,sportID;
    public String teamName;
    public double longitude_home,latitude_home;
}
