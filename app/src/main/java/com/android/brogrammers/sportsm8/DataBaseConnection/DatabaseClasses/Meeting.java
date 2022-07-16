package com.android.brogrammers.sportsm8.DataBaseConnection.DatabaseClasses;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;

/**
 * Created by Korbi on 03.06.2017.
 */

public class Meeting extends BaseObservable implements Parcelable {

    public int MeetingID;


    private int confirmed = 0;
    public int minParticipants;
    public int duration;
    public int dynamic;
    public int sportID;
    public int status;
    public String meetingActivity;
    private DateTime startTime;
    private DateTime endTime;
    private DateTime mystartTime;
    private DateTime myendTime;
    private String day;
    public float longitude, latitude;
    private String mytime;

    public Meeting(int meetingID, int minParticipants) {
        super();
        MeetingID = meetingID;
        this.minParticipants = minParticipants;
    }


    protected Meeting(Parcel in) {
        MeetingID = in.readInt();
        confirmed = in.readInt();
        minParticipants = in.readInt();
        duration = in.readInt();
        dynamic = in.readInt();
        sportID = in.readInt();
        status = in.readInt();
        meetingActivity = in.readString();
        endTime = new DateTime(in.readLong());
        mystartTime = new DateTime(in.readLong());
        myendTime = new DateTime(in.readLong());
        startTime = new DateTime(in.readLong());
        day = in.readString();
        longitude = in.readFloat();
        latitude = in.readFloat();
        mytime = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(MeetingID);
        dest.writeInt(confirmed);
        dest.writeInt(minParticipants);
        dest.writeInt(duration);
        dest.writeInt(dynamic);
        dest.writeInt(sportID);
        dest.writeInt(status);
        dest.writeString(meetingActivity);
        dest.writeLong(endTime.getMillis());
        dest.writeLong(mystartTime.getMillis());
        dest.writeLong(myendTime.getMillis());
        dest.writeLong(startTime.getMillis());
        dest.writeString(day);
        dest.writeFloat(longitude);
        dest.writeFloat(latitude);
        dest.writeString(mytime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Meeting> CREATOR = new Creator<Meeting>() {
        @Override
        public Meeting createFromParcel(Parcel in) {
            return new Meeting(in);
        }

        @Override
        public Meeting[] newArray(int size) {
            return new Meeting[size];
        }
    };

    public DateTime getStartDateTime() {
        return startTime;
//        DateTimeFormatter formatter = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss");
//        return formatter.parseDateTime(startTime);
    }

    public DateTime getEndDateTime() {
        return endTime;
    }

    public String getDay() {
        return startTime.toString("yyyy-MM-dd");
    }


    public String getStartTime() {
        return startTime.toString("HH:mm");
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;

    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    public String getEndTime() {
        return endTime.toString("HH:mm");
    }


    public void setStartDateTime(DateTime dateTime) {
        startTime = dateTime;
    }

    public void setEndStartDateTime(DateTime dateTime) {
        endTime = dateTime;
    }

    public String getMytime() {
        if (myendTime != null && mystartTime!=null) {
                return mystartTime.toString("HH:mm - ") + myendTime.toString("HH:mm");
        } else return "";
    }

    public void setConfirmed(int confirmed) {
        this.confirmed = confirmed;
        // notifyPropertyChanged(BR.meeting);
    }

    @Bindable
    public int getConfirmed() {
        return confirmed;
    }


}

