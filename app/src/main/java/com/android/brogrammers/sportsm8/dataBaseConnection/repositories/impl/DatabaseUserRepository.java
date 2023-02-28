package com.android.brogrammers.sportsm8.dataBaseConnection.repositories.impl;

import com.android.brogrammers.sportsm8.dataBaseConnection.apiServices.AccountApiService;
import com.android.brogrammers.sportsm8.dataBaseConnection.apiServices.FriendshipsApiService;
import com.android.brogrammers.sportsm8.dataBaseConnection.apiServices.MeetingApiService;
import com.android.brogrammers.sportsm8.userClasses.LoginScreen;
import com.android.brogrammers.sportsm8.dataBaseConnection.APIUtils;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Meeting;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.UserInfo;
import com.android.brogrammers.sportsm8.dataBaseConnection.repositories.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.ResponseBody;

public class DatabaseUserRepository implements UserRepository {

    private final MeetingApiService meetingApiService;
    private final AccountApiService accountApiService;
    private final FriendshipsApiService friendshipsApiService;
    String email;

    public DatabaseUserRepository() {
        meetingApiService = APIUtils.getMeetingAPIService();
        accountApiService = APIUtils.getAccountAPIService();
        friendshipsApiService = APIUtils.getFriendshipsAPIService();
        email = LoginScreen.getRealEmail();
    }

    @Override
    public Single<List<UserInfo>> getUsers(final int meetingID) {
        return meetingApiService.getMemberList(meetingID);
    }

    @Override
    public Completable addUsersToMeeting(final int meetingID, final Map<String, String> membersMap) {
        return meetingApiService.addMembersToMeeting(meetingID,membersMap);
    }
////////////////////////////////////////////////////
    @Override
    public Single<ResponseBody> confirmMeeting(final Meeting meeting) {
        if (meeting.dynamic == 0) {
            return Single.fromCallable(new Callable<ResponseBody>() {
                @Override
                public ResponseBody call() throws Exception {
                    return meetingApiService.confirmMeeting2("confirmAtt", meeting.meetingID, LoginScreen.getRealEmail()).execute().body();
                }
            });
        } else {
          return null;
        }
    }

}


