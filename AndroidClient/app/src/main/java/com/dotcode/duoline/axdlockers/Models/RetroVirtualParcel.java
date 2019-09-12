package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

public class RetroVirtualParcel {
    @SerializedName("id")
    private int id;

    @SerializedName("buildingResidentId")
    private int buildingResidentId;

    @SerializedName("addressId")
    private int addressId;

    @SerializedName("securityCode")
    private String securityCode;

    @SerializedName("lockerNumber")
    private String lockerNumber;

    @SerializedName("lockerSize")
    private String lockerSize;

    @SerializedName("addressDetails")
    private String addressDetails;

    @SerializedName("status")
    private int status;


    public RetroVirtualParcel(int id, int addressId, int buildingResidentId, String securityCodee, String lockerNumber, String lockerSize, int status) {
        this.id = id;
        this.buildingResidentId = buildingResidentId;
        this.addressId = addressId;
        this.securityCode = securityCodee;
        this.lockerNumber = lockerNumber;
        this.lockerSize = lockerSize;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setAddressDetail(String addressDetails) {
        this.addressDetails = addressDetails;
    }
}
