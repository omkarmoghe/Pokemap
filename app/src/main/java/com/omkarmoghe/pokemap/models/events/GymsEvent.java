package com.omkarmoghe.pokemap.models.events;

import java.util.Collection;

import POGOProtos.Map.Fort.FortDataOuterClass;

/**
 * Created by aronhomberg on 7/23/2016.
 */
public class GymsEvent implements IEvent {

    private Collection<FortDataOuterClass.FortData> gyms;

    public GymsEvent(Collection<FortDataOuterClass.FortData> gyms) {
        this.gyms = gyms;
    }

    public Collection<FortDataOuterClass.FortData> getGyms() {
        return gyms;
    }

    public void setGyms(Collection<FortDataOuterClass.FortData> gyms) {
        this.gyms = gyms;
    }
}
