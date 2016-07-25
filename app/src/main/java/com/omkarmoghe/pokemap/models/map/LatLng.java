package com.omkarmoghe.pokemap.models.map;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chris on 7/24/2016.
 */

public class LatLng implements Parcelable {

    private static final String TAG = "LatLng";

    //Radius of the Earth in km
    public static final double EARTH = 6371;

    public final double latitude;
    public final double longitude;

    public LatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Returns the distance from 'this' point to destination point (using haversine formula).
     *
     * @return Distance between this point and destination point, in same units as radius.
     */
    public double distance(LatLng point){
        //Getting the coordinates and converting them to radians
        double lat = Math.toRadians(latitude);
        double lat2 = Math.toRadians(point.latitude);

        //Getting the differences between the Lat and Long coordinates and
        //converting them to radians
        double difLat = Math.toRadians(point.latitude-latitude);
        double difLong = Math.toRadians(point.longitude - longitude);

        //Calculating the Haversine formula - a
        double a = (Math.pow(Math.sin(difLat/2), 2)) +
                (Math.cos(lat) * Math.cos(lat2) * Math.pow(Math.sin(difLong/2), 2));

        //Calculating the Haversine formula - c
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        //Calculating the distance using c and the radius of the earth
        return EARTH * c;
    }

    /**
     * Returns the destination point from the starting point point having travelled the given distance on the
     * given initial bearing (bearing normally varies around path followed).
     * @param distance - the distance between the current point and the new point
     * @param bearing - the bearing of the object moving (clockwise from north) in degrees (0-360)
     * @return An ArcGIS Point class storing the the Latitude and Longitude in decimal degrees.
     *  The x coordinate is the Longitude, the y coordinate is the Latitude
     */
    public LatLng translatePoint(double distance, double bearing){
        distance = distance/1000d;

        //converts the Latitude, Longitude and bearings into radians
        double lat = Math.toRadians(latitude);
        double lng = Math.toRadians(longitude);
        bearing = Math.toRadians(bearing);

        //Give the distance and the first Latitude, computes the second latitude
        double Lat2 = Math.asin( (Math.sin(lat)*Math.cos(distance/EARTH)) +
                (Math.cos(lat)*Math.sin(distance/EARTH)*Math.cos(bearing)) );

        //Give the distance and the first longitude, computes the second longitude
        double Long2 = lng + Math.atan2(Math.sin(bearing)*Math.sin(distance/EARTH)*Math.cos(lat),
                Math.cos(distance/EARTH)- (Math.sin(lat)*Math.sin(Lat2)) );

        //Converting the new Latitude and Longitude from radians to degrees
        Lat2 = Math.toDegrees(Lat2);
        Long2 = Math.toDegrees(Long2);

        //Creates a point object to return back. X is the longitude, Y is the Latitude
        return new LatLng(Lat2, Long2);
    }

    public com.google.android.gms.maps.model.LatLng toLatLng(){
        return new com.google.android.gms.maps.model.LatLng(latitude, longitude);
    }

    @Override
    public String toString() {
        return "Lat/Long: (" + latitude + ", " + longitude + ')';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
    }

    protected LatLng(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    public static final Parcelable.Creator<LatLng> CREATOR = new Parcelable.Creator<LatLng>() {
        @Override
        public LatLng createFromParcel(Parcel source) {
            return new LatLng(source);
        }

        @Override
        public LatLng[] newArray(int size) {
            return new LatLng[size];
        }
    };
}
