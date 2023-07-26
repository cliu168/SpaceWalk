package com.example.spacewalk;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class StoryActivity extends AppCompatActivity {

    int planetCount;
    @SuppressWarnings("deprecation")
    @SuppressLint({"UseCompatLoadingForColorStateLists"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story);

        SharedPreferences myPref = getDefaultSharedPreferences(getApplicationContext());
        planetCount = myPref.getInt("totalPlanets",0);

        // have unlocked chapters be orange
        if (planetCount >= 1) {
            Button chap2 = findViewById(R.id.chap2);
            chap2.setBackgroundTintList(getResources().getColorStateList(R.color.orange));
        }
    }

    public void goToChapOne(View view) {
        Intent intent = new Intent(this, Chapter1Activity.class);
        startActivity(intent);
    }

    public void goToChapTwo(View view) {
        if (planetCount >= 1) {
            Intent intent = new Intent(this, Chapter2Activity.class);
            startActivity(intent);
        } else {
            Toast.makeText(StoryActivity.this, "Visit 1 planet(s) to unlock!", Toast.LENGTH_SHORT).show();
        }

    }

    public void goToChapNone(View view) {
        Toast.makeText(StoryActivity.this, "More chapters coming soon!", Toast.LENGTH_SHORT).show();
    }
}
