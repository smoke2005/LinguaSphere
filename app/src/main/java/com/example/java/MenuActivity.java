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
        setContentView(R.layout.activity_menu);  // Ensure the correct layout file is used

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");

        // Use the username (for example, display it in a TextView)
        TextView usernameTextView = findViewById(R.id.usernameTextView); // Make sure to have this TextView in your layout
        usernameTextView.setText(username);

        Button button = findViewById(R.id.levelButton1);

        // Set an OnClickListener to the button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to open SecondActivity
                Intent intent = new Intent(MenuActivity.this, MenuActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);  // Start the new activity
            }
        });
    }
}
