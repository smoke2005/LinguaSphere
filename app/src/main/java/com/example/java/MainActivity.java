package com.example.javaproject;

import android.text.style.ForegroundColorSpan;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView englishSentenceTextView;
    private TextView germanSentenceTextView;
    private List<String> selectedWords;
    private List<TextView> wordTextViews;
    private Button checkButton, nextButton, addScoreButton;
    private GridLayout gridLayout;

    private int currentQuestionIndex = 0;
    private int score = 0;  // Score variable

    private final String[][] questions = {
            {"Ich mag Deutsch lernen und genieße die Sprache.", "I like to learn German and enjoy the language"},
            {"Der Hund läuft schnell durch den Park mit Freude.", "The dog runs quickly through the park with joy"},
            {"Sie kaufte Blumen und Schokolade für ihre beste Freundin.", "She bought flowers and chocolate for her best friend"},
            {"Wir reisen gerne im Sommer zu verschiedenen Städten.", "We like traveling to different cities in the summer"}
    };

    private final List<List<String>> wordSets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        englishSentenceTextView = findViewById(R.id.englishSentenceTextView);
        germanSentenceTextView = findViewById(R.id.germanSentenceTextView);
        gridLayout = findViewById(R.id.gridLayout);
        checkButton = findViewById(R.id.checkButton);
        nextButton = findViewById(R.id.nextButton);
        addScoreButton = findViewById(R.id.addScoreButton);  // Find Add Score button

        selectedWords = new ArrayList<>();
        wordTextViews = new ArrayList<>();

        // Initialize word sets for all questions
        wordSets.add(List.of("I", "like", "to", "learn", "German", "and", "enjoy", "the", "language"));
        wordSets.add(List.of("The", "dog", "runs", "quickly", "through", "the", "park", "with", "joy"));
        wordSets.add(List.of("She", "bought", "flowers", "and", "chocolate", "for", "her", "best", "friend"));
        wordSets.add(List.of("We", "like", "traveling", "to", "different", "cities", "in", "the", "summer"));

        // Set initial score to 0
        addScoreButton.setText("Score: " + score);

        // Load the first question
        loadQuestion();

        // Set up the check button
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkTranslation();
            }
        });

        // Set up the next button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToNextQuestion();
            }
        });
    }

    // Load the current question's German sentence and jumble the English words in the grid
    private void loadQuestion() {
        // Clear previous selections
        selectedWords.clear();
        wordTextViews.clear();
        gridLayout.removeAllViews();
        englishSentenceTextView.setText(""); // Clear the English sentence

        // Set the German sentence
        germanSentenceTextView.setText(questions[currentQuestionIndex][0]);

        // Get the words for the current question and shuffle them
        List<String> words = new ArrayList<>(wordSets.get(currentQuestionIndex));
        Collections.shuffle(words);

        // Create TextViews for each word and add them to the grid
        for (final String word : words) {
            final TextView wordTextView = new TextView(this);
            wordTextView.setText(word);
            wordTextView.setTextSize(18);
            wordTextView.setPadding(16, 16, 16, 16);
            wordTextView.setBackgroundResource(R.drawable.word_box_background);  // Use your own box style
            wordTextView.setGravity(View.TEXT_ALIGNMENT_CENTER);

            // Handle word click (add to sentence area and hide the word from grid)
            wordTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!selectedWords.contains(word)) {
                        selectedWords.add(word);
                        wordTextView.setVisibility(View.INVISIBLE);  // Hide the word in the grid
                        updateEnglishSentence();
                    }
                }
            });

            wordTextViews.add(wordTextView);
            gridLayout.addView(wordTextView);
        }
    }

    // Update the English sentence display as words are selected
    private void updateEnglishSentence() {
        SpannableStringBuilder sentenceBuilder = new SpannableStringBuilder();

        // Define your desired color
        int color = getResources().getColor(R.color.jump_color); // Replace with your color resource

        for (String word : selectedWords) {
            int start = sentenceBuilder.length();  // Get the starting position of the word
            sentenceBuilder.append(word).append(" ");
            int end = sentenceBuilder.length();  // Get the ending position of the word

            // Apply size increase to the word
            sentenceBuilder.setSpan(new RelativeSizeSpan(1.5f), start, end - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Apply color to the word
            sentenceBuilder.setSpan(new ForegroundColorSpan(color), start, end - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        englishSentenceTextView.setText(sentenceBuilder, TextView.BufferType.SPANNABLE);

        // Set click listener for the sentence (to return the word to the grid)
        englishSentenceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectedWords.isEmpty()) {
                    String removedWord = selectedWords.remove(selectedWords.size() - 1);
                    updateEnglishSentence();
                    reEnableWordInGrid(removedWord);
                }
            }
        });
    }

    // Method to return a word back to the grid when deselected
    private void reEnableWordInGrid(String removedWord) {
        for (TextView wordTextView : wordTextViews) {
            if (wordTextView.getText().toString().equals(removedWord)) {
                wordTextView.setVisibility(View.VISIBLE);  // Re-enable and show the word
                break;
            }
        }
    }

    // Method to check if the formed sentence is correct
    private void checkTranslation() {
        String formedSentence = englishSentenceTextView.getText().toString().trim();
        String correctSentence = questions[currentQuestionIndex][1];
        if (formedSentence.equalsIgnoreCase(correctSentence)) {
            Toast.makeText(this, "Correct! You've translated the sentence.", Toast.LENGTH_LONG).show();
            score++;  // Increase score on correct answer
            addScoreButton.setText("Score: " + score);  // Update score display
        } else {
            Toast.makeText(this, "Incorrect! Try again.", Toast.LENGTH_LONG).show();
        }
    }

    // Move to the next question
    private void moveToNextQuestion() {
        if (currentQuestionIndex < questions.length - 1) {
            currentQuestionIndex++;
            loadQuestion();

            // Hide the 'Next' button on the last question
            if (currentQuestionIndex == questions.length - 1) {
                nextButton.setVisibility(View.GONE);
            }
        }
    }
}

