package com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses;

import java.io.Serializable;

/**
 * Created by Korbi on 05.06.2017.
 */

public class Group implements Serializable {

    public String GroupName;
    public int GroupID;
    public Boolean selected = false;
}
