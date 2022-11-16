package com.oriontech.cokit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PatientMainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    CheckBox cbSymptom1, cbSymptom2, cbSymptom3, cbSymptom4, cbSymptom5,
            cbSymptom6, cbSymptom7, cbSymptom8, cbSymptom9, cbSymptom10,
            cbRedFlag1, cbRedFlag2, cbRedFlag3, cbRedFlag4, cbRedFlag5;
    Button btnSaveData;
    EditText txtTempAm, txtTempPm, txtTempNn, txtoxygnAm, txtoxygnNn, txtoxygnPm;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    String username, userType, day = "1", doctorsCode, name, email, currentDay, totalDays, remainingDays;
    DrawerLayout drawerLayout;
    SharedPreferences prefs;
    NumberPicker numberPicker;
    TextView remaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("name", MODE_PRIVATE);
        username = prefs.getString("username", "user");
        userType = prefs.getString("userType", "patient");
        name = prefs.getString("fullname", "name");
        email = prefs.getString("email", "E-mail");
        doctorsCode = prefs.getString("code", "general");
        if (doctorsCode.equals("")) {
            doctorsCode = "General";
        }

        setContentView(R.layout.activity_patient_main);           //setLayout

        NavigationView navigationView = findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.headerName);
        TextView navEmail = headerView.findViewById(R.id.headerEmail);
        navUsername.setText(name);
        navEmail.setText(email);

        drawerLayout = findViewById(R.id.navigation_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_draw_open, R.string.navigation_draw_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        btnSaveData = findViewById(R.id.btnSaveData);
        (cbSymptom1 = findViewById(R.id.symptom1)).setOnCheckedChangeListener(new checkBoxOnclickSymptoms("Symptoms"));
        (cbSymptom2 = findViewById(R.id.symptom2)).setOnCheckedChangeListener(new checkBoxOnclickSymptoms("Symptoms"));
        (cbSymptom3 = findViewById(R.id.symptom3)).setOnCheckedChangeListener(new checkBoxOnclickSymptoms("Symptoms"));
        (cbSymptom4 = findViewById(R.id.symptom4)).setOnCheckedChangeListener(new checkBoxOnclickSymptoms("Symptoms"));
        (cbSymptom5 = findViewById(R.id.symptom5)).setOnCheckedChangeListener(new checkBoxOnclickSymptoms("Symptoms"));
        (cbSymptom6 = findViewById(R.id.symptom6)).setOnCheckedChangeListener(new checkBoxOnclickSymptoms("Symptoms"));
        (cbSymptom7 = findViewById(R.id.symptom7)).setOnCheckedChangeListener(new checkBoxOnclickSymptoms("Symptoms"));
        (cbSymptom8 = findViewById(R.id.symptom8)).setOnCheckedChangeListener(new checkBoxOnclickSymptoms("Symptoms"));
        (cbSymptom9 = findViewById(R.id.symptom9)).setOnCheckedChangeListener(new checkBoxOnclickSymptoms("Symptoms"));
        (cbSymptom10 = findViewById(R.id.symptom10)).setOnCheckedChangeListener(new checkBoxOnclickSymptoms("Symptoms"));
        (cbRedFlag1 = findViewById(R.id.redFlag1)).setOnCheckedChangeListener(new checkBoxOnclickSymptoms("Red Flags"));
        (cbRedFlag2 = findViewById(R.id.redFlag2)).setOnCheckedChangeListener(new checkBoxOnclickSymptoms("Red Flags"));
        (cbRedFlag3 = findViewById(R.id.redFlag3)).setOnCheckedChangeListener(new checkBoxOnclickSymptoms("Red Flags"));
        (cbRedFlag4 = findViewById(R.id.redFlag4)).setOnCheckedChangeListener(new checkBoxOnclickSymptoms("Red Flags"));
        (cbRedFlag5 = findViewById(R.id.redFlag5)).setOnCheckedChangeListener(new checkBoxOnclickSymptoms("Red Flags"));
        /*temp and oxygen*/
        txtTempAm = findViewById(R.id.tblTempAm);
        txtTempNn = findViewById(R.id.tblTempNn);
        txtTempPm = findViewById(R.id.tblTempPm);
        txtoxygnAm = findViewById(R.id.tblOxygnAm);
        txtoxygnPm = findViewById(R.id.tblOxygnPm);
        txtoxygnNn = findViewById(R.id.tblOxygnNn);

        /*set object's data*/

        checkSymptoms("Symptoms");
        checkSymptoms("Red Flags");
        UpdateFirebaseDB("temp", txtTempAm, txtTempNn, txtTempPm);
        UpdateFirebaseDB("oxygen", txtoxygnAm, txtoxygnNn, txtoxygnPm);
        remaining = findViewById(R.id.txtRemainingDays);
        numberPicker = findViewById(R.id.patientNumberPicker);
        if (numberPicker != null) {
            numberPicker.setMinValue(1);
            DatabaseReference tempRef = myRef.child(doctorsCode).child(username).child("Day");
            tempRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("totalDays")) {
                        totalDays = (String) dataSnapshot.child("totalDays").getValue();
                        assert totalDays != null;
                        numberPicker.setMaxValue(Integer.parseInt(totalDays));
                    } else {
                        tempRef.child("totalDays").setValue("14");
                        numberPicker.setMaxValue(14);
                        totalDays = "14";
                    }
                    if (dataSnapshot.hasChild("currentDay")) {
                        currentDay = (String) dataSnapshot.child("currentDay").getValue();
                        assert currentDay != null;
                    } else {
                        tempRef.child("currentDay").setValue("1");
                        currentDay = "1";
                    }
                    numberPicker.setValue(Integer.parseInt(currentDay));
                    remainingDays = String.valueOf(Integer.parseInt(totalDays) - Integer.parseInt(currentDay));
                    remaining.setText(remainingDays);
                    day = currentDay;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }

            });

            numberPicker.setWrapSelectorWheel(true);
            numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
                day = String.valueOf(newVal);
                UpdateFirebaseDB("temp", txtTempAm, txtTempNn, txtTempPm);
                UpdateFirebaseDB("oxygen", txtoxygnAm, txtoxygnNn, txtoxygnPm);
                CheckBox[] cb = new CheckBox[]{cbSymptom1, cbSymptom2, cbSymptom3, cbSymptom4, cbSymptom5,
                        cbSymptom6, cbSymptom7, cbSymptom8, cbSymptom9, cbSymptom10,
                        cbRedFlag1, cbRedFlag2, cbRedFlag3, cbRedFlag4, cbRedFlag5};
                for (CheckBox x : cb) {
                    x.setChecked(false);
                }
                checkSymptoms("Symptoms");
                checkSymptoms("Red Flags");
            });
        }

        btnSaveData.setOnClickListener(v -> {
            saveTemp(getString(txtTempAm), getString(txtTempNn), getString(txtTempPm));
            saveOxygn(getString(txtoxygnAm), getString(txtoxygnNn), getString(txtoxygnPm));
            EditText[] ar = new EditText[]{txtTempAm, txtTempNn, txtTempPm, txtoxygnAm, txtoxygnNn, txtoxygnPm};
            for (EditText x : ar) {
                x.clearFocus();
            }

            Map<String, Object> currentDay = new HashMap<>();
            currentDay.put("currentDay", day);
            myRef.child(doctorsCode).child(username).child("Day").updateChildren(currentDay);
        });

    }

    void UpdateFirebaseDB(String dataType, EditText edittext1, EditText edittext2, EditText edittext3) {
        DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference()
                .child(doctorsCode).child(username).child("Day").child(day).child(dataType);
        tempRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> tempValues = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    tempValues.add(Objects.requireNonNull(dataSnapshot.getValue()).toString());
                }
                if (tempValues.size() == 3) {
                    edittext1.setText(tempValues.get(0));
                    edittext2.setText(tempValues.get(1));
                    edittext3.setText(tempValues.get(2));
                } else {
                    edittext1.setText("");
                    edittext2.setText("");
                    edittext3.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void saveTemp(String tempAm, String tempNn, String tempPm) {
        PatientTemp temp = new PatientTemp();
        temp.setTempAm(tempAm);
        temp.setTempNn(tempNn);
        temp.setTempPm(tempPm);
        myRef.child(doctorsCode).child(username).child("Day").child(day).child("temp").setValue(temp);
    }

    private void saveOxygn(String oxygnAm, String oxygnNn, String oxygnPm) {
        PatientOxygen oxygn = new PatientOxygen();
        oxygn.setOxygnAm(oxygnAm);
        oxygn.setOxygnNn(oxygnNn);
        oxygn.setOxygnPm(oxygnPm);
        myRef.child(doctorsCode).child(username).child("Day").child(day).child("oxygen").setValue(oxygn);
    }

    void checkSymptoms(String symptomType) {
        DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference()
                .child(doctorsCode).child(username).child("Day").child(day).child(symptomType);
        tempRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CheckBox checkBox;
                    checkBox = findViewById(getResources().getIdentifier(dataSnapshot.getKey(), "id", getPackageName()));
                    checkBox.setChecked(true);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aboutus:
                startActivity(new Intent(getApplicationContext(), AboutUs.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.anim_slide_from_right,R.anim.anim_slide_to_left);
                return true;
            case R.id.aboutcovid:
                startActivity(new Intent(getApplicationContext(), AboutCovid.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.anim_slide_from_right,R.anim.anim_slide_to_left);
                return true;
            case R.id.contactdoctor:
                Intent intent1 = new Intent(getApplicationContext(), ContactDoctor.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent1.putExtra("code",doctorsCode);
                startActivity(intent1);
                overridePendingTransition(R.anim.anim_slide_from_right,R.anim.anim_slide_to_left);
                return true;
            case R.id.logout:
                SharedPreferences.Editor editor = getSharedPreferences("name", MODE_PRIVATE).edit();
                editor.clear();
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

    private String getString(EditText text) {
        return text.getText().toString().trim();
    }

    class checkBoxOnclickSymptoms implements CompoundButton.OnCheckedChangeListener {
        private final String symptomType;

        public checkBoxOnclickSymptoms(String symptomType) {
            this.symptomType = symptomType;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.isChecked()) {
                Map<String, Object> symptoms = new HashMap<>();
                symptoms.put(getResources().getResourceEntryName(buttonView.getId()), buttonView.getText().toString());
                myRef.child(doctorsCode).child(username).child("Day").child(day).child(symptomType).updateChildren(symptoms);
            } else {
                myRef.child(doctorsCode).child(username).child("Day").child(day).child(symptomType).child(getResources()
                        .getResourceEntryName(buttonView.getId())).removeValue();
            }
        }
    }

}
