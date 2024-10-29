package com.example.java;

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


public class LevelPathActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_levelpath);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");


        TextView usernameTextView = findViewById(R.id.usernameTextView); // Make sure to have this TextView in your layout
        usernameTextView.setText(username);

        Button button = findViewById(R.id.circularButton1);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LevelPathActivity.this, MenuActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }
}
