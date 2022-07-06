package com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses;

import org.joda.time.DateTime;

/**
 * Created by Korbi on 25.06.2017.
 */

public class TimeCalcObject {
    public DateTime time;
    public int minusOrPlus;
    public int number = 1;

    public TimeCalcObject(DateTime time, int minusOrPlus) {
            this.time = time;
            this.minusOrPlus = minusOrPlus;
    }
}
