package com.tutsplus.activityrecognition;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StepReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        // So we should communicate with MainActivity to increment the count of the relevant geofence,
        // and make the appropriate Toast
    }
}
