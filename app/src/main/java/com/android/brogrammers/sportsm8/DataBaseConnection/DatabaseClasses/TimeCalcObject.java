package com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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
