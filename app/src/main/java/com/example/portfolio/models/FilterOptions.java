package com.example.portfolio.models;

import java.io.Serializable;

public class FilterOptions implements Serializable {
    private String name;
    private BusinessType businessType;
    private int businessTypePosition = -1;
    private boolean businessTypeAll;
    private boolean showExempt;
    private LocalAuthority localAuthority;
    private int localAuthorityPosition = -1;
    private boolean localAuthorityAll;
    private String region;
    private int regionPosition = -1;
    private boolean regionAll;

    public FilterOptions() {
        // Default constructor
        name = "";
        businessTypeAll = true;
        localAuthorityAll = true;
        regionAll = true;
        showExempt = true;
    }

//    public FilterOptions(String name, BusinessType businessType, boolean showExempt, LocalAuthority localAuthority, String region) {
//        this.name = name;
//        this.businessType = businessType;
//        this.showExempt = showExempt;
//        this.localAuthority = localAuthority;
//        this.region = region;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BusinessType getBusinessType() {
        return businessType;
    }

    public void setBusinessType(BusinessType businessType) {
        this.businessType = businessType;
    }

    public boolean isShowExempt() {
        return showExempt;
    }

    public void setShowExempt(boolean showExempt) {
        this.showExempt = showExempt;
    }

    public LocalAuthority getLocalAuthority() {
        return localAuthority;
    }

    public void setLocalAuthority(LocalAuthority localAuthority) {
        this.localAuthority = localAuthority;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public boolean isBusinessTypeAll() {
        return businessTypeAll;
    }

    public void setBusinessTypeAll(boolean businessTypeAll) {
        this.businessTypeAll = businessTypeAll;
    }

    public boolean isLocalAuthorityAll() {
        return localAuthorityAll;
    }

    public void setLocalAuthorityAll(boolean localAuthorityAll) {
        this.localAuthorityAll = localAuthorityAll;
    }

    public boolean isRegionAll() {
        return regionAll;
    }

    public void setRegionAll(boolean regionAll) {
        this.regionAll = regionAll;
    }

    public int getBusinessTypePosition() {
        return businessTypePosition;
    }

    public void setBusinessTypePosition(int businessTypePosition) {
        this.businessTypePosition = businessTypePosition;
    }

    public int getLocalAuthorityPosition() {
        return localAuthorityPosition;
    }

    public void setLocalAuthorityPosition(int localAuthorityPosition) {
        this.localAuthorityPosition = localAuthorityPosition;
    }

    public int getRegionPosition() {
        return regionPosition;
    }

    public void setRegionPosition(int regionPosition) {
        this.regionPosition = regionPosition;
    }
}
