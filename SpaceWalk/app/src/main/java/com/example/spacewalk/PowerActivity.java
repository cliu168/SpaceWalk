package com.example.spacewalk;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PowerActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private Button dailyProgress;
    private Button percent;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.power);

        sharedPref = getDefaultSharedPreferences(getApplicationContext());
        dailyProgress = findViewById(R.id.dailyGoalCount);
        percent = findViewById(R.id.percentChangeCount);

        int power = sharedPref.getInt("Power",50);
        ProgressBar powerBar = findViewById(R.id.batteryBar);
        powerBar.setProgress(power);
        TextView powerText = findViewById(R.id.powerPercent);
        powerText.setText(String.format("%d%%", power));

        updateText();
    }

    @SuppressWarnings("deprecation")
    public void editGoal(View view) {
        Intent intent = new Intent(this, PopUpActivity.class);
        startActivityForResult(intent, 1);
    }

    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateText();
    }

    @SuppressLint("DefaultLocale")
    public void updateText() {
        int dailySteps = sharedPref.getInt("DailyStepGoal", 10000);
        int stepsToday = sharedPref.getInt("StepsToday", 0);
        int percentChange = (int) (-10 + 10 * (((float) stepsToday) / dailySteps));
        dailyProgress.setText(String.format("%d/ \n %d", stepsToday, dailySteps));
        percent.setText(String.format("%d%%", percentChange));
    }
}