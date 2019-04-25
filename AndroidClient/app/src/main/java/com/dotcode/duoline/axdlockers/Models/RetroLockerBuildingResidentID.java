package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

public class RetroLockerBuildingResidentID {
    @SerializedName("lockerBuildingResidentId")
    private int lockerBuildingResidentId;


    public RetroLockerBuildingResidentID(int lockerBuildingResidentId) {
        this.lockerBuildingResidentId = lockerBuildingResidentId;
    }

    public void setId(int lockerBuildingResidentId) {
        this.lockerBuildingResidentId = lockerBuildingResidentId;
    }



    public int getId() {
        return lockerBuildingResidentId;
    }


}
