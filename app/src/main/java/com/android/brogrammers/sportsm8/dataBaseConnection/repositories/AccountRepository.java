package com.android.brogrammers.sportsm8.dataBaseConnection.repositories;

import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.UserInfo;

import io.reactivex.Single;

/**
 * Created by Korbi on 14.09.2017.
 */

public interface AccountRepository {
    Single<UserInfo> loadAccountInfo();
}
