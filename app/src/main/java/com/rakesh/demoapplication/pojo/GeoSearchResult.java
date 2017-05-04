package com.rakesh.demoapplication.pojo;

import android.location.Address;

import java.io.Serializable;

/**
 * Created by rakesh_rajyaguru
 * on 23-Jan-17.
 */

public class GeoSearchResult implements Serializable {

    private String address;
    private double mlat;
    private double mlong;


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getMlat() {
        return mlat;
    }

    public void setMlat(double mlat) {
        this.mlat = mlat;
    }

    public double getMlong() {
        return mlong;
    }

    public void setMlong(double mlong) {
        this.mlong = mlong;
    }
}
