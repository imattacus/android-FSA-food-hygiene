package com.example.portfolio.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class LocalAuthority implements Serializable {
    private int apiId;
    private int laCode;
    private String name;
    private int estCount;
    private String region;

    public LocalAuthority(JSONObject jo) throws JSONException {
        this.apiId = jo.getInt("LocalAuthorityId");
        this.laCode = jo.getInt("LocalAuthorityIdCode");
        this.name = jo.getString("Name");
        this.estCount = jo.getInt("EstablishmentCount");
        this.region = jo.getString("RegionName");
    }

    public int getApiId() {
        return apiId;
    }

    public void setApiId(int apiId) {
        this.apiId = apiId;
    }

    public int getLaCode() {
        return laCode;
    }

    public void setLaCode(int laCode) {
        this.laCode = laCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEstCount() {
        return estCount;
    }

    public void setEstCount(int estCount) {
        this.estCount = estCount;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String toString() {
        return name;
    }
}
