package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

public class RetroState {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("countryId")
    private int countryId;

    @SerializedName("country")
    private RetroCountry country;


    public RetroState(int id, String name, int countryId) {
        this.id = id;
        this.name = name;
        this.countryId = countryId;

    }

    public int getCountryId() {
        return countryId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RetroCountry getCountry() {
        return country;
    }

    public void setCountry(RetroCountry country) {
        this.country = country;
    }
}
