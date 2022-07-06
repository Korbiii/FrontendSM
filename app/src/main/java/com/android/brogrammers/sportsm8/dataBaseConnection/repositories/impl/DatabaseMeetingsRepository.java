package com.android.brogrammers.sportsm8.dataBaseConnection.repositories.impl;

import com.android.brogrammers.sportsm8.dataBaseConnection.APIUtils;
import com.android.brogrammers.sportsm8.dataBaseConnection.apiServices.MeetingApiService;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Meeting;
import com.android.brogrammers.sportsm8.dataBaseConnection.repositories.MeetingsRepository;
import com.android.brogrammers.sportsm8.userClasses.LoginScreen;
import com.google.gson.JsonObject;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by Korbi on 14.06.2017.
 */

public class DatabaseMeetingsRepository implements MeetingsRepository {

    private MeetingApiService apiService;
    private DateTimeFormatter formatter;
    private String email;

    public DatabaseMeetingsRepository() {
        apiService = APIUtils.getMeetingAPIService();
        formatter = DateTimeFormat.forPattern("MM-dd-YYYY HH:mm:ss");
         email = LoginScreen.getRealEmail();

    }

    @Override
    public Single<JsonObject> isMeetingReady(final Meeting meeting) {
        return apiService.isMeetingConfirmed(meeting.MeetingID);
    }

    @Override
    public Single<List<Meeting>> getMeetings(String email) {
        return apiService.getMeetings(email);
    }

    @Override
    public Completable confirmMeeting(Meeting meeting) {
        return apiService.confirmMeeting(meeting.MeetingID, email);
    }

    @Override
    public Completable setOtherTime(Meeting meeting, DateTime start, DateTime end) {
        return apiService.setOtherTime(formatter.print(start),formatter.print(end),meeting.MeetingID,email);
    }

    @Override
    public Completable declineMeeting(Meeting meeting) {
        return apiService.declineMeeting(meeting.MeetingID,email);
    }


}
