package com.oriontech.cokit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.Objects;

public class EditProfile extends AppCompatActivity {
    SharedPreferences prefs;
    String username, firstname, middlename, surname, number, code, email, userType, oldCode;
    EditText txtUsername, txtSurname, txtFirstname, txtMI, txtEmail, txtPhone, txtReferral;
    EditText oldPass, newPass, newPassConfirm;
    Button btnSave, btnEdit, btnChange;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        prefs = getSharedPreferences("name", MODE_PRIVATE);
        userType = prefs.getString("userType", "patient");
        username = prefs.getString("username", "user");
        firstname = prefs.getString("firstname", "FN");
        middlename = prefs.getString("middlename", "MN");
        surname = prefs.getString("surname", "SN");
        number = prefs.getString("number", "NUM");
        code = prefs.getString("code", "CODE");
        email = prefs.getString("email", "EMAIL");


        txtUsername = findViewById(R.id.usernameEdit);
        txtUsername.setText(username);
        txtSurname = findViewById(R.id.surnameEdit);
        txtSurname.setText(surname);
        txtFirstname = findViewById(R.id.firstnameEdit);
        txtFirstname.setText(firstname);
        txtMI = findViewById(R.id.middleinitialEdit);
        txtMI.setText(middlename);
        txtEmail = findViewById(R.id.emailEdit);
        txtEmail.setText(email);
        txtPhone = findViewById(R.id.phonenoEdit);
        txtPhone.setText(number);
        txtReferral = findViewById(R.id.refCodeEdit);
        txtReferral.setText(code);
        btnSave = findViewById(R.id.btnSave);
        btnEdit = findViewById(R.id.btnEdit);
        btnChange = findViewById(R.id.btnChange);

        oldCode = code;
        btnChange.setOnClickListener(v -> {
            View customAlertDialogView = LayoutInflater.from(this)
                    .inflate(R.layout.dialog_change_password, null, false);
            dialog = new AlertDialog.Builder(EditProfile.this)
                    .setView(customAlertDialogView)
                    .setTitle("Change Password")
                    .setPositiveButton(android.R.string.ok, null)
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();

            dialog.setOnShowListener(dialogInterface -> {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(view -> {
                    oldPass = customAlertDialogView.findViewById(R.id.oldPass);
                    newPass = customAlertDialogView.findViewById(R.id.newPass);
                    newPassConfirm = customAlertDialogView.findViewById(R.id.newPass2);
                    if (newPass != null && newPassConfirm != null) {
                        if (getString(newPass).equals(getString(newPassConfirm))) {
                            checkOldPassword(getString(oldPass));
                        } else {
                            ((TextInputLayout) customAlertDialogView.findViewById(R.id.newPassLayout)).setError("Passwords do not match");
                        }
                    }
                });
            });
            dialog.show();
            ((EditText) customAlertDialogView.findViewById(R.id.newPass)).addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    ((TextInputLayout) customAlertDialogView.findViewById(R.id.newPassLayout)).setError(null);
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                }

                @Override
                public void afterTextChanged(Editable arg0) {
                }
            });
        });

        btnSave.setOnClickListener(v -> {
            Config config = new Config();
            if (validEmail(getString(txtEmail))) {
                if (isNotEmpty(txtSurname) && isNotEmpty(txtFirstname) && isNotEmpty(txtUsername) &&
                        isNotEmpty(txtEmail) && isNotEmpty(txtPhone)) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        String[] field = {"fullname", "email", "username", "firstname",
                                "middlename", "surname", "number", "doctor", "userType"};
                        String[] data = {getString(txtFirstname) + " " + getString(txtMI) + " " + getString(txtSurname), getString(txtEmail),
                                getString(txtUsername), getString(txtFirstname), getString(txtMI), getString(txtSurname), getString(txtPhone),
                                getString(txtReferral), userType};
                        PutData putData = new PutData(config.getDatabase() + "LoginRegister/UpdateProfile.php", "POST", field, data);
                        if (putData.startPut()) {
                            if (putData.onComplete()) {
                                String result = putData.getResult();
                                if (result.equals("Update Success")) {
                                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                    EditText[] ar = new EditText[]{txtSurname, txtFirstname, txtMI, txtEmail, txtPhone, txtReferral};
                                    SharedPreferences.Editor editor = getSharedPreferences("name", MODE_PRIVATE).edit();
                                    editor.putString("fullname", getString(txtFirstname) + " " + getString(txtMI) + " " + getString(txtSurname));
                                    editor.putString("email", getString(txtEmail));
                                    editor.putString("firstname", getString(txtFirstname));
                                    editor.putString("middlename", getString(txtMI));
                                    editor.putString("surname", getString(txtSurname));
                                    editor.putString("number", getString(txtPhone));
                                    editor.putString("code", getString(txtReferral));
                                    editor.apply();
                                    FirebaseDatabase.getInstance().getReference().child(oldCode).child(username)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (oldCode.equals("")){
                                                        FirebaseDatabase.getInstance().getReference().child(getString(txtReferral)).child(username).setValue(dataSnapshot.getValue());
                                                        FirebaseDatabase.getInstance().getReference().child("general").child(username).setValue(null);
                                                    }
                                                    else{
                                                        if(getString(txtReferral).equals("")){
                                                            FirebaseDatabase.getInstance().getReference().child("general").child(username).setValue(dataSnapshot.getValue());
                                                            FirebaseDatabase.getInstance().getReference().child(oldCode).child(username).setValue(null);
                                                            oldCode = getString(txtReferral);
                                                        }
                                                        FirebaseDatabase.getInstance().getReference().child(getString(txtReferral)).child(username).setValue(dataSnapshot.getValue());
                                                        FirebaseDatabase.getInstance().getReference().child(oldCode).child(username).setValue(null);
                                                    }
                                                    oldCode = getString(txtReferral);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                    for (EditText x : ar) {
                                        x.setEnabled(false);
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                }
            } else {
                txtEmail.setError("Invalid email");
                Toast.makeText(getApplicationContext(), "Enter a valid email address", Toast.LENGTH_SHORT).show();
            }

        });
        btnEdit.setOnClickListener(v -> {
            EditText[] ar = new EditText[]{txtSurname, txtFirstname, txtMI, txtEmail, txtPhone, txtReferral};
            for (EditText x : ar) {
                x.setEnabled(true);
            }
        });

    }

    void checkOldPassword(String password) {
        Config config = new Config();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String[] field = {"username", "password", "table"};
            String[] data = {username, password, userType};
            PutData putData = new PutData(config.getDatabase() + "LoginRegister/login.php", "POST", field, data);
            if (putData.startPut()) {
                while (putData.isAlive()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult();
                        if (result.equals("Login Success")) {
                            updatePassword(getString(newPass));
                        }
                    }
                }
            }
        });

    }

    void updatePassword(String password) {
        Config config = new Config();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String[] field = {"email", "password", "table"};
            String[] data = {prefs.getString("email", "EMAIL"), password, userType};

            PutData putData = new PutData(config.getDatabase() + "LoginRegister/update_password.php", "POST", field, data);
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    String result = putData.getResult();
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }
        });
    }

    String getString(EditText text) {
        return Objects.requireNonNull(text.getText()).toString();
    }

    boolean isNotEmpty(EditText text) {
        if (TextUtils.isEmpty(getString(text))) {
            text.setError(text.getHint().toString() + " can not be empty");
            return false;
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();

        Intent intent;
        if (userType.equals("patient")) {
            intent = new Intent(getApplicationContext(), PatientMainActivity.class);
        } else {
            intent = new Intent(getApplicationContext(), DoctorsMonitoring.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_from_left, R.anim.anim_slide_to_right);
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();

        Intent intent;
        if (userType.equals("patient")) {
            intent = new Intent(getApplicationContext(), PatientMainActivity.class);
        } else {
            intent = new Intent(getApplicationContext(), DoctorsMonitoring.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_from_left, R.anim.anim_slide_to_right);
    }

    private boolean validEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}