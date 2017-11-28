package com.andoid.example.checkconnectivity;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

public class MainActivityObservable extends AppCompatActivity implements Observer {
    private static final String LOG_PREFIX = "antlap";
    private static final String LOG_TAG = LOG_PREFIX + MainActivityObservable.class.getSimpleName();

    private static TextView mCheckConnectionTextView;
    private BroadcastReceiver mNetworkReceiver; // useful for Android 7.0 (Nougat, API level 24) or above
    private boolean mNetworkConnected;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCheckConnectionTextView = (TextView) findViewById(R.id.tv_check_connection);

        // register broadcast receiver
        mNetworkReceiver = new NetReceiverObservable();
    }

    private void showStatus() {
        Log.d(LOG_TAG, "showStatus: NetworkConnected = " + mNetworkConnected);
        if (mNetworkConnected) {
            setTextViewColor("We are back !!!", Color.GREEN, Color.WHITE);
            // visualizza variazione dopo 3 secondi nell stesso thread in cui si trova Handler cioè lo UIThread
            setColorDelayed("Network connected", 3000, Color.WHITE, Color.BLACK);
        } else {
            setTextViewColor("Could not Connect to internet", Color.RED, Color.WHITE);
            setColorDelayed("Network disconnected", 3000, Color.WHITE, Color.BLACK);
        }
    }

    /* Cambia il colore del backgrount della TextView dopo delayMillisec;
       NB: setBackgroundColor verrà eseguito nello stesso thread in cui si trova Handler cioè lo UIThread
       see:
        https://developer.android.com/training/multiple-threads/communicate-ui.html
     */
    private void setColorDelayed(final String msg, int delayMillisec, final int backgroundColor, final int foregroundColor) {
        Runnable delayedRunnable = new Runnable() {
            @Override
            public void run() {
                mCheckConnectionTextView.setBackgroundColor(backgroundColor);
                mCheckConnectionTextView.setTextColor(foregroundColor);
                mCheckConnectionTextView.setText(msg);
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(delayedRunnable, delayMillisec);
    }


    private void setTextViewColor(String msg, int backgroundColor, int foregroundColor) {
        mCheckConnectionTextView.setBackgroundColor(backgroundColor);
        mCheckConnectionTextView.setTextColor(foregroundColor);
        mCheckConnectionTextView.setText(msg);
    }


    /* Apps targeting Android 7.0 (API level 24 Android Nougat) and higher do not receive CONNECTIVITY_ACTION broadcasts
       if they declare the broadcast receiver in their manifest.
       Apps will still receive CONNECTIVITY_ACTION broadcasts if they register their BroadcastReceiver with
       Context.registerReceiver()
       see:
         https://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
       see:
         https://developer.android.com/topic/performance/background-optimization.html#connectivity-action
     */
    private void registerBroadcastReceiver() {
        Log.d(LOG_TAG, "registerBroadcastReceiver: ");

        // add Observer
        NetConnectionObservable.getInstance().addObserver(this);

        // register broadcast receiver
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void unregisterBroadcastReceiver() {
        Log.d(LOG_TAG, "unregisterBroadcastReceiver: ");

        // delete observer
        NetConnectionObservable.getInstance().deleteObserver(this);

        // unregister broadcast receiver
        unregisterReceiver(mNetworkReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBroadcastReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterBroadcastReceiver();
    }

    @Override
    public void update(Observable o, Object data) {
//        mNetworkConnected = (Boolean)data;

        boolean wasConnected = mNetworkConnected;
        mNetworkConnected = NetReceiverObservable.isConnected(this);

        Log.d(LOG_TAG, "update: network is now " + (mNetworkConnected ? "CONNECTED" : "DISCONNECTED"));
        if(mToast != null){
            mToast.cancel();
        }
        mToast = Toast.makeText(this, "Network connectivity: " + (mNetworkConnected ? "CONNECTED" : "DISCONNECTED"), Toast.LENGTH_SHORT);
        mToast.show();
        if(wasConnected != mNetworkConnected) {
            showStatus();
        }
    }

}
