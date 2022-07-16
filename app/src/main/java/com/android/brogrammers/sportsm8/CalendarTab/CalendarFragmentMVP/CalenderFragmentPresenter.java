package com.android.brogrammers.sportsm8.CalendarTab.CalendarFragmentMVP;

import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.android.brogrammers.sportsm8.CalendarTab.MeetingDetailMVP.MeetingDetailActivity;
import com.android.brogrammers.sportsm8.CalendarTab.MeetingDetailMVP.MeetingDetailView;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Meeting;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.UserInfo;
import com.android.brogrammers.sportsm8.DataBaseConnection.OfflineDatabase.DatabaseHelper;
import com.android.brogrammers.sportsm8.DataBaseConnection.Repositories.MeetingsRepository;
import com.android.brogrammers.sportsm8.DataBaseConnection.Repositories.UserRepository;
import com.android.brogrammers.sportsm8.DataBaseConnection.RetroFitClient;
import com.android.brogrammers.sportsm8.UserClasses.LoginScreen;
import com.google.gson.reflect.TypeToken;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * Created by Korbi on 13.09.2017.
 */

public class CalenderFragmentPresenter {

    private CalendarFragmentView view;
    private MeetingsRepository meetingsRepository;
    private Scheduler mainScheduler;
    private List<Meeting> meetings;
    private List<DayFragment> dayFragments = new ArrayList<>();
    private boolean locationMode = false;
    private double longitude;
    private double latitude;
    private DateTime today = new DateTime();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final String TAG = MeetingDetailActivity.class.getSimpleName();

    public CalenderFragmentPresenter(CalendarFragmentView view, MeetingsRepository meetingsRepository, Scheduler mainScheduler) {
        this.view = view;
        this.meetingsRepository = meetingsRepository;
        this.mainScheduler = mainScheduler;
    }

    public void loadMeetings(final DateTime today) {
        this.today = today;
        compositeDisposable.add(meetingsRepository.getMeetings(LoginScreen.getRealEmail())
                .observeOn(mainScheduler)
                .subscribeWith(new DisposableSingleObserver<List<Meeting>>() {
                    @Override
                    public void onSuccess(@NonNull List<Meeting> meetingList) {
                        if (meetingList != null) {
                            meetings = meetingList;
                            view.displayMeetings(getDayFragments(meetings, 14), getHighlightList(meetings), today);
                            addData(meetingList.get(0));
                            RetroFitClient.storeObjectList(new ArrayList<Object>(meetingList), "meetings", view.getContext());
                        } else {
                            view.displayNoMeetings();
                        }
                    }

                    @Override
                    @SuppressWarnings("unchecked")
                    public void onError(@NonNull Throwable e) {
                        meetings = (ArrayList<Meeting>) RetroFitClient.retrieveObjectList("meetings", view.getContext(), new TypeToken<ArrayList<Meeting>>() {
                        }.getType());
                        if ( meetings != null) {
                            view.displayMeetings(getDayFragments(meetings, 14), getHighlightList(meetings), today);
                        }else{
                            view.displayNoMeetings();
                        }
                    }
                }));
    }



    public void addData(Meeting meeting){
        DatabaseHelper databaseHelper = new DatabaseHelper(view.getContext());
        databaseHelper.addData(meeting);
    }


    public void refreshMeetings() {
        loadMeetings(today);
    }

    private List<DayFragment> getDayFragments(List<Meeting> meetings, int count) {
        dayFragments.clear();
        for (int j = 0; j < count; j++) {
            List<Meeting> meetingsOnDay = new ArrayList<>();
            Log.i(TAG, "This is today" + today);
            for (int i = 0; i < meetings.size(); i++) {
                DateTime dt1 = meetings.get(i).getStartDateTime();
                DateTime dt2 = today;
                if (dt1.withTimeAtStartOfDay().equals(dt2.plusDays(j).withTimeAtStartOfDay())) {
                    if (locationMode) {
                        Location start = new Location("locationA");
                        start.setLatitude(latitude);
                        start.setLongitude(longitude);
                        Location end = new Location("locationB");
                        end.setLatitude(meetings.get(i).latitude);
                        end.setLongitude(meetings.get(i).longitude);
                        double distance = start.distanceTo(end);
                        if (distance < 5000) {
                            meetingsOnDay.add(meetings.get(i));
                        }
                    } else {
                        meetingsOnDay.add(meetings.get(i));
                    }
                }
            }
            DayFragment temp = DayFragment.newInstance(meetingsOnDay);
            dayFragments.add(temp);
        }
        return dayFragments;
    }

    public List<DayFragment> getNextDays(int numberOfNewDays, int numberOfCurrentDays) {
        List<DayFragment> newDays = new ArrayList<>();
        for (int i = 0; i < numberOfNewDays; i++) {
            List<Meeting> meetingsOnDay = new ArrayList<>();
            for (int j = 0; j < meetings.size(); j++) {
                String date = meetings.get(j).getDay(); //problem if no meetingsOnDay yet!?
                int dateOfMeeting = DateTimeFormat.forPattern("yyyy-MM-dd").parseLocalDate(date).getDayOfYear();
                if (dateOfMeeting == today.getDayOfYear() + (numberOfCurrentDays + i)) {
                    meetingsOnDay.add(meetings.get(j));
                }
            }
            newDays.add(DayFragment.newInstance(meetingsOnDay));
        }
        return newDays;
    }

    private List<CalendarDay> getHighlightList(List<Meeting> meetings) {
        List<CalendarDay> highlights = new ArrayList<>();
        for (int i = 0; i < meetings.size(); i++) {
            DateTime dateTime = meetings.get(i).getStartDateTime();
            CalendarDay date = CalendarDay.from(dateTime.toDate());
            highlights.add(date);
        }
        return highlights;
    }

    public void setLocation(double longitude, double latitude, boolean locationMode, int tabCount) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.locationMode = locationMode;
        view.displayMeetings(getDayFragments(meetings, tabCount), getHighlightList(meetings), today);
    }

}
