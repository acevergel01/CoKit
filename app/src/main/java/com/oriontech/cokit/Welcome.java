package com.oriontech.cokit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Welcome extends AppCompatActivity {
    ImageView btnPatient, btnDoctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences("name", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            if (getUserType(prefs).equals("patient")) {
                startActivity(new Intent(getApplicationContext(), PatientMainActivity.class));
                finish();
                return;
            } else if (getUserType(prefs).equals("doctor")) {
                startActivity(new Intent(getApplicationContext(), DoctorsMonitoring.class));
                finish();
                return;
            }
        }
        setContentView(R.layout.activity_welcome);
        btnPatient = findViewById(R.id.btnPatient);
        btnDoctor = findViewById(R.id.btnDoctor);
        btnPatient.setOnClickListener(v -> {
            setUserType("patient");
            startActivity(new Intent(getApplicationContext(), PatientsLogIn.class));
        });

        btnDoctor.setOnClickListener(v -> {
            setUserType("doctor");
            startActivity(new Intent(getApplicationContext(), DoctorsLogIn.class));
        });
    }

    void setUserType(String user) {
        SharedPreferences.Editor editor = getSharedPreferences("name", MODE_PRIVATE).edit();
        editor.putString("userType", user);
        editor.apply();
    }

    String getUserType(SharedPreferences prefs) {
        return prefs.getString("userType", "patient");
    }
}