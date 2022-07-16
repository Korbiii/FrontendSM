package com.android.brogrammers.sportsm8.ZZOldClassers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.brogrammers.sportsm8.DataBaseConnection.APIUtils;
import com.android.brogrammers.sportsm8.DataBaseConnection.ApiServices.MeetingApiService;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Meeting;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class DatabaseHelperMeetings implements Callback {
    private static final String TAG = DatabaseHelperMeetings.class.getSimpleName();
    private MeetingApiService apiService = APIUtils.getMeetingAPIService();
    private String email;

    public DatabaseHelperMeetings(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences("loginInformation", Context.MODE_PRIVATE);
        email = sharedPrefs.getString("email", "");
    }

    public void confirm(Meeting meetingConfirm) {
        if (meetingConfirm.dynamic == 0) {
            apiService.confirmMeeting(meetingConfirm.MeetingID, email)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action() {
                        @Override
                        public void run() throws Exception {
                            Log.i("CONFIRMED","Meeting Confirmed");
                        }
                    });
        }
    }

    public void setOtherTime(DateTime start, DateTime end, Meeting meeting) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM-dd-YYYY HH:mm:ss");
        apiService.setOtherTime(formatter.print(start),formatter.print(end), meeting.MeetingID, email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.d(TAG,"other Time has been set");
                    }
                });
    }

    public void declineMeeting(Meeting infoData) {
        apiService.declineMeeting(infoData.MeetingID, email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.d(TAG,"Meeting has been declined");
                    }
                });
    }

    @Override
    public void onResponse(Call call, Response response) {
    }

    @Override
    public void onFailure(Call call, Throwable t) {
    }
}
