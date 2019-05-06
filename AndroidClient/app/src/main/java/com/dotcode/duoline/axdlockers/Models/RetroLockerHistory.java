package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

public class RetroLockerHistory {
    @SerializedName("id")
    private int id;
    @SerializedName("qrCode")
    private String qrCode;
    @SerializedName("size")
    private String size;
    @SerializedName("number")
    private String number;
    @SerializedName("lockerAddress")
    private String lockerAddress;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("email")
    private String email;
    @SerializedName("phoneNumber")
    private String phoneNumber;
    @SerializedName("securityCode")
    private String securityCode;
    @SerializedName("suiteNumber")
    private String suiteNumber;
    @SerializedName("name")
    private String buildingName;
    @SerializedName("buildingAddress")
    private String buildingAddress;
    @SerializedName("buildingUniqueNumber")
    private String buildingUniqueNumber;
    @SerializedName("residentAddress")
    private String residentAddress;
    @SerializedName("createdByEmail")
    private String createdByEmail;
    @SerializedName("packageStatus")
    private String psckageStatus;


    public RetroLockerHistory(int id, String qrCode, String number, String size,
                              String lockerAddress, String firstName, String lastName,
                              String email, String phoneNumber, String securityCode,
                              String suiteNumber, String buildingName,
                              String buildingAddress, String residentAddress, String buildingUniqueNumber,
                              String createdByEmail, String packageStatus) {
        this.id = id;
        this.number = number;
        this.qrCode = qrCode;
        this.size = size;
        this.lockerAddress = lockerAddress;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.securityCode = securityCode;
        this.suiteNumber = suiteNumber;
        this.buildingName = buildingName;
        this.buildingAddress = buildingAddress;
        this.residentAddress = residentAddress;
        this.buildingUniqueNumber = buildingUniqueNumber;
        this.createdByEmail = createdByEmail;
        this.psckageStatus = packageStatus;
    }


    public String getLockerAddress() {
        return lockerAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBuildingAddress() {
        return buildingAddress;
    }

    public String getBuildingUniqueNumber() {
        return buildingUniqueNumber;
    }

    public int getId() {
        return id;
    }
}
