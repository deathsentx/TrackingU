package com.example.trackingu.data;

/**
 * Created by aonauma on 2/24/2017 AD.
 */


public class DataFriend {
    private String mImage;
    private String mFirstname;
    private String mLastname;
    private String mLat;
    private String mLong;
    private String mEmailfriend;

    public DataFriend(String Firstname, String Lastname, String Image, String Lat, String Long, String Emailfriend){
        this.mFirstname = Firstname;
        this.mLastname = Lastname;
        this.mImage = Image;
        this.mLat = Lat;
        this.mLong = Long;
        this.mEmailfriend = Emailfriend;
    }
    public String getmFirstname() {
        return mFirstname;
    }

    public String getmLastname() {
        return mLastname;
    }

    public String getmImage() {
        return mImage;
    }
    public double getmLat() {
        return Double.parseDouble(mLat);
    }
    public double getmLong() {
        return Double.parseDouble(mLong);
    }
    public String getmEmailfriend() {
        return mEmailfriend;
    }


}
