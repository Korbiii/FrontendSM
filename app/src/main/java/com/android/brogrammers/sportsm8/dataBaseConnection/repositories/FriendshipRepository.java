package com.android.brogrammers.sportsm8.dataBaseConnection.repositories;

import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Meeting;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.UserInfo;

import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.ResponseBody;

/**
 * Created by Korbi on 07.06.2017.
 */

public interface FriendshipRepository {
   Single<List<UserInfo>> getFriends();
   Single<List<UserInfo>> searchFriends(String searchString);
}
