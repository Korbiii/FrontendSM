package com.android.brogrammers.sportsm8.dataBaseConnection.repositories.impl;

import com.android.brogrammers.sportsm8.dataBaseConnection.APIUtils;
import com.android.brogrammers.sportsm8.dataBaseConnection.apiServices.MeetingApiService;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Meeting;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.UserInfo;
import com.android.brogrammers.sportsm8.dataBaseConnection.repositories.MeetingsRepository;
import com.android.brogrammers.sportsm8.userClasses.LoginScreen;
import com.google.gson.JsonObject;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Map;

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
        formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
         email = LoginScreen.getRealEmail();

    }

    @Override
    public Single<JsonObject> isMeetingReady(final Meeting meeting) {
        return apiService.isMeetingConfirmed(meeting.meetingID);
    }

    @Override
    public Single<List<Meeting>> getMeetings(String email) {
        return apiService.getMeetings(email);
    }

    @Override
    public Completable confirmMeeting(Meeting meeting) {
        return apiService.confirmMeeting(meeting.meetingID, email,meeting.getStartDateTime().toString("yyyy-MM-dd HH:mm"), meeting.getEndDateTime().toString("yyyy-MM-dd HH:mm"));
    }

    @Override
    public Completable setOtherTime(Meeting meeting, DateTime start, DateTime end) {
        return apiService.setOtherTime(formatter.print(start),formatter.print(end),meeting.meetingID,email);
    }

    @Override
    public Completable declineMeeting(Meeting meeting) {
        return apiService.declineMeeting(meeting.meetingID,email);
    }

    @Override
    public Completable createMeeting(Meeting meeting, Map<String,String> Selection) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-YYYY HH:mm:ss");;
        return apiService.createMeeting(formatter.print(meeting.startTime),formatter.print(meeting.endTime),meeting.minParticipants,email,meeting.meetingActivity, meeting.sportID, meeting.dynamic,Selection,meeting.longitude, meeting.latitude);
    }


}
