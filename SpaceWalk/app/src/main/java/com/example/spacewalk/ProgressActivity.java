package com.example.spacewalk;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class ProgressActivity extends AppCompatActivity {

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress);

        SharedPreferences myPref = getDefaultSharedPreferences(getApplicationContext());

        int planetCount = myPref.getInt("totalPlanets",0);
        int progress = myPref.getInt("Progress",0);

        // move the image of spaceship along the line
        ConstraintLayout cl = findViewById(R.id.constraintLayoutParent);
        ConstraintSet cs = new ConstraintSet();
        cs.clone(cl);
        cs.setHorizontalBias(R.id.ship, ((float) progress) / 100);
        cs.applyTo(cl);

        TextView progressText = findViewById(R.id.progressPercent);
        progressText.setText(String.format("%d%%", progress));

        Button totalPlanets = findViewById(R.id.totalPlanetsCount);
        totalPlanets.setText(String.valueOf(planetCount));

        Button nextPlanet = findViewById(R.id.nextPlanetCount);
        int daysCalc = Math.round(5 - 5 * (((float) progress) / 100));
        nextPlanet.setText(String.valueOf(daysCalc));
    }

}
