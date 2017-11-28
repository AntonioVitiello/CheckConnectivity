package com.andoid.example.checkconnectivity;

import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivityHandler extends AppCompatActivity implements Handler.Callback {

    private static final int MESSAGE_CODE_ID = 1;
    private static final String LOG_TAG = "antlap";
    private static TextView mCheckConnectionTextView;

    private Toast mToast;
    private NetReceiverHandler mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCheckConnectionTextView = (TextView) findViewById(R.id.tv_check_connection);

        mReceiver = new NetReceiverHandler();
    }

    private void setConnected(boolean connected) {
        if (connected) {
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

    private void registerBroadcastReceiver(){
        Log.d(LOG_TAG, "registerBroadcastReceiver: ");
        mReceiver.startListening(this, MESSAGE_CODE_ID);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, filter);
    }

    private void unregisterBroadcastReceiver(){
        Log.d(LOG_TAG, "unregisterBroadcastReceiver: ");
        mReceiver.stopListening(MESSAGE_CODE_ID);
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onResume() {
        registerBroadcastReceiver();
        super.onResume();
    }

    @Override
    public void onPause() {
        unregisterBroadcastReceiver();
        super.onPause();
    }

    @Override
    public boolean handleMessage(Message msg) {
        Log.d(LOG_TAG, "handleMessage: NetworkInfo = " + mReceiver.getNetworkInfo()
                + ", OtherNetworkInfo = " + mReceiver.getOtherNetworkInfo()
                + ", state = " + mReceiver.getState()
                + ", reason = " + mReceiver.getReason()
                + ", failOver = " + mReceiver.isFailover()
                + ", isWiFiConnected = " + mReceiver.isWiFiConnected());
        if(mToast != null){
            mToast.cancel();
        }
        mToast = Toast.makeText(this, "Network connectivity: " + mReceiver.getState(), Toast.LENGTH_SHORT);
        mToast.show();

//        setConnected(mConnListener.getNetworkInfo().getState() == NetworkInfo.State.CONNECTED);
        setConnected(mReceiver.getState() == NetReceiverHandler.State.CONNECTED);
        return true;
    }

}
