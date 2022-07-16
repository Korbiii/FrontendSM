package com.android.brogrammers.sportsm8.DataBaseConnection.Repositories.impl;

import android.util.Log;

import com.android.brogrammers.sportsm8.DataBaseConnection.APIUtils;
import com.android.brogrammers.sportsm8.DataBaseConnection.ApiServices.AccountApiService;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.UserInfo;
import com.android.brogrammers.sportsm8.DataBaseConnection.Repositories.AccountRepository;
import com.android.brogrammers.sportsm8.UserClasses.LoginScreen;

import io.reactivex.Single;

public class DatabaseAccountRepository implements AccountRepository{

    AccountApiService apiService = APIUtils.getAccountAPIService();

    @Override
    public Single<UserInfo> loadAccountInfo() {
        return apiService.getAccountInfo(LoginScreen.getRealEmail());
    }
}
