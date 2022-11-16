package com.oriontech.cokit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class PatientData extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    String doctorsCode, username, strName, day = "1";
    TextView txtName, txtSymptoms;
    EditText txtTempAm, txtTempPm, txtTempNn, txtoxygnAm, txtoxygnNn, txtoxygnPm;
    int totalDay;
    NumberPicker numberPicker;
    Button add,subtract,ok;
    EditText addDays;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_data);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();
        doctorsCode = intent.getStringExtra("Code");
        username = intent.getStringExtra("Username");
        strName = intent.getStringExtra("Name");
        txtSymptoms = findViewById(R.id.txtPatientSymptoms);
        getSupportActionBar().setTitle(strName);

        txtTempAm = findViewById(R.id.tblTempAm);
        txtTempNn = findViewById(R.id.tblTempNn);
        txtTempPm = findViewById(R.id.tblTempPm);
        txtoxygnAm = findViewById(R.id.tblOxygnAm);
        txtoxygnPm = findViewById(R.id.tblOxygnPm);
        txtoxygnNn = findViewById(R.id.tblOxygnNn);
        EditText[] ar = new EditText[]{txtTempAm, txtTempNn, txtTempPm, txtoxygnAm, txtoxygnNn, txtoxygnPm};
        for (EditText x : ar) {
            x.setEnabled(false);
        }
        checkSymptoms("Symptoms");
        checkSymptoms("Red Flags");
        UpdateFirebaseDB("temp", txtTempAm, txtTempNn, txtTempPm);
        UpdateFirebaseDB("oxygen", txtoxygnAm, txtoxygnNn, txtoxygnPm);

        numberPicker = findViewById(R.id.numberPicker);
        if (numberPicker != null) {
            numberPicker.setMinValue(1);
            numberPicker.setMaxValue(14);
            DatabaseReference tempRef = myRef.child(doctorsCode).child(username).child("Day");
            tempRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild("totalDays")) {
                        tempRef.child("totalDays").setValue("14");
                        numberPicker.setMaxValue(14);
                        totalDay = 14;
                    } else {
                        numberPicker.setMaxValue(Integer.parseInt((String) dataSnapshot.child("totalDays").getValue()));
                        totalDay = Integer.parseInt((String) dataSnapshot.child("totalDays").getValue());
                    }
                    if (!dataSnapshot.hasChild("currentDay")) {
                        tempRef.child("currentDay").setValue("1");
                        numberPicker.setValue(1);
                    } else {
                        numberPicker.setValue(Integer.parseInt((String) dataSnapshot.child("currentDay").getValue()));
                    }

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
                txtSymptoms.setText("");
                checkSymptoms("Symptoms");
                checkSymptoms("Red Flags");
            });

        }
        add = findViewById(R.id.addBtn);
        subtract = findViewById(R.id.removeBtn);
        ok = findViewById(R.id.okBtn);
        addDays = findViewById(R.id.daysQuantity);
        add.setOnClickListener(v -> addDays.setText(String.valueOf(Integer.parseInt(addDays.getText().toString().trim()) + 1)));

        subtract.setOnClickListener(v -> addDays.setText(String.valueOf(Integer.parseInt(addDays.getText().toString().trim()) - 1)));
        ok.setOnClickListener(v -> {
            totalDay = totalDay + (Integer.parseInt(addDays.getText().toString().trim()));
            myRef.child(doctorsCode).child(username).child("Day").child("totalDays").setValue(String.valueOf(totalDay));
        });
    }
    void checkSymptoms(String symptomType) {
        DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference()
                .child(doctorsCode).child(username).child("Day").child(day).child(symptomType);
        tempRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StringBuilder symptom = new StringBuilder();
                if (symptomType.equals("Symptoms") && snapshot.hasChildren()) {
                    snapshot.getChildren();
                    symptom.append("Symptoms:").append("\n\n");
                }
                if (symptomType.equals("Red Flags")&& snapshot.hasChildren()) {
                    snapshot.getChildren();
                    symptom.append("\n").append("Red Flags:").append("\n\n");
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    symptom.append("\u25E6 ").append(dataSnapshot.getValue()).append("\n");
                }
                txtSymptoms.setTextSize(16);
                txtSymptoms.append(symptom);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
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
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}