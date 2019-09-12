package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

public class RetroBuildingResident {
    @SerializedName("id")
    private int id;

    @SerializedName("buildingId")
    private int buildingId;

    @SerializedName("residentId")
    private int residentId;

    @SerializedName("suiteNumber")
    private String unitNumber;

    @SerializedName("building")
    private RetroBuilding building;

    @SerializedName("resident")
    private RetroResident resident;




    public RetroBuildingResident(int id, int buildingId, int residentId, String unitNumber) {
        this.id = id;
        this.buildingId = buildingId;
        this.residentId = residentId;
        this.unitNumber = unitNumber;
    }

    public RetroBuilding getBuilding() {
        return building;
    }

    public RetroResident getResident() {
        return resident;
    }

    public String getUnitNumber() {
        return unitNumber;
    }
}
