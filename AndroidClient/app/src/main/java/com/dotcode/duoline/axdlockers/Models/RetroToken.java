package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

public class RetroToken {

    @SerializedName("id")
    private int userId;

    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("tokenExpiresAt")
    private long tokenExpiresAt;

    @SerializedName("isSuperAdmin")
    private int isSuperAdmin;

    public RetroToken(int id, String accessToken, long tokenExpiresAt, int isSuperAdmin) {
        this.userId = id;
        this.accessToken = accessToken;
        this.tokenExpiresAt = tokenExpiresAt;
        this.isSuperAdmin = isSuperAdmin;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getTokenExpiresAt() {
        return tokenExpiresAt;
    }

    public void setTokenExpiresAt(long tokenExpiresAt) {
        this.tokenExpiresAt = tokenExpiresAt;
    }

    public boolean isSuperAdmin() {
        return (isSuperAdmin == 1);
    }

    public void setIsSuperAdmin(boolean isSuperAdmin) {
        if (isSuperAdmin) {
            this.isSuperAdmin = 1;
        } else {
            this.isSuperAdmin = 0;
        }
    }
}
