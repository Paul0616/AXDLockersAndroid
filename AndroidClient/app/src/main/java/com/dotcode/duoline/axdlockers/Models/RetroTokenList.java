package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetroTokenList {
    @SerializedName("items")
    private List<RetroToken> tokens;

    public RetroToken getToken() {
        if (tokens.size() > 0) {
            return tokens.get(0);
        } else
            return null;
    }

    public List<RetroToken> getTokens() {
        return tokens;
    }
}
