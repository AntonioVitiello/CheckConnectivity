package com.andoid.example.checkconnectivity;

import java.util.Observable;

/**
 * Created by antlap on 24/10/2017.
 */

public class NetConnectionObservable extends Observable {
    private static NetConnectionObservable instance = new NetConnectionObservable();

    private NetConnectionObservable() {
    }

    public static NetConnectionObservable getInstance() {
        return instance;
    }

    public void connectionChanged() {
        synchronized(this) {
            setChanged();
            notifyObservers();
        }
    }

    public void connectionChanged(boolean connected) {
        synchronized(this) {
            setChanged();
            notifyObservers(Boolean.valueOf(connected));
        }
    }

}