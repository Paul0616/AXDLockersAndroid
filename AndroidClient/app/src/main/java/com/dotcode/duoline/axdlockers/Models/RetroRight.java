package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

public class RetroRight {
    @SerializedName("id")
    private int id;
    @SerializedName("code")
    private String code;


    public String getCode() {
        return code;
    }
}
