package com.example.portfolio.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.portfolio.MainActivity;

public class LocationHelper implements LocationListener {

    private Context mContext;
    private LocationManager lm;
    private Location loc;
    private boolean permission;
    private LocationPermissionPresenter permPresenter;

    private ReceiveLocationContract locCallback;

    public LocationHelper(LocationPermissionPresenter permPresenter, Context mContext) {
        // Constructor
        this.permPresenter = permPresenter;
        this.mContext = mContext;
        lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        initLocation();
    }

    private void initLocation() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            attachLocManager();
        } else {
            getPermission();
        }
    }

    private void getPermission() {
        permPresenter.presentPermissionRequest();
    }

    public void permissionDenied() {
        if (locCallback != null) {
            locCallback.receiveLocationFailure();
            locCallback = null;
        }
    }

    public boolean hasPermission() {
        return ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void getLocation(ReceiveLocationContract contract) {
        if (hasPermission() && loc != null) {
            contract.receiveLocation(loc);
        } else {
            //contract.receiveLocationFailure();
            locCallback = contract;
            getPermission();
        }
    }

    public void attachLocManager() {
        // Should be called only when the location permission has been granted
        try {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        } catch (SecurityException e) {
            // Didn't have permission to attach the location manager!
            Log.wtf("Location manager attach", e.getMessage());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (locCallback != null) {
            locCallback.receiveLocation(location);
            locCallback = null;
        }
        loc = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}
