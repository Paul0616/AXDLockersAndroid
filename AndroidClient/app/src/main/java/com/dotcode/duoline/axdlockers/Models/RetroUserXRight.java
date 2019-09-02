package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

public class RetroUserXRight {
    @SerializedName("userId")
    private int userId;
    @SerializedName("rightId")
    private int rightId;
    @SerializedName("right")
    private RetroRight right;



    public int getRightId() {
        return rightId;
    }

    public RetroRight getRight() {
        return right;
    }
}
