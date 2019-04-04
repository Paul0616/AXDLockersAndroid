package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetroTokenList {
    @SerializedName("items")
    private List<RetroToken> tokens;

    public RetroToken getToken() {
        return tokens.get(0);
    }

}
