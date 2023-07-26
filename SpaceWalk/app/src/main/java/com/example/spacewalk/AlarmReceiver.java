package com.example.spacewalk;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class AlarmReceiver extends BroadcastReceiver {
    @SuppressLint("ObsoleteSdkInt")
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPref = getDefaultSharedPreferences(context);

        int power = sharedPref.getInt("Power",50);
        int progress = sharedPref.getInt("Progress",0);
        int dailySteps = sharedPref.getInt("DailyStepGoal", 10000);
        int stepsToday = sharedPref.getInt("StepsToday", 0);
        int stepsYesterday = sharedPref.getInt("StepsYesterday", 0);
        int hour = sharedPref.getInt("Hour", 0);
        SharedPreferences.Editor editor = sharedPref.edit();

        // progress update
        if (power > 0) {
            progress += 1;
            if (progress < 0) { progress = 0; }
            if (progress > 100) { progress = 100; }
        }
        editor.putInt("Progress", progress);
        hour += 1;
        editor.putInt("Hour", hour);

        if (hour == 24) {
            // power update
            power += (int) (-10 + 10 * ((float) stepsToday / dailySteps));
            if (power < 0) {
                power = 0;
            }
            if (power > 100) {
                power = 100;
            }
            editor.putInt("Power", power);
            // reset day
            editor.putInt("Hour", 0);
            editor.putInt("StepsOvermorrow",stepsYesterday);
            editor.putInt("StepsYesterday", stepsToday);
            editor.putInt("StepsToday", 0);
        }
        editor.apply();

        Intent i = new Intent(".MainActivity");
        context.sendBroadcast(i);

        // Step counting when app destroyed, is buggy
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // context.startForegroundService(new Intent(context, StepCountService.class));
        // } else {
            // context.startService(new Intent(context, StepCountService.class));
        // }
        System.out.println("Service is called");
    }
}

