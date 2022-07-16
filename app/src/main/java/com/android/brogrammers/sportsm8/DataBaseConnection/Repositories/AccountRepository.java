package com.android.brogrammers.sportsm8.DataBaseConnection.Repositories;

import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.UserInfo;

import io.reactivex.Single;

/**
 * Created by Korbi on 14.09.2017.
 */

public interface AccountRepository {
    Single<UserInfo> loadAccountInfo();
}
