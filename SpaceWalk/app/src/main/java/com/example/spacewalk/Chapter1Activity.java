package com.example.spacewalk;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spacewalk.databinding.Chapter1Binding;

public class Chapter1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.spacewalk.databinding.Chapter1Binding binding = Chapter1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}