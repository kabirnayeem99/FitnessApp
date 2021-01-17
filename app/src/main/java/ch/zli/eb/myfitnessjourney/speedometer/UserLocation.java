package ch.zli.eb.myfitnessjourney.speedometer;

import android.location.Location;

public class UserLocation extends Location {

    public UserLocation(Location userLocation) {
        super(userLocation);
    }


    @Override
    public float distanceTo(Location dest) {
        return super.distanceTo(dest);
    }

    @Override
    public double getAltitude() {
        return super.getAltitude();
    }

    @Override
    public float getAccuracy() {
        return super.getAccuracy();
    }

    @Override
    public float getSpeed() {
        return super.getSpeed() * 3.6f;
    }

}
