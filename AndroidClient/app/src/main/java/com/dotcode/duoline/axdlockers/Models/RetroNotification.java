package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

public class RetroNotification {
    @SerializedName("id")
    private int id;


    public RetroNotification(int id) {
        this.id = id;

    }

    public void setId(int id) {
        this.id = id;
    }



    public int getId() {
        return id;
    }


}
