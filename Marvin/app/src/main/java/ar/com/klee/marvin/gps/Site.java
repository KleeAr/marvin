package ar.com.klee.marvin.gps;


import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

public class Site {
    private String siteName;
    private String siteAddress;
    private LatLng sitecoordinates;
    private int siteThumbnail;

    public Site(String name, String address, LatLng coordinates, int thumbnail){
        siteName = name;
        siteAddress = address;
        sitecoordinates = coordinates;
        siteThumbnail = thumbnail;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteAddress() {
        return siteAddress;
    }

    public void setSiteAddress(String siteAddress) {
        this.siteAddress = siteAddress;
    }

    public LatLng getCoordinates() {
        return sitecoordinates;
    }

    public void setCoordinates(LatLng coordinates) {
        sitecoordinates = coordinates;
    }

    public int getSiteThumbnail() {
        return siteThumbnail;
    }

    public void setSiteThumbnail(int siteThumbnail) {
        this.siteThumbnail = siteThumbnail;
    }
}

