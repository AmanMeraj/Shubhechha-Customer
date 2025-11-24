package com.subh.shubhechha.utils;

import android.location.Geocoder;
import android.widget.TextView;

/**
 * Created by jawed on 30/3/18.
 */

public class AsyncGeocoderObject {

    public Double lat; // location to get address from
    public Double lng; // location to get address from
    Geocoder geocoder; // the geocoder
    TextView textView;

    public AsyncGeocoderObject(Geocoder geocoder, Double lat, Double lng, TextView textView) {
        this.geocoder = geocoder;
        this.lat = lat;
        this.lng = lng;
        this.textView = textView;
    }
}
