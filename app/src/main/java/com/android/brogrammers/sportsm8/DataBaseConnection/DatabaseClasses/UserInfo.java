package com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;

/**
 * Created by Korbi on 05.06.2017.
 */

public class UserInfo implements Parcelable {
    public String email, PPpath, username, friend;
    public DateTime  mystartTime, myendTime;
    public int friendcount, groupcount,meetingcount, confirmed,begin,duration;
    public Boolean selected = false;

    public UserInfo() {
    }

    public UserInfo(String email) {
        this.email = email;
    }

    protected UserInfo(Parcel in) {
        email = in.readString();
        PPpath = in.readString();
        username = in.readString();
        friend = in.readString();
        friendcount = in.readInt();
        groupcount = in.readInt();
        meetingcount = in.readInt();
        confirmed = in.readInt();
        begin = in.readInt();
        duration = in.readInt();
        myendTime = new DateTime(in.readLong());
        mystartTime = new DateTime(in.readLong());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(PPpath);
        dest.writeString(username);
        dest.writeString(friend);
        dest.writeInt(friendcount);
        dest.writeInt(groupcount);
        dest.writeInt(meetingcount);
        dest.writeInt(confirmed);
        dest.writeInt(begin);
        dest.writeInt(duration);
        dest.writeLong(myendTime.getMillis());
        dest.writeLong(mystartTime.getMillis());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    public DateTime getStartDateTime() {
        return mystartTime;
    }

    public DateTime getEndDateTime() {

        return myendTime;
    }

    public void setMystartTime(DateTime mystartTime) {
        this.mystartTime = mystartTime;
    }

    public void setMyendTime(DateTime myendTime) {
        this.myendTime = myendTime;
    }
}
