package com.android.brogrammers.sportsm8.DataBaseConnection.Repositories;

import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Meeting;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.UserInfo;

import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.ResponseBody;

/**
 * Created by Korbi on 07.06.2017.
 */

public interface UserRepository {
   Single<List<UserInfo>> getUsers(int meetingID);
   Completable addUsersToMeeting(int meetingID, Map<String, String> membersMap);
   Single<ResponseBody> confirmMeeting(Meeting meeting);
}
