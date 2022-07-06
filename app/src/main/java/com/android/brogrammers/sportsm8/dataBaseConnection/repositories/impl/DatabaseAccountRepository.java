package com.android.brogrammers.sportsm8.dataBaseConnection.repositories.impl;

import com.android.brogrammers.sportsm8.dataBaseConnection.APIUtils;
import com.android.brogrammers.sportsm8.dataBaseConnection.apiServices.AccountApiService;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.UserInfo;
import com.android.brogrammers.sportsm8.dataBaseConnection.repositories.AccountRepository;
import com.android.brogrammers.sportsm8.userClasses.LoginScreen;

import io.reactivex.Single;

public class DatabaseAccountRepository implements AccountRepository{

    AccountApiService apiService = APIUtils.getAccountAPIService();

    @Override
    public Single<UserInfo> loadAccountInfo() {
        return apiService.getAccountInfo(LoginScreen.getRealEmail());
    }
}
