package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

public class RetroBuildingXUser {
    @SerializedName("buildingId")
    private String buildingId;
    @SerializedName("building")
    private RetroBuilding building;

    public String getBuildingId() {
        return buildingId;
    }

    public RetroBuilding getBuilding() {
        return building;
    }
}
