package com.android.brogrammers.sportsm8.DataBaseConnection.Repositories.impl;

import com.android.brogrammers.sportsm8.DataBaseConnection.ApiServices.MeetingApiService;
import com.android.brogrammers.sportsm8.UserClasses.LoginScreen;
import com.android.brogrammers.sportsm8.DataBaseConnection.APIUtils;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Meeting;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.UserInfo;
import com.android.brogrammers.sportsm8.DataBaseConnection.Repositories.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.ResponseBody;

public class DatabaseUserRepository implements UserRepository {

    private MeetingApiService apiService;

    public DatabaseUserRepository() {
        apiService = APIUtils.getMeetingAPIService();
    }

    @Override
    public Single<List<UserInfo>> getUsers(final int meetingID) {
        return apiService.getMemberList(meetingID);
    }

    @Override
    public Completable addUsersToMeeting(final int meetingID, final Map<String, String> membersMap) {
        return apiService.addMembersToMeeting(meetingID,membersMap);
    }
////////////////////////////////////////////////////
    @Override
    public Single<ResponseBody> confirmMeeting(final Meeting meeting) {
        if (meeting.dynamic == 0) {
            return Single.fromCallable(new Callable<ResponseBody>() {
                @Override
                public ResponseBody call() throws Exception {
                    return apiService.confirmMeeting2("confirmAtt", meeting.MeetingID, LoginScreen.getRealEmail()).execute().body();
                }
            });
        } else {
          return null;
        }
    }


}


