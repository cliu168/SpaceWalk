package com.example.spacewalk;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;
import android.Manifest.permission;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private final boolean runningQOrLater =
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;

    private SharedPreferences sharedPref;
    private ProgressBar powerBar;
    private ProgressBar progressBar;
    private TextView powerPercent;
    private TextView progressPercent;
    private TextView showStepsMain;
    private ImageView notification;
    private SensorManager sensorManager;
    private int reportedSteps;

    @SuppressLint({"ShortAlarm", "SetTextI18n", "InlinedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get shared preferences
        Context context = getApplicationContext();
        sharedPref = getDefaultSharedPreferences(context);

        // need to request permissions on API level 29 and above
        if (runningQOrLater) {
            checkPermission(permission.ACTIVITY_RECOGNITION, 100);
        }
        if (!HasGotSensorCaps()) {
            Toast.makeText(MainActivity.this, "Your Device Does Not have the needed Sensors!", Toast.LENGTH_SHORT).show();
        }

        // Setting up Background Listeners
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        Intent i = new Intent(this, AlarmReceiver.class);
        i.setAction("BackGroundStepsTrack");
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, i,
                PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 60 * 1000, alarmIntent);
        // timing: millis,AlarmManager.INTERVAL_HOUR

        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);

        // Reference/Assign the sensors
        Sensor senStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(this, senStepCounter, SensorManager.SENSOR_DELAY_NORMAL);

        setContentView(R.layout.activity_main);

        // elements that get updated
        powerBar = findViewById(R.id.powerBar);
        progressBar = findViewById(R.id.progressBar);
        powerPercent = findViewById(R.id.powerPercentHome);
        progressPercent = findViewById(R.id.progressPercentHome);
        showStepsMain = findViewById(R.id.debugSteps);
        notification = findViewById(R.id.notification);
        int power = sharedPref.getInt("Power",50);
        int progress = sharedPref.getInt("Progress",0);
        int stepsToday = sharedPref.getInt("StepsToday", 0);
        powerBar.setProgress(power);
        powerPercent.setText(power + "%");
        progressBar.setProgress(progress);
        progressPercent.setText(progress + "%");
        showStepsMain.setText(Integer.toString(stepsToday));

        // display planet if progress full
        if (progress == 100) {
            ImageView planet = findViewById(R.id.planet);
            planet.setVisibility(View.VISIBLE);
        }
        // display notification if new story not viewed
        int newStory = sharedPref.getInt("newStory",1);
        if (newStory == 1) {
            notification.setVisibility(View.VISIBLE);
        }

        // ship animation
        ImageView ship = findViewById(R.id.ship);
        Animation aniFloat = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.floating);
        ship.startAnimation(aniFloat);


        // update bars after alarm runs
        IntentFilter filter = new IntentFilter(".MainActivity");
        registerReceiver(new BroadcastReceiver(){
            public void onReceive(Context context, Intent intent) {
                int power = sharedPref.getInt("Power",50);
                int progress = sharedPref.getInt("Progress",0);
                int stepsToday = sharedPref.getInt("StepsToday", 0);
                powerBar.setProgress(power);
                powerPercent.setText(power + "%");
                progressBar.setProgress(progress);
                progressPercent.setText(progress + "%");
                showStepsMain.setText(Integer.toString(stepsToday));
                if (progress == 100) {
                    ImageView planet = findViewById(R.id.planet);
                    Animation aniIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
                    planet.startAnimation(aniIn);
                    planet.setVisibility(View.VISIBLE);
                }
            }
        }, filter);
    }

    @Override
    protected void onDestroy() {
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR));
        super.onDestroy();
    }

    // Function to check and request permission
    public void checkPermission(String permission, int requestCode) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(MainActivity.this, "Activity Permission Granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Checking whether user granted the permission or not.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Showing the toast message
            // Toast.makeText(MainActivity.this, "Activity Permission Granted", Toast.LENGTH_SHORT).show();
            recreate(); // need to reset app so steps work
        }
        else {
            Toast.makeText(MainActivity.this, "Activity Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    // Check if the phone has the correct sensors to track steps
    private boolean HasGotSensorCaps() {
        PackageManager pm = MainActivity.this.getPackageManager();

        // Require at least Android KitKat
        int currentApiVersion = Build.VERSION.SDK_INT;

        // Check that the device supports the step counter and detector sensors
        return currentApiVersion >= 19
                && pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER);
    }

    // Method registered with the sensor manager which is called upon a sensor
    // update event, this is set via the registerSensors() method.
    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (reportedSteps < 1) {
            // Log the initial value
            reportedSteps = (int)event.values[0];
        }

        // Calculate steps taken based on first value received.
        int stepsTaken = (int) event.values[0] - reportedSteps;
        // Output the value to the simple GUI
        Toast.makeText(MainActivity.this, "Steps Taken = " + stepsTaken, Toast.LENGTH_SHORT).show();

        if (stepsTaken > 0) {
            // update steps counter
            int steps = sharedPref.getInt("StepsToday", 0);
            showStepsMain.setText(Integer.toString(steps+1));

            // update saved steps
            sharedPref.edit().putInt("StepsToday", steps+1).apply();
            int total = sharedPref.getInt("TotalSteps", 0);
            sharedPref.edit().putInt("TotalSteps", total+1).apply();
        }
    }

    // Method registered with the sensor manager which is called upon a sensor
    // accuracy update event, this is set via the registerSensors() method.
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    // Switch activity
    public void goToPower(View view) {
        Intent intent = new Intent(this, PowerActivity.class);
        startActivity(intent);
    }

    public void goToProgress(View view) {
        SharedPreferences.Editor editor = sharedPref.edit();
        int progress = sharedPref.getInt("Progress",0);
        int planetCount = sharedPref.getInt("totalPlanets",0);
        if (progress == 100) { // reach planet
            editor.putInt("Progress",0);
            editor.putInt("totalPlanets", planetCount+1);
            progressBar.setProgress(0);
            progressPercent.setText("0%");
            // image fade out
            ImageView planet = findViewById(R.id.planet);
            Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
            planet.startAnimation(aniFade);
            planet.setVisibility(View.GONE);
            // notification of new story
            if (planetCount + 1 == 1) {
                notification.setVisibility(View.VISIBLE);
                editor.putInt("newStory",1);
            }
            editor.apply();
        } else { // launch activity
            Intent intent = new Intent(this, ProgressActivity.class);
            startActivity(intent);
        }
    }

    public void goToStory(View view) {
        Intent intent = new Intent(this, StoryActivity.class);
        // remove notification
        startActivity(intent);
        notification.setVisibility(View.GONE);
        sharedPref.edit().putInt("newStory",0).apply();
    }

    public void goToStats(View view) {
        Intent intent = new Intent(this, StatsActivity.class);
        startActivity(intent);
    }

    // debug function to increment steps
    @SuppressLint("SetTextI18n")
    public void incSteps(View view) {
        int steps = sharedPref.getInt("StepsToday", 0);
        showStepsMain.setText(Integer.toString(steps+1));

        sharedPref.edit().putInt("StepsToday", steps+1).apply();
        int total = sharedPref.getInt("TotalSteps", 0);
        sharedPref.edit().putInt("TotalSteps", total+1).apply();
    }

}
