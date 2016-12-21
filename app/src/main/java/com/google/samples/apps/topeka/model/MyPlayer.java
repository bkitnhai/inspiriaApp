package com.google.samples.apps.topeka.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by NguyenHai on 3/23/2016.
 */
public class MyPlayer implements Parcelable {
    String name;
    String email;
    Integer id;
    Integer score;
    //Bitmap avatar;
    public MyPlayer(String name, String email) {
        this.name =name;
        this.email =email;
       // this.avatar =avatar;
    }
    public Bitmap getImage (){
       // return avatar;
        return null;
    }
    public String getName (){
        return name;
    }
    public String getEmail (){
        return email;
    }
    public Integer getID (){
        return id;
    }
    public Integer getScore (){
        return score;
    }
    protected MyPlayer(Parcel in) {
        name = in.readString();
        email = in.readString();
        //avatar = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<MyPlayer> CREATOR = new Creator<MyPlayer>() {
        @Override
        public MyPlayer createFromParcel(Parcel in) {
            return new MyPlayer(in);
        }

        @Override
        public MyPlayer[] newArray(int size) {
            return new MyPlayer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(email);
       // parcel.writeParcelable(avatar, i);
    }
}
