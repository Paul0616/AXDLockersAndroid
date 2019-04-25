package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

public class RetroLockerBuildingResident {
    @SerializedName("id")
    private int id;

    @SerializedName("lockerId")
    private int lockerId;

    @SerializedName("buildingResidentId")
    private int buildingResidentId;

    @SerializedName("status")
    private int status;


    public RetroLockerBuildingResident(int id, int lockerId, int buildingResidentId, int status) {
        this.id = id;
        this.buildingResidentId = buildingResidentId;
        this.lockerId = lockerId;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getBuildingResidentId() {
        return buildingResidentId;
    }

    public int getLockerId() {
        return lockerId;
    }
}
