package com.android.brogrammers.sportsm8.CalendarTab.CalendarFragmentMVP;

import android.content.Context;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by Korbi on 13.09.2017.
 */

public interface CalendarFragmentView {
    void displayMeetings(List<DayFragment> meetings, List<CalendarDay> highlights,DateTime currentStartDate);
    void displayNoMeetings();
    Context getContext();

}
