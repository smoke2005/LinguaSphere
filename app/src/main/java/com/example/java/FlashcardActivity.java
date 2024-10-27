package com.example.java;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlashcardActivity extends AppCompatActivity {

    private CardView card_front, card_back;
    public  TextView FlashcardRedirectText;
    private TextView textFront, textBack;  // Declare TextViews
    private Button buttonPrevious, buttonNext;  // Declare Buttons
    private boolean isFront = true;
    private int currentIndex = 0;

    // Sample data for words and translations
    private List<Map<String, String>> words = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip);

        // Initialize Firestore
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Populate word list
        Map<String, String> word1 = new HashMap<>();
        word1.put("English Word", "Hello");
        word1.put("French Translation", "Bonjour");
        words.add(word1);

        Map<String, String> word2 = new HashMap<>();
        word2.put("English Word", "How are you?");
        word2.put("French Translation", "Comment ça va?");
        words.add(word2);

        Map<String, String> word3 = new HashMap<>();
        word3.put("English Word", "Cat");
        word3.put("French Translation", "Katze");
        words.add(word3);

        Map<String, String> word4 = new HashMap<>();
        word4.put("English Word", "Tree");
        word4.put("French Translation", "Baum");
        words.add(word4);

        Map<String, String> word5 = new HashMap<>();
        word5.put("English Word", "Book");
        word5.put("French Translation", "Buch");
        words.add(word5);

        // Find card views and text views
        card_front = findViewById(R.id.card_front);
        card_back = findViewById(R.id.card_back);
        textFront = findViewById(R.id.text_front);
        textBack = findViewById(R.id.text_back);

        // Find buttons
        buttonPrevious = findViewById(R.id.button_previous);
        buttonNext = findViewById(R.id.button_next);

        // Display the first word and translation
        updateCardText(currentIndex);
        Button buttonNavigate = findViewById(R.id.button_navigate);

        // Set click listener for navigation to McqActivityMain
        buttonNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to navigate to McqActivityMain
                Intent intent = new Intent(FlashcardActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });
        // Set button click listeners for navigation
        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex > 0) {
                    currentIndex--;
                    updateCardText(currentIndex);
                } else {
                    Toast.makeText(getApplicationContext(), "No previous words!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex < words.size() - 1) {
                    currentIndex++;
                    updateCardText(currentIndex);
                } else {
                    Toast.makeText(getApplicationContext(), "No more words!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set click listener for card flip
        findViewById(R.id.card_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipCard();
            }
        });
    }

    // Update the words on the card
    private void updateCardText(int index) {
        Map<String, String> currentWord = words.get(index);
        textFront.setText(currentWord.get("English Word"));
        textBack.setText(currentWord.get("French Translation"));
    }

    // Flip card animation logic
    private void flipCard() {
        AnimatorSet setOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.card_flip_out);
        AnimatorSet setIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.card_flip_in);

        if (isFront) {
            setOut.setTarget(card_front);
            setIn.setTarget(card_back);

            setOut.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}

                @Override
                public void onAnimationEnd(Animator animation) {
                    card_front.setVisibility(View.GONE);  // Hide the front card after flip
                    card_back.setVisibility(View.VISIBLE);  // Show the back card
                    setIn.start();  // Start the back flip-in animation
                }

                @Override
                public void onAnimationCancel(Animator animation) {}

                @Override
                public void onAnimationRepeat(Animator animation) {}
            });

            setOut.start();
        } else {
            setOut.setTarget(card_back);
            setIn.setTarget(card_front);

            setOut.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}

                @Override
                public void onAnimationEnd(Animator animation) {
                    card_back.setVisibility(View.GONE);  // Hide the back card after flip
                    card_front.setVisibility(View.VISIBLE);  // Show the front card
                    setIn.start();  // Start the front flip-in animation
                }

                @Override
                public void onAnimationCancel(Animator animation) {}

                @Override
                public void onAnimationRepeat(Animator animation) {}
            });

            setOut.start();
        }

        isFront = !isFront;  // Toggle the flag
    }


}
