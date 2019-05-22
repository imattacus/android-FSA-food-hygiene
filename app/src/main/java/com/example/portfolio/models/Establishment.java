package com.example.portfolio.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

@Entity
public class Establishment implements Serializable {
    @PrimaryKey
    @NonNull
    private int id;

    @ColumnInfo(name="name")
    private String name;
    @ColumnInfo(name="rating")
    private int rating;
    @ColumnInfo(name="hasHygiene")
    private boolean hasHygiene;
    @ColumnInfo(name="hygieneScore")
    private int hygieneScore;
    @ColumnInfo
    private String hygieneString;
    @ColumnInfo(name="hasStruc")
    private boolean hasStruc;
    @ColumnInfo(name="strucScore")
    private int strucScore;
    @ColumnInfo
    private String strucString;
    @ColumnInfo(name="hasConfidence")
    private boolean hasConfidence;
    @ColumnInfo(name="confidenceScore")
    private int confidenceScore;
    @ColumnInfo
    private String confidenceString;
    @ColumnInfo(name="longitude")
    private String longitude;
    @ColumnInfo(name="latitude")
    private String latitude;
    @Ignore
    private float distance;
    @Ignore
    private boolean hasDistance;
    @ColumnInfo(name="hasRating")
    private boolean hasRating;
    @ColumnInfo(name="awaitingInspection")
    private boolean awaitingInspection;
    @ColumnInfo(name="exempt")
    private boolean exempt;
    @ColumnInfo(name="address_line1")
    private String address_line1;
    @ColumnInfo(name="address_line2")
    private String address_line2;
    @ColumnInfo(name="address_line3")
    private String address_line3;
    @ColumnInfo(name="address_line4")
    private String address_line4;
    @ColumnInfo(name="postcode")
    private String postcode;
    @ColumnInfo(name="inspectionDateString")
    private String inspectionDateString;
    @ColumnInfo(name="businessType")
    private String businessType;
    @ColumnInfo(name="favourited")
    private boolean favourite;
    @ColumnInfo(name="region")
    private String region;
    @ColumnInfo(name="localAuth")
    private String localAuth;

    @Ignore
    private FavouritesDao dao;

    public Establishment() {
        // Empty constructor for Room
    }

    public Establishment(JSONObject jo) throws JSONException {
        this.awaitingInspection = false;
        this.exempt = false;

        this.id = jo.getInt("FHRSID");
        this.name = jo.getString("BusinessName");

        if (name.contains("\n")) {
            Log.d("EstablishmentJSON", name + " has newlines?");
        }

        // RatingValue can either be: a number, 'Exempt', or 'AwaitingInspection'
        String ratingString = jo.getString("RatingValue");
        if (ratingString.equals("Exempt")) {
            this.hasRating = false;
            this.exempt = true;
            this.rating = 0;
        } else if (ratingString.equals("AwaitingInspection")) {
            this.hasRating = false;
            this.awaitingInspection = true;
            this.rating = 0;
        } else {
            this.hasRating = true;
            this.rating = Integer.parseInt(ratingString);
        }

        // Get score breakdown -- values can be null so have to be checked first
        JSONObject scores = jo.getJSONObject("scores");
        String hygieneString = scores.getString("Hygiene");
        hasHygiene = !hygieneString.equals("null");
        hygieneScore = hasHygiene ? scores.getInt("Hygiene") : 0;

        String confidenceString = scores.getString("ConfidenceInManagement");
        hasConfidence = !confidenceString.equals("null");
        confidenceScore = hasConfidence ? scores.getInt("ConfidenceInManagement") : 0;

        String strucString = scores.getString("Structural");
        hasStruc = !strucString.equals("null");
        strucScore = hasStruc ? scores.getInt("Structural") : 0;

        // Get date
        inspectionDateString = jo.getString("RatingDate");

        // Get business type
        businessType = jo.getString("BusinessType");

        // Get distance from response, it may be null if no location was supplied in request.
        String distanceString = jo.getString("Distance");
        if (distanceString == "null") {
            this.hasDistance = false;
            this.distance = 0.f;
        } else {
            this.hasDistance = true;
            this.distance = Float.parseFloat(distanceString);
        }

        this.address_line1 = jo.getString("AddressLine1");
        this.address_line2 = jo.getString("AddressLine2");
        this.address_line3 = jo.getString("AddressLine3");
        this.address_line4 = jo.getString("AddressLine4");
        this.postcode = jo.getString("PostCode");

        this.latitude = jo.getJSONObject("geocode").getString("latitude");
        this.longitude = jo.getJSONObject("geocode").getString("longitude");

        this.localAuth = jo.getString("LocalAuthorityName");
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public boolean isHasHygiene() {
        return hasHygiene;
    }

    public void setHasHygiene(boolean hasHygiene) {
        this.hasHygiene = hasHygiene;
    }

    public int getHygieneScore() {
        return hygieneScore;
    }

    public void setHygieneScore(int hygieneScore) {
        this.hygieneScore = hygieneScore;
    }

    public boolean isHasStruc() {
        return hasStruc;
    }

    public void setHasStruc(boolean hasStruc) {
        this.hasStruc = hasStruc;
    }

    public int getStrucScore() {
        return strucScore;
    }

    public void setStrucScore(int strucScore) {
        this.strucScore = strucScore;
    }

    public boolean isHasConfidence() {
        return hasConfidence;
    }

    public void setHasConfidence(boolean hasConfidence) {
        this.hasConfidence = hasConfidence;
    }

    public int getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(int confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public boolean isHasDistance() {
        return hasDistance;
    }

    public void setHasDistance(boolean hasDistance) {
        this.hasDistance = hasDistance;
    }

    public boolean isHasRating() {
        return hasRating;
    }

    public void setHasRating(boolean hasRating) {
        this.hasRating = hasRating;
    }

    public boolean isAwaitingInspection() {
        return awaitingInspection;
    }

    public void setAwaitingInspection(boolean awaitingInspection) {
        this.awaitingInspection = awaitingInspection;
    }

    public boolean isExempt() {
        return exempt;
    }

    public void setExempt(boolean exempt) {
        this.exempt = exempt;
    }

    public String getAddress_line1() {
        return address_line1;
    }

    public void setAddress_line1(String address_line1) {
        this.address_line1 = address_line1;
    }

    public String getAddress_line2() {
        return address_line2;
    }

    public void setAddress_line2(String address_line2) {
        this.address_line2 = address_line2;
    }

    public String getAddress_line3() {
        return address_line3;
    }

    public void setAddress_line3(String address_line3) {
        this.address_line3 = address_line3;
    }

    public String getAddress_line4() {
        return address_line4;
    }

    public void setAddress_line4(String address_line4) {
        this.address_line4 = address_line4;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getInspectionDateString() {
        return inspectionDateString;
    }

    public void setInspectionDateString(String inspectionDateString) {
        this.inspectionDateString = inspectionDateString;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public void setFavouriteDao(FavouritesDao dao) {
        this.dao = dao;
    }

    public FavouritesDao getDao() {
        return dao;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getLocalAuth() {
        return localAuth;
    }

    public void setLocalAuth(String localAuth) {
        this.localAuth = localAuth;
    }

    public String getHygieneString() {
        return hygieneString;
    }

    public void setHygieneString(String hygieneString) {
        this.hygieneString = hygieneString;
    }

    public String getStrucString() {
        return strucString;
    }

    public void setStrucString(String strucString) {
        this.strucString = strucString;
    }

    public String getConfidenceString() {
        return confidenceString;
    }

    public void setConfidenceString(String confidenceString) {
        this.confidenceString = confidenceString;
    }

    public Location getLocation() {
        Location location = new Location("");
        location.setLongitude(Float.parseFloat(this.longitude));
        location.setLatitude(Float.parseFloat(this.latitude));
        return location;
    }


}
