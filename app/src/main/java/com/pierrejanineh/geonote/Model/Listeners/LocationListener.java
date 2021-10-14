package com.pierrejanineh.geonote.Model.Listeners;

import android.location.Location;

public interface LocationListener {

    /**
     * Listens to FusedLocationProviderClient tasks.
     * @param location Location returned.
     */
    void onSuccess(Location location);
}