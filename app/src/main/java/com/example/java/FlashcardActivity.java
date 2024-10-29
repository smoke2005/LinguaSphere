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
import java.util.List;
import java.util.Map;

// Flashcard class to encapsulate word and translation data
class Flashcard {
    private String word;
    private String translation;

    public Flashcard(String word, String translation) {
        this.word = word;
        this.translation = translation;
    }

    public String getWord() {
        return word;
    }

    public String getTranslation() {
        return translation;
    }
}

// FlashcardManager class to handle navigation through flashcards
class FlashcardManager {
    private List<Flashcard> flashcards;
    private int currentIndex = 0;

    public FlashcardManager(List<Flashcard> flashcards) {
        this.flashcards = flashcards;
    }

    public Flashcard getCurrentFlashcard() {
        return flashcards.get(currentIndex);
    }

    public Flashcard getNextFlashcard() {
        if (currentIndex < flashcards.size() - 1) {
            currentIndex++;
        }
        return getCurrentFlashcard();
    }

    public Flashcard getPreviousFlashcard() {
        if (currentIndex > 0) {
            currentIndex--;
        }
        return getCurrentFlashcard();
    }

    public boolean hasNextFlashcard() {
        return currentIndex < flashcards.size() - 1;
    }

    public boolean hasPreviousFlashcard() {
        return currentIndex > 0;
    }
}

// CardFlipAnimation class to encapsulate animation logic
class CardFlipAnimation {
    private AppCompatActivity activity;
    private boolean isFront = true;

    public CardFlipAnimation(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void flipCard(final CardView cardFront, final CardView cardBack) {
        AnimatorSet setOut = (AnimatorSet) AnimatorInflater.loadAnimator(activity, R.animator.card_flip_out);
        AnimatorSet setIn = (AnimatorSet) AnimatorInflater.loadAnimator(activity, R.animator.card_flip_in);

        if (isFront) {
            setOut.setTarget(cardFront);
            setIn.setTarget(cardBack);
            setOut.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}

                @Override
                public void onAnimationEnd(Animator animation) {
                    cardFront.setVisibility(View.GONE);
                    cardBack.setVisibility(View.VISIBLE);
                    setIn.start();
                }

                @Override
                public void onAnimationCancel(Animator animation) {}

                @Override
                public void onAnimationRepeat(Animator animation) {}
            });
            setOut.start();
        } else {
            setOut.setTarget(cardBack);
            setIn.setTarget(cardFront);
            setOut.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}

                @Override
                public void onAnimationEnd(Animator animation) {
                    cardBack.setVisibility(View.GONE);
                    cardFront.setVisibility(View.VISIBLE);
                    setIn.start();
                }

                @Override
                public void onAnimationCancel(Animator animation) {}

                @Override
                public void onAnimationRepeat(Animator animation) {}
            });
            setOut.start();
        }

        isFront = !isFront;
    }
}

// Main activity class
public class FlashcardActivity extends AppCompatActivity {
    private CardView cardFront, cardBack;
    private TextView textFront, textBack;
    private Button buttonPrevious, buttonNext;
    private FlashcardManager flashcardManager;
    private CardFlipAnimation cardFlipAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Initialize flashcards
        List<Flashcard> flashcards = new ArrayList<>();
        flashcards.add(new Flashcard("Hello", "Bonjour"));
        flashcards.add(new Flashcard("How are you?", "Comment ça va?"));
        flashcards.add(new Flashcard("Cat", "Chat"));
        flashcards.add(new Flashcard("Tree", "Arbre"));
        flashcards.add(new Flashcard("Book", "Livre"));

        flashcardManager = new FlashcardManager(flashcards);
        cardFlipAnimation = new CardFlipAnimation(this);

        cardFront = findViewById(R.id.card_front);
        cardBack = findViewById(R.id.card_back);
        textFront = findViewById(R.id.text_front);
        textBack = findViewById(R.id.text_back);
        buttonPrevious = findViewById(R.id.button_previous);
        buttonNext = findViewById(R.id.button_next);

        updateCardText();

        Button buttonNavigate = findViewById(R.id.button_navigate);
        buttonNavigate.setOnClickListener(v -> {
            Intent intent = new Intent(FlashcardActivity.this, MenuActivity.class);
            startActivity(intent);
        });

        buttonPrevious.setOnClickListener(v -> {
            if (flashcardManager.hasPreviousFlashcard()) {
                flashcardManager.getPreviousFlashcard();
                updateCardText();
            } else {
                Toast.makeText(getApplicationContext(), "No previous words!", Toast.LENGTH_SHORT).show();
            }
        });

        buttonNext.setOnClickListener(v -> {
            if (flashcardManager.hasNextFlashcard()) {
                flashcardManager.getNextFlashcard();
                updateCardText();
            } else {
                Toast.makeText(getApplicationContext(), "No more words!", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.card_main).setOnClickListener(v -> cardFlipAnimation.flipCard(cardFront, cardBack));
    }

    private void updateCardText() {
        Flashcard currentFlashcard = flashcardManager.getCurrentFlashcard();
        textFront.setText(currentFlashcard.getWord());
        textBack.setText(currentFlashcard.getTranslation());
    }
}
