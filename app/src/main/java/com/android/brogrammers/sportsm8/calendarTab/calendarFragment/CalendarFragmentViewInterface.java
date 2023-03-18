package com.android.brogrammers.sportsm8.calendarTab.calendarFragment;

import android.content.Context;

import com.android.brogrammers.sportsm8.calendarTab.calendarFragment.dayFragment.DayFragmentView;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by Korbi on 13.09.2017.
 */

public interface CalendarFragmentViewInterface {
    void displayMeetings(List<DayFragmentView> meetings, List<CalendarDay> highlights, DateTime currentStartDate);
    void displayNoMeetings();
    Context getContext();

}
