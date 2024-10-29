package com.example.java;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class Badge extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge);

        imageView = findViewById(R.id.imageView);

        // Load and start the animation as soon as the activity is created
        Animation moveUpAnimation = AnimationUtils.loadAnimation(this, R.anim.move_up);
        imageView.startAnimation(moveUpAnimation);
    }
}
