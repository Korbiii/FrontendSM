package com.android.brogrammers.sportsm8.dataBaseConnection.databaseClasses;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import android.os.Parcel;
import android.os.Parcelable;


import org.joda.time.DateTime;
import org.joda.time.LocalDate;

/**
 * Created by Korbi on 03.06.2017.
 */

public class Meeting extends BaseObservable implements Parcelable {

    public int meetingID;


    public int confirmed = 0;
    public int minParticipants;
    public int duration;
    public int dynamic;
    public int sportID;
    public int status;
    public String meetingActivity;
    public DateTime startTime;
    public DateTime endTime;
    public DateTime mystartTime;
    public DateTime myendTime;
    public String day;
    public float longitude, latitude;
    public String mytime;


    public Meeting() {
        super();
    }
    public Meeting(int minParticipants,int duration){
        this.duration = duration;
        this.minParticipants = minParticipants;
        this.startTime = new DateTime();
        this.endTime = new DateTime();
    }


    protected Meeting(Parcel in) {
        meetingID = in.readInt();
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
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        dest.writeInt(meetingID);
        dest.writeInt(confirmed);
        dest.writeInt(minParticipants);
        dest.writeInt(duration);
        dest.writeInt(dynamic);
        dest.writeInt(sportID);
        dest.writeInt(status);
        dest.writeString(meetingActivity);
        dest.writeLong(endTime.getMillis());
        if(mystartTime != null) {
            dest.writeLong(mystartTime.getMillis());
            dest.writeLong(myendTime.getMillis());
        }else{
            dest.writeLong(0);
            dest.writeLong(0);
        }
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

