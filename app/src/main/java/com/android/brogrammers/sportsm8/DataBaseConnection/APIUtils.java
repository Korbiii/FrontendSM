package com.android.brogrammers.sportsm8.DataBaseConnection;


import com.android.brogrammers.sportsm8.DataBaseConnection.ApiServices.APIService;
import com.android.brogrammers.sportsm8.DataBaseConnection.ApiServices.AccountApiService;
import com.android.brogrammers.sportsm8.DataBaseConnection.ApiServices.FriendshipsApiService;
import com.android.brogrammers.sportsm8.DataBaseConnection.ApiServices.GroupsApiService;
import com.android.brogrammers.sportsm8.DataBaseConnection.ApiServices.MeetingApiService;
import com.android.brogrammers.sportsm8.DataBaseConnection.ApiServices.TeamsApiService;

public class APIUtils {

    private APIUtils(){}

    private static final String BASE_URL = "http://sportsm8.bplaced.net:80/php/dynamicphp/include/";

    public static APIService getAPIService(){
        return RetroFitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static MeetingApiService getMeetingAPIService(){
        return RetroFitClient.getClient(BASE_URL).create(MeetingApiService.class);
    }

    public static FriendshipsApiService getFriendshipsAPIService(){
        return RetroFitClient.getClient(BASE_URL).create(FriendshipsApiService.class);
    }

    public static GroupsApiService getGroupsAPIService(){
        return RetroFitClient.getClient(BASE_URL).create(GroupsApiService.class);
    }

    public static TeamsApiService getTeamsAPIService(){
        return RetroFitClient.getClient(BASE_URL).create(TeamsApiService.class);
    }

    public static AccountApiService getAccountAPIService(){
        return RetroFitClient.getClient(BASE_URL).create(AccountApiService.class);
    }
}
