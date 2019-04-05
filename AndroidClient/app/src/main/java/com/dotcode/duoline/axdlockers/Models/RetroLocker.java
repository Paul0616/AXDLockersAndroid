package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

public class RetroLocker {
    @SerializedName("id")
    private int id;

    public int getUserId() {
        return id;
    }

    public void setUserId(int id) {
        this.id = id;
    }
}
