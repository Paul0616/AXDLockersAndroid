package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

public class RetroLockerBuildingResidentID {
    @SerializedName("lockerBuildingResidentId")
    private int lockerBuildingResidentId;
    private int status;


    public RetroLockerBuildingResidentID(int lockerBuildingResidentId, int status) {
        this.lockerBuildingResidentId = lockerBuildingResidentId;
        this.status = status;
    }

    public void setId(int lockerBuildingResidentId) {
        this.lockerBuildingResidentId = lockerBuildingResidentId;
    }



    public int getId() {
        return lockerBuildingResidentId;
    }


}
