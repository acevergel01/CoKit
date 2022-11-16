package com.oriontech.cokit;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;

public class ContactPatient extends AppCompatActivity {
    LinearLayout linearLayout;
    String doctorsCode, patientString;
    ArrayList<String> patientList = new ArrayList<>();
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_patient);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                patientString = null;
            } else {
                patientString = extras.getString("Patients");
            }
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        prefs = getSharedPreferences("name", MODE_PRIVATE);
        linearLayout = findViewById(R.id.container);
        doctorsCode = prefs.getString("code", "");
        getPatientList();
        createPatientCard();
    }

    void getPatientList() {
        String[] Array = patientString.split(";");

        int i = Array.length;

        while (i > 0) {
            patientList.add(Array[i - 1]);
            i -= 1;
        }
        Collections.reverse(patientList);
    }

    void createPatientCard() {
        for (int i = patientList.size(); i > 3; i--) {
            if (i % 4 == 0) {
                View view = getLayoutInflater().inflate(R.layout.fragment_contact_patient, null);
                view.setTag(patientList.get(i - 3));
                TextView name = view.findViewById(R.id.name);
                name.setText(patientList.get(i - 4));
                TextView email = view.findViewById(R.id.email);
                email.setText(patientList.get(i - 2));
                TextView number = view.findViewById(R.id.number);
                number.setText(patientList.get(i - 1));
                linearLayout.addView(view);
            }

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(R.anim.anim_slide_from_left,R.anim.anim_slide_to_right);
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_from_left,R.anim.anim_slide_to_right);
    }
}