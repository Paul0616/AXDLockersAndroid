package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

public class RetroBuilding {
    @SerializedName("id")
    private int id;
    @SerializedName("buildingUniqueNumber")
    private String buildingUniqueNumber;
    @SerializedName("name")
    private String name;
    @SerializedName("address")
    private RetroAddress address;


    public RetroBuilding(int id, String number, String name,  RetroAddress address) {
        this.id = id;
        this.buildingUniqueNumber = number;
        this.name = name;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public String getBuildingUniqueNumber() {
        return buildingUniqueNumber;
    }

    public String getName() {
        return name;
    }

    public RetroAddress getAddress() {
        return address;
    }


    public String getBuildingAddress() {
        return name + ", " + address.getStreetName() + ", " + address.getCity().getName() + ", " + address.getCity().getState().getName() + ", " + address.getZipCode();
    }

}
