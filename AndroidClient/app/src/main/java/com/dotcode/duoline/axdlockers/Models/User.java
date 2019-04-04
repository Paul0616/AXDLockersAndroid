package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class User {
    @SerializedName("id")
    private int userId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
