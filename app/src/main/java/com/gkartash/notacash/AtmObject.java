package com.gkartash.notacash;

import android.location.Location;

/**
 * Created by Gennadiy on 22.06.2014.
 */
public class AtmObject {

    private Location location;
    private String bank;
    private String locationType;
    private String workTime;

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getWorkTime() {
        return workTime;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }
}
