package com.oriontech.cokit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class Help extends AppCompatActivity {
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ImageButton btn1 = findViewById(R.id.btn1);
        ImageButton btn2 = findViewById(R.id.btn2);
        ImageButton btn3 = findViewById(R.id.btn3);
        ImageButton btn4 = findViewById(R.id.btn4);
        ImageButton btn5 = findViewById(R.id.btn5);
        intent = new Intent(getApplicationContext(), HelpAnswers.class);
        btn1.setOnClickListener(new click("1"));
        btn2.setOnClickListener(new click("2"));
        btn3.setOnClickListener(new click("3"));
        btn4.setOnClickListener(new click("4"));
        btn5.setOnClickListener(new click("5"));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(R.anim.anim_slide_from_left, R.anim.anim_slide_to_right);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_from_left, R.anim.anim_slide_to_right);
    }

    class click implements View.OnClickListener {
        private final String val;

        click(String val) {
            this.val = val;
        }

        @Override
        public void onClick(View v) {
            intent.putExtra("question", val);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_from_right, R.anim.anim_slide_to_left);
        }
    }
}

