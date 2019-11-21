package com.example.smartvalve;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Device_Details {
    public String Status;
    public String Hour_B;
    public String Minute_B;
    public String Hour_T;
    public String Minute_T;
    public String stsSW;

    public Device_Details() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Device_Details(String status, String hour_B, String minute_B, String hour_T, String minute_T,String stssw) {
        this.Status = status;
        this.Hour_B =hour_B;
        this.Minute_B=minute_B;
        this.Hour_T =hour_T;
        this.Minute_T =minute_T;
        this.stsSW = stssw;
    }


}
