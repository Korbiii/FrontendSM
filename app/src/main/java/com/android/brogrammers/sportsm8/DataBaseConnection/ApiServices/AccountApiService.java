package com.android.brogrammers.sportsm8.DataBaseConnection.ApiServices;


import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.UserInfo;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Korbi on 14.09.2017.
 */

public interface AccountApiService {
    @GET("Accounts/AccountInfo")
    Single<UserInfo> getAccountInfo(@Query("email") String email);
}
