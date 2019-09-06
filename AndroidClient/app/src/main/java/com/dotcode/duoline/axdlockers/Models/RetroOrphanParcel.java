package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

public class RetroOrphanParcel {
    @SerializedName("parcelDescription")
    private String parcelDescriptions;
    @SerializedName("comments")
    private String comments;
    @SerializedName("createdAt")
    private long createdAt;


    public RetroOrphanParcel(String parcelDesc) {
        this.parcelDescriptions = parcelDesc;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getComments() {
        return comments;
    }

    public String getParcelDescriptions() {
        return parcelDescriptions;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

}
