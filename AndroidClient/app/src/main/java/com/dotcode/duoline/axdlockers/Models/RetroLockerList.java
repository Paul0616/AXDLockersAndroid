package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetroLockerList {
    @SerializedName("items")
    private List<RetroLocker> lockers;

    public RetroLocker getLocker() {
        if (lockers.size() > 0) {
            return lockers.get(0);
        } else
            return null;
    }

}
