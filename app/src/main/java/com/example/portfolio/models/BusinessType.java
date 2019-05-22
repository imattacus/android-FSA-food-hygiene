package com.example.portfolio.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class BusinessType implements Serializable {
    private int id;
    private String name;

    public BusinessType(JSONObject jo) throws JSONException {
        this.id = jo.getInt("BusinessTypeId");
        this.name = jo.getString("BusinessTypeName");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
