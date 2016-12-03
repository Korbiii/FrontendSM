package com.example.alex.helloworld;

import java.io.Serializable;

/**
 * Created by alex on 10/30/2016.
 */


public class Information implements Serializable {

    //userInformation
    public String email, PPpath,username,friend;

    //meetingInformation
    public String MeetingID, startTime, endTime;
    public String confirmed = "0";

    //GroupInformation
    public String GroupID, GroupName;

    //SportInformation
    public String team, sportname, sportID;

    //Help Variables
    public Boolean selected = false;
    public int success;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
