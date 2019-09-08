package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

public class RetroVirtualParcelID {
    @SerializedName("virtualParcelId")
    private int virtualParcelId;


    public RetroVirtualParcelID(int virtualParcelId) {
        this.virtualParcelId = virtualParcelId;
    }

    public int getVirtualParcelId() {
        return virtualParcelId;
    }

    public void setVirtualParcelId(int virtualParcelId) {
        this.virtualParcelId = virtualParcelId;
    }
}
