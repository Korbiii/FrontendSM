package com.android.brogrammers.sportsm8.dataBaseConnection.repositories.impl;

import com.android.brogrammers.sportsm8.dataBaseConnection.APIUtils;
import com.android.brogrammers.sportsm8.dataBaseConnection.apiServices.FriendshipsApiService;
import com.android.brogrammers.sportsm8.dataBaseConnection.apiServices.TeamsApiService;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.Team;
import com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses.UserInfo;
import com.android.brogrammers.sportsm8.dataBaseConnection.repositories.FriendshipRepository;
import com.android.brogrammers.sportsm8.dataBaseConnection.repositories.TeamsRepository;
import com.android.brogrammers.sportsm8.userClasses.LoginScreen;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by Korbi on 16.06.2017.
 */

public class DatabaseFriendshipRepository implements FriendshipRepository {

    private final FriendshipsApiService friendshipsApiService;
    private final String email;

    public DatabaseFriendshipRepository() {
        friendshipsApiService = APIUtils.getFriendshipsAPIService();
        email = LoginScreen.getRealEmail();
    }

    @Override
    public Single<List<UserInfo>> getFriends() {
        return friendshipsApiService.getFriends(email);
    }

    @Override
    public Single<List<UserInfo>> searchFriends(String searchString) {
        return friendshipsApiService.searchFriends(email,searchString);
    }
}


