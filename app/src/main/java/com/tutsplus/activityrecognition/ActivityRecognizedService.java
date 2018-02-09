package com.tutsplus.activityrecognition;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Paul on 2/1/16.
 */
public class ActivityRecognizedService extends IntentService {
    public static final String ACTION_ACTIVITY_RECOG = "ACTION_ACTIVITY_RECOG";
    public static final String INTENT_EXTRA_ACTIVITY_RECOG = "INTENT_EXTRA_ACTIVITY_RECOG";
    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    private final double ACTIVITY_RECOG_MIN_THRESH = 0.75;
    private final List<Integer> ACCEPTED_ACTIVITIES = Arrays.asList(DetectedActivity.STILL, DetectedActivity.WALKING, DetectedActivity.RUNNING);

    public ActivityRecognizedService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities(result);
        }
    }

    private void handleDetectedActivities(ActivityRecognitionResult result) {

        DetectedActivity mostProbablyActivity = result.getMostProbableActivity();
            if(ACCEPTED_ACTIVITIES.contains(mostProbablyActivity.getType()) &&
                    mostProbablyActivity.getConfidence() > ACTIVITY_RECOG_MIN_THRESH) {
                Intent intent = new Intent();
                intent.setAction(ACTION_ACTIVITY_RECOG);
                intent.putExtra(INTENT_EXTRA_ACTIVITY_RECOG, mostProbablyActivity.getType());
                sendBroadcast(intent);
            }
        }
}
