package com.example.spacewalk;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class StepCountService extends Service {

    private SensorManager mSensorManager;
    private Sensor stepCounter;

    private SharedPreferences sharedPreferences;

    private final SensorEventListener listener = new SensorEventListener(){
        @Override
        public void onSensorChanged(SensorEvent event) {
            // ATTEMPT READ STEPS FROM THE LAST RECORDED PERIOD TILL NOW
            int steps = (int) event.values[0];
            long storedSteps = sharedPreferences.getLong("prevSteps", 0);
            long stepsOverTime = -(storedSteps - steps);

            // Save the total steps so far
            sharedPreferences.edit().putLong("prevSteps", steps).apply();

            // String totalSteps = "Total Steps: " + steps;
            String stepsDelta = ", Steps this Period " + stepsOverTime;
            // Toast.makeText(context, totalSteps + stepsDelta, Toast.LENGTH_LONG).show();
            System.out.println("registering Steps" + stepsDelta);

            // Unregister the listener after receiving the first event
            mSensorManager.unregisterListener(this, stepCounter);

            // update shared pref
            int firstTime = sharedPreferences.getInt("FirstTime", 1);
            if (firstTime == 1) {
                sharedPreferences.edit().putInt("FirstTime", 0).apply();
            } else {
                int stepsToday = sharedPreferences.getInt("StepsToday", 0);
                sharedPreferences.edit().putInt("StepsToday", (int) (stepsToday + stepsOverTime)).apply();
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Do nothing
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        Context context = getApplicationContext();
        // Get the step counter sensor
        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        stepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepCounter == null) {
            System.out.println("There is no step counter available");
        }

        // Set up Shared Pref
        sharedPreferences = getDefaultSharedPreferences(context);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Grab prev time, if DNE, then set current time to prev time
        // Date t2 = new Date(System.currentTimeMillis());
        long storedDate = sharedPreferences.getLong("prevTime", 0);
        // Date t1;
        if (storedDate == 0) {
            // t1 = t2;
            sharedPreferences.edit().putLong("prevTime", storedDate).apply();
        }
        // } else {
        // t1 = new Date(storedDate);
        // }
        // Time delta is now set, if t2 == t1, then no time delta is present
        //boolean hasSensor = mSensorManager.requestTriggerSensor(listener, stepCounter);
        boolean hasSensor = mSensorManager.registerListener(listener, stepCounter, SensorManager.SENSOR_DELAY_NORMAL);
        if (!hasSensor) {
            System.out.println("No Sensor Steps Background");
        } else {
            System.out.println("Sensor Steps Background");
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        System.out.println("Service Destroyed");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

