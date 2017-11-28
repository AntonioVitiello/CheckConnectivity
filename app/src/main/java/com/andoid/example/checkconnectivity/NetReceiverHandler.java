package com.andoid.example.checkconnectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by antlap on 24/10/2017.
 */

public class NetReceiverHandler extends BroadcastReceiver {
    private static final String LOG_PREFIX = "antlap";
    private static final String LOG_TAG = LOG_PREFIX + NetReceiverHandler.class.getSimpleName();

    private final Map<Integer, Handler> mHandlers = new HashMap<>();
    private State mState;
    private NetworkInfo mNetworkInfo;
    private NetworkInfo mOtherNetworkInfo;
    private String mReason;
    private boolean mIsFailover;

    public enum State {UNKNOWN, CONNECTED, NOT_CONNECTED};


    public NetReceiverHandler(){
        mState = State.UNKNOWN;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "onReceive: " + intent);
        boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        if (noConnectivity) {
            mState = State.NOT_CONNECTED;
        } else {
            mState = State.CONNECTED;
        }
        // WARNING: ConnectivityManager.EXTRA_NETWORK_INFO is deprecated.
        // Since NetworkInfo can vary based on UID, applications should always obtain network information through getActiveNetworkInfo() or getAllNetworkInfo().
        mNetworkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
        mOtherNetworkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
        mReason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
        mIsFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
        Log.d(LOG_TAG, "onReceive: NetworkInfo = " + mNetworkInfo
                + ", OtherNetworkInfo = " + (mOtherNetworkInfo == null ? "[none]" : mOtherNetworkInfo)
                + ", noConnectivity = " + noConnectivity
                + ", State = " + mState);

        // Notifiy any handlers.
        Iterator<Integer> iterator = mHandlers.keySet().iterator();
        while (iterator.hasNext()) {
            Integer messageCode = iterator.next();
            Handler target = mHandlers.get(messageCode);
            Message message = Message.obtain(target, messageCode);
            target.sendMessage(message);
        }
    }


    public void startListening(Handler.Callback callback, int messageCode) {
        Handler target = new Handler(callback);
        mHandlers.put(messageCode, target);
    }

    public void stopListening(int messageCode) {
        mHandlers.remove(messageCode);
    }

    public NetworkInfo getNetworkInfo() {
        return mNetworkInfo;
    }

    public NetworkInfo getOtherNetworkInfo() {
        return mOtherNetworkInfo;
    }

    public State getState() {
        return mState;
    }

    public String getReason() {
        return mReason;
    }

    public boolean isFailover() {
        return mIsFailover;
    }

    public boolean isWiFiConnected() {
        return mNetworkInfo != null
                && mNetworkInfo.isConnected()
                && mNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

}