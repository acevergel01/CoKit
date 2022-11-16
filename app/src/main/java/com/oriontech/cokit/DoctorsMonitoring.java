package com.oriontech.cokit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.ArrayList;
import java.util.Collections;

public class DoctorsMonitoring extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    String doctorsCode, patientString;

    ArrayList<String> patientList = new ArrayList<>();
    SharedPreferences prefs;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("name", MODE_PRIVATE);
        doctorsCode = prefs.getString("code", "");
        String name = prefs.getString("fullname", "Name");
        String email = prefs.getString("email", "E-Mail");

        setContentView(R.layout.activity_doctors_monitoring);
        linearLayout = findViewById(R.id.container);
        UpdatePatientData updater = new UpdatePatientData();
        updater.execute();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.headerName);
        TextView navEmail = headerView.findViewById(R.id.headerEmail);
        navUsername.setText(name);
        navEmail.setText(email);

        DrawerLayout drawerLayout = findViewById(R.id.navigation_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_draw_open, R.string.navigation_draw_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aboutus:
                startActivity(new Intent(getApplicationContext(), AboutUs.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.anim_slide_from_right,R.anim.anim_slide_to_left);
                return true;
            case R.id.contactpatient:
                Intent intent1 = new Intent(getApplicationContext(), ContactPatient.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent1.putExtra("Patients", patientString);
                startActivity(intent1);
                overridePendingTransition(R.anim.anim_slide_from_right,R.anim.anim_slide_to_left);
                return true;
            case R.id.aboutcovid:
                startActivity(new Intent(getApplicationContext(), AboutCovid.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.anim_slide_from_right,R.anim.anim_slide_to_left);
                return true;
            case R.id.logout:
                SharedPreferences.Editor editor = getSharedPreferences("name", MODE_PRIVATE).edit();
                editor.putString("username", "");
                editor.putString("fullname", "");
                editor.putString("userType", "");
                editor.putBoolean("isLoggedIn", false);
                editor.apply();
                Intent intent = new Intent(getApplicationContext(), Welcome.class);
                intent.putExtra("finish", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.anim_slide_from_left,R.anim.anim_slide_to_right);
                return true;
            case R.id.profile:
                startActivity(new Intent(getApplicationContext(), EditProfile.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.anim_slide_from_right,R.anim.anim_slide_to_left);
                return true;
            case R.id.help:
                startActivity(new Intent(getApplicationContext(), Help.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.anim_slide_from_right,R.anim.anim_slide_to_left);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("StaticFieldLeak")
    private class UpdatePatientData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            getPatientList(doctorsCode);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            createPatientCard();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        updatePatientData();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    void updatePatientData() {

    }

    void getPatientList(String doctor) {
        Config config = new Config();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String[] field = new String[2];
            field[0] = "doctor";
            field[1] = "table";
            String[] data = new String[2];
            data[0] = doctor;
            data[1] = "patient";
            PutData putData = new PutData(config.getDatabase() + "LoginRegister/getPatient.php", "POST", field, data);
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    String result = putData.getResult();
                    String[] Array = result.split(";");
                    patientString = result;
                    int i = Array.length;

                    while (i > 3) {

                        if (i % 4 == 0) {
                            patientList.add(Array[i - 3]);
                            patientList.add(Array[i - 4]);
                        }
                        i -= 1;
                    }
                    Collections.reverse(patientList);

                }
            }
        });
    }

    void createPatientCard() {
        for (int i = patientList.size(); i > 0; i--) {
            if (i % 2 == 0) {
                View view = getLayoutInflater().inflate(R.layout.fragment_patient_data, null);
                view.setTag(patientList.get(i - 1));
                TextView textview = view.findViewById(R.id.name);
                textview.setText(patientList.get(i - 2));
                linearLayout.addView(view);

            }

        }
    }

    public void click(View v) {
        TextView textview = v.findViewById(R.id.name);
        Intent intent = new Intent(getApplicationContext(), PatientData.class);
        intent.putExtra("Name", textview.getText().toString());
        intent.putExtra("Code", doctorsCode);
        intent.putExtra("Username", (String) v.getTag());
        startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

}