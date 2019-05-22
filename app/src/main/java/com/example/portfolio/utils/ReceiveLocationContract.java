package com.example.portfolio.utils;

import android.location.Location;

public interface ReceiveLocationContract {
    public void receiveLocation(Location loc);
    public void receiveLocationFailure();
}
