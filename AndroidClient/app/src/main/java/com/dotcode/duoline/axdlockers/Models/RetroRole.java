package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

public class RetroRole {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("hasRelatedBuildings")
    private int hasRelatedBuildings;

    public boolean getHasRelatedBuildings() {
        if(hasRelatedBuildings == 0) return false;
        else return true;
    }
}
