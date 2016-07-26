package com.omkarmoghe.pokemap.models.events;

/**
 * Created by aronhomberg on 25.07.16.
 */
public class InternalExceptionEvent implements IEvent {

    private Exception e;

    public InternalExceptionEvent(Exception e) {
        this.e = e;
    }

    public Exception getE() {
        return e;
    }

    public void setE(Exception e) {
        this.e = e;
    }
}
