package com.andoid.example.checkconnectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


/**
 * Created by antlap on 24/10/2017.
 */

public class NetReceiverObservable extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("antlap", "onReceive: Connection changed...");
        NetConnectionObservable.getInstance().connectionChanged();

//        NetConnectionObservable.getInstance().connectionChanged(isConnected(context));
    }

                                            /***** UTILITY METHODS *****/

    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }

    public static boolean isConnected(Context context) {
        NetworkInfo networkInfo = getNetworkInfo(context);
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isWiFiConnected(Context context) {
        NetworkInfo networkInfo = getNetworkInfo(context);
        return networkInfo != null
                && networkInfo.isConnected()
                && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    // see: https://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
}