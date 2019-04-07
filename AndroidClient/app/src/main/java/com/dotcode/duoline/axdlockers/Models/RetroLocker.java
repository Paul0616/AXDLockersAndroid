package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

public class RetroLocker {
    @SerializedName("id")
    private int id;
    @SerializedName("qrCode")
    private String qrCode;
    @SerializedName("size")
    private String size;
    @SerializedName("number")
    private String number;
    @SerializedName("addressId")
    private int addressId;
    @SerializedName("address")
    private RetroAddress address;

    public RetroLocker(int id, String qrCode, String number,  String size, int addressId, RetroAddress address) {
        this.id = id;
        this.number = number;
        this.addressId = addressId;
        this.size = size;
        this.qrCode = qrCode;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public RetroAddress getAddress() {
        return address;
    }

    public String getNumber() {
        return number;
    }

    public String getSize() {
        return size;
    }

    public String getQrCode() {
        return qrCode;
    }
}
