package com.example.spacewalk;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import java.util.ArrayList;
import java.text.*;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import com.example.spacewalk.databinding.StatBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

public class StatsActivity extends AppCompatActivity {
    ArrayList<String> labels = new ArrayList<>();
    ArrayList<Integer>stepData = new ArrayList<>();
    ArrayList<BarEntry> stepStatistics = new ArrayList<>();
    BarChart barchart;
    StatBinding binding;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stat);
        SharedPreferences myPref = getDefaultSharedPreferences(getApplicationContext());
        int totalSteps = myPref.getInt("TotalSteps", 0);
        int stepsToday = myPref.getInt("StepsToday", 0);
        int stepsYesterday = myPref.getInt("StepsYesterday", 0);
        int stepsOvermorrow = myPref.getInt("StepsOvermorrow",0);
        TextView showSteps = findViewById(R.id.totalStepsCount);
        showSteps.setText(Integer.toString(totalSteps));
        stepData.add(stepsOvermorrow);
        stepData.add(stepsYesterday);
        stepData.add(stepsToday);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -2);
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (int i =0 ; i<stepData.size(); i++){
            String date = dateFormat.format(cal.getTime());
            int steps = stepData.get(i);
            stepStatistics.add(new BarEntry(i,steps));
            labels.add(date);
            cal.add(Calendar.DATE, +1);
        }

        barchart = findViewById(R.id.graph);
        BarDataSet barDataSet = new BarDataSet(stepStatistics ,"Step Counts" );
        barDataSet.setColors (ColorTemplate. COLORFUL_COLORS);
        Description description= new Description();
        description.setText("");
        barchart.setDescription (description);
        BarData barData = new BarData (barDataSet);
        barchart.setData(barData);

        XAxis xAxis= barchart.getXAxis();
        xAxis.setValueFormatter (new IndexAxisValueFormatter(labels));

        xAxis.setPosition (XAxis.XAxisPosition.TOP);
        xAxis.setDrawGridLines (false);
        xAxis.setDrawAxisLine (false);
        xAxis.setLabelCount(labels.size());

        binding = StatBinding.inflate(getLayoutInflater());
    }

    public void shareScreen(View view) {
        View v = new View(getApplicationContext());
        shareContent(takeScreenShot(v));
    }

    private Bitmap takeScreenShot(View view) {
        Bitmap bitmap = getScreenBitmap();
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public Bitmap getScreenBitmap() {
        View v = findViewById(android.R.id.content).getRootView();
        v.setDrawingCacheEnabled(true);
        // v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                // View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());

        v.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false); // clear drawing cache
        return b;
    }
    private void shareContent(Bitmap bitmap) {
        String bitmapPath = MediaStore.Images.Media.insertImage(
                binding.getRoot().getContext().getContentResolver(), bitmap, "title", "");
        Uri uri = Uri.parse(bitmapPath);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "App");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out user activity data in the Spacewalk App!");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        binding.getRoot().getContext().startActivity(Intent.createChooser(shareIntent, "Share"));
    }
}

