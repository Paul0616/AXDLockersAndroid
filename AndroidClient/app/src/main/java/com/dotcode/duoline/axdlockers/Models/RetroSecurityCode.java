package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

public class RetroSecurityCode {
    @SerializedName("securityCode")
    private String securityCode;


    public RetroSecurityCode() {
    }

    public String getSecurityCode() {
        return securityCode;
    }
}
