package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

public class RetroAddress {
    @SerializedName("id")
    private int id;

    @SerializedName("streetName")
    private String streetName;

    @SerializedName("zipCode")
    private String zipCode;

    @SerializedName("cityId")
    private int cityId;

    @SerializedName("city")
    private RetroCity city;

    public RetroAddress(int id, String streetName, String zipCode, int cityId, RetroCity city) {
        this.id = id;
        this.streetName = streetName;
        this.zipCode = zipCode;
        this.city = city;
        this.cityId = cityId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCityId() {
        return cityId;
    }

    public RetroCity getCity() {
        return city;
    }

    public String getStreetName() {
        return streetName;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setCity(RetroCity city) {
        this.city = city;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

}
