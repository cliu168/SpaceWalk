package com.example.spacewalk;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static java.lang.Integer.parseInt;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

public class PopUpActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_up);

        // load daily step goal
        sharedPref = getDefaultSharedPreferences(getApplicationContext());
        int dailyStep = sharedPref.getInt("DailyStepGoal", 10000);
        editText = findViewById(R.id.editDailySteps);
        editText.setText(String.valueOf(dailyStep));
    }

    public void saveGoal(View view) {
        String amount = editText.getText().toString();

        // invalid input
        if (amount.equals("") || parseInt(amount) <= 0) {
            Context context = getApplicationContext();
            CharSequence err = "Must enter a positive number!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, err, duration);
            toast.show();
            return;
        }

        // save new daily step goal
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("DailyStepGoal", parseInt(amount));
        editor.apply();

        finish();
    }
}
