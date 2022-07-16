package com.android.brogrammers.sportsm8.DataBaseConnection.ApiServices;

import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.Meeting;
import com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses.UserInfo;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Korbi on 08.09.2017.
 */

public interface MeetingApiService {
    @GET("Meetings/Meetings")
    Single<List<Meeting>> getMeetings(@Query("email") String email);

    @GET("Meetings/MemberList")
    Single<List<UserInfo>> getMemberList(@Query("MeetingID") int meetingID);

    @GET("Meetings/MeetingMemberTimes")
    Single<List<UserInfo>> getMeetingMemberTimes(@Query("meetingID") int meetingID);

    @GET("Meetings/isMeetingConfirmed")
    Single<JsonObject> isMeetingConfirmed(@Query("MeetingID") int meetingID);

    @FormUrlEncoded
    @POST("Meetings/newMeeting")
    Completable createMeeting(@Field("startTime") String startTime, @Field("endTime") String endTime, @Field("minPar") int mimMemberCount, @Field("member") String email, @Field("activity") String Activity, @Field("sportID") int sportID, @Field("dynamic") int dynamic, @FieldMap Map<String, String> members, @Field("longitude") double
            longitude, @Field("latitude") double latitude);

    @FormUrlEncoded
    @POST("Meetings/confirmMeeting")
    Completable confirmMeeting(@Field("meetingID") int meetingID, @Field("email") String email);

    @FormUrlEncoded
    @POST("Meetings/setOtherTime")
    Completable setOtherTime(@Field("mystartTime") String startTime, @Field("myendTime") String endTime, @Field("meetingID") int meetingID, @Field("email") String email);

    @FormUrlEncoded
    @POST("Meetings/declineMeeting")
    Completable declineMeeting(@Field("meetingID") int meetingID, @Field("email") String email);

    @FormUrlEncoded
    @POST("Meetings/addMembersToMeeting")
    Completable addMembersToMeeting(@Field("MeetingID") int meetingID, @FieldMap Map<String, String> members);


    @FormUrlEncoded
    @POST("IndexMeetings.php")
    Call<ResponseBody> confirmMeeting2(@Field("function") String function, @Field("meetingID") int meetingID, @Field("email") String email);

}
