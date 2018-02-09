package com.tutsplus.activityrecognition;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by kicknickr on 2/9/2018.
 */

public class StepsService extends Service implements SensorEventListener {
    private static final String TAG = StepsService.class.getSimpleName();
    private static final int DEFAULT_MIN_STEP_TRIGGER = 6;
    private static final String MIN_STEPS_TRIGGER_ACTION = "MIN_STEPS_TRIGGER_ACTION";
    // Value of the step counter sensor when the listener was registered.
    // (Total steps are calculated from this value.)
    private int mCounterSteps = 0;

    // Steps counted in current session
    private int mSteps = 0;
    private SensorManager mSensorManager;
    private Sensor mStepDetectorSensor;


    @Override
    public void onCreate() {
        super.onCreate();

        mSensorManager = (SensorManager)
                this.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            mStepDetectorSensor =
                    mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            mSensorManager.registerListener(this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Log.e(TAG, "Could not get a TYPE_STEP_COUNTER sensor.");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int
            startId) {
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {

                /*
                A step counter event contains the total number of steps since the listener
                was first registered. We need to keep track of this initial value to calculate the
                number of steps taken, as the first value a listener receives is undefined.
                 */
            if (mCounterSteps < 1) {
                // initial value
                mCounterSteps = (int) event.values[0];
            }

            // Calculate steps taken based on first counter value received.
            mSteps = (int) event.values[0] - mCounterSteps;

            if (mSteps > DEFAULT_MIN_STEP_TRIGGER) {
                Intent intent = new Intent();
                intent.setAction(MIN_STEPS_TRIGGER_ACTION);
                intent.putExtra("MinStepsTriggered", true);
                sendBroadcast(intent);
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


}