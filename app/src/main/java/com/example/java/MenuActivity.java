package com.example.java;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

// Base class for common activity features (if needed)
abstract class BaseActivity extends AppCompatActivity {
    protected String username;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    protected void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        intent.putExtra("username", username);
        startActivity(intent);
    }
}

// MenuActivity class inheriting from BaseActivity
public class MenuActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_menu);

        // Retrieve username from Intent
        username = getIntent().getStringExtra("username");

        TextView usernameTextView = findViewById(R.id.usernameTextView);
        usernameTextView.setText(username);

        Button button1 = findViewById(R.id.levelButton1);
        button1.setOnClickListener(v -> navigateToActivity(McqActivity.class));

        Button button2 = findViewById(R.id.flash_button);
        button2.setOnClickListener(v -> navigateToActivity(FlashcardActivity.class));

        Button button3 = findViewById(R.id.levelButton3);
        button3.setOnClickListener(v -> navigateToActivity(Jumbled_words.class));

        Button button4 = findViewById(R.id.levelButton2);
        button4.setOnClickListener(v -> navigateToActivity(QuizActivity.class));
    }
}
