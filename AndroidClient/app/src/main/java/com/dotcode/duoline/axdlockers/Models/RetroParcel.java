package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

public class RetroParcel {
    @SerializedName("id")
    private int id;

    @SerializedName("lockerId")
    private int lockerId;

    @SerializedName("buildingResidentId")
    private int buildingResidentId;

    @SerializedName("securityCode")
    private String securityCode;

    @SerializedName("status")
    private int status;


    public RetroParcel(int id, int lockerId, int buildingResidentId, String securityCodee, int status) {
        this.id = id;
        this.buildingResidentId = buildingResidentId;
        this.lockerId = lockerId;
        this.securityCode = securityCodee;
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
