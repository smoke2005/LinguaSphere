package com.example.java;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MenuActivity extends AppCompatActivity{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_menu);  // Ensure the correct layout file is used

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");

        // Use the username (for example, display it in a TextView)
        TextView usernameTextView = findViewById(R.id.usernameTextView); // Make sure to have this TextView in your layout
        usernameTextView.setText(username);

        Button button1 = findViewById(R.id.levelButton1);

        // Set an OnClickListener to the button
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to open SecondActivity
                Intent intent = new Intent(MenuActivity.this, McqActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);  // Start the new activity
            }
        });

        Button button2 = findViewById(R.id.flash_button);

        // Set an OnClickListener to the button
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to open SecondActivity
                Intent intent = new Intent(MenuActivity.this, FlashcardActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);  // Start the new activity
            }
        });
    }
}
