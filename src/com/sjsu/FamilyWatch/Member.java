package com.sjsu.FamilyWatch;

import android.graphics.Bitmap;
import com.google.android.gms.maps.model.LatLng;

public class Member {
    private String id;
private String phoneNo;
    private String name;
    private LatLng location;
    private long timestamp;
    private Bitmap mImage;


    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public void setImage(String imageUrl) {
        mImage = NetworkFetcher.getImage(imageUrl);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getImage() {
      return mImage;
    }
}
