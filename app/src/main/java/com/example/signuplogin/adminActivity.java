package com.example.signuplogin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class adminActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        CardView routine = findViewById(R.id.routine);
        CardView calender = findViewById(R.id.calender);
        CardView waivar = findViewById(R.id.waivar);
        CardView regi = findViewById(R.id.regi);
        CardView res = findViewById(R.id.res);
        CardView sche = findViewById(R.id.sche);

        routine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(adminActivity.this, routineActivity.class);
                startActivity(intent);
            }
        });

        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(adminActivity.this, calenderActivity.class);
                startActivity(intent);
            }
        });

        waivar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(adminActivity.this, waiveradminActivity.class);
                startActivity(intent);
            }
        });

        regi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(adminActivity.this, regiadminActivity.class);
                startActivity(intent);
            }
        });

        res.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(adminActivity.this, resultadminActivity.class);
                startActivity(intent);
            }
        });

        sche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(adminActivity.this, scheduleadminActivity.class);
                startActivity(intent);
            }
        });
    }
}
