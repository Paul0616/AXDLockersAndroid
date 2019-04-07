package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

public class RetroFilteredResident {
    @SerializedName("id")
    private int id;

    @SerializedName("buildingId")
    private int buildingId;

    @SerializedName("suiteNumber")
    private String suiteNumber;

    @SerializedName("resident")
    private RetroResident resident;

    @SerializedName("building")
    private RetroBuilding building;

    public RetroFilteredResident(int id, int buildingId, String suiteNumber, RetroResident resident, RetroBuilding building) {
        this.id = id;
        this.buildingId = buildingId;
        this.resident = resident;
        this.building = building;
        this.suiteNumber = suiteNumber;

    }

    public int getId() {
        return id;
    }

    public RetroResident getResident() {
        return resident;
    }

    public RetroBuilding getBuilding() {
        return building;
    }

    public String getSuiteNumber() {
        return suiteNumber;
    }
}
