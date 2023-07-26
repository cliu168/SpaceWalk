package com.example.spacewalk;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spacewalk.databinding.Chapter2Binding;

public class Chapter2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Chapter2Binding binding = Chapter2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}