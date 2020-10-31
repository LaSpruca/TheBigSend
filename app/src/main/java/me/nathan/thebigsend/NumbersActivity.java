package me.nathan.thebigsend;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class NumbersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numbers);
        ImageButton btn = findViewById(R.id.BackButton);
        btn.setOnClickListener((view) -> {
            finish();
        });
    }
}