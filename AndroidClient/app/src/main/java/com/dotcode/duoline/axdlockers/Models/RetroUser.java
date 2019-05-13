package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetroUser {
    @SerializedName("id")
    private int userId;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
