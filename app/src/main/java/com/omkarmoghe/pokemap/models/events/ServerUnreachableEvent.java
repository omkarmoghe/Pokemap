package com.omkarmoghe.pokemap.models.events;

import com.pokegoapi.exceptions.RemoteServerException;

/**
 * Created by Jon on 7/23/2016.
 */
public class ServerUnreachableEvent implements IEvent {

    private RemoteServerException e;

    public ServerUnreachableEvent(RemoteServerException e) {
        this.e = e;
    }

    public RemoteServerException getE() {
        return e;
    }

    public void setE(RemoteServerException e) {
        this.e = e;
    }
}
