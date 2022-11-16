package com.oriontech.cokit;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class HelpAnswers extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                switch (extras.getString("question")) {
                    case "1":
                        setContentView(R.layout.help_q1);
                        break;
                    case "2":
                        setContentView(R.layout.help_q2);
                        break;
                    case "3":
                        setContentView(R.layout.help_q3);
                        break;
                    case "4":
                        setContentView(R.layout.help_q4);
                        break;
                    case "5":
                        setContentView(R.layout.help_q5);
                        break;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_from_left,R.anim.anim_slide_to_right);
    }
}