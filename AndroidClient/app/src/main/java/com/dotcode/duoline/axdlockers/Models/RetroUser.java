package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetroUser {
    @SerializedName("id")
    private int userId;
    @SerializedName("role")
    private RetroRole role;
    @SerializedName("buildingXUsers")
    private List<RetroBuildingXUser> buildingXUsers;

    public int getUserId() {
        return userId;
    }

    public RetroRole getRole() {
        return role;
    }

    public List<RetroBuildingXUser> getBuildingXUsers() {
        return buildingXUsers;
    }
}
