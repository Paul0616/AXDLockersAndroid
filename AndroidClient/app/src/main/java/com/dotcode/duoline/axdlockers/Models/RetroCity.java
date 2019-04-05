package com.dotcode.duoline.axdlockers.Models;

import com.google.gson.annotations.SerializedName;

public class RetroCity {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("stateId")
    private int stateId;

    @SerializedName("state")
    private RetroState state;

    public RetroCity(int id, String name,  int stateId, RetroState state) {
        this.id = id;
        this.name = name;
        this.stateId = stateId;
        this.state = state;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getStateId() {
        return stateId;
    }

    public RetroState getState() {
        return state;
    }

    public void setState(RetroState state) {
        this.state = state;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }
}
