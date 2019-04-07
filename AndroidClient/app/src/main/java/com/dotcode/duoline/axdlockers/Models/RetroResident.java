package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

public class RetroResident {
    @SerializedName("id")
    private int id;

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

    public RetroResident(int id, String firstName, String lastName, String email, String phone, String securityCode) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phone;
        this.securityCode = securityCode;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getSecurityCode() {
        return securityCode;
    }
}
