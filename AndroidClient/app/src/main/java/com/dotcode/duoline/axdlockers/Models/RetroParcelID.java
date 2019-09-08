package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

public class RetroParcelID {
    @SerializedName("lockerBuildingResidentId")
    private int lockerBuildingResidentId;


    public RetroParcelID(int lockerBuildingResidentId) {
        this.lockerBuildingResidentId = lockerBuildingResidentId;
    }

    public void setId(int lockerBuildingResidentId) {
        this.lockerBuildingResidentId = lockerBuildingResidentId;
    }



    public int getId() {
        return lockerBuildingResidentId;
    }


}
