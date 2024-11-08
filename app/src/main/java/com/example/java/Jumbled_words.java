package com.example.java;

import android.content.Intent;
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

public class Jumbled_words extends AppCompatActivity {

    private TextView englishSentenceTextView;
    private TextView germanSentenceTextView;
    private List<String> selectedWords;
    private List<TextView> wordTextViews;
    private Button checkButton, nextButton, addScoreButton;
    private GridLayout gridLayout;

    private int currentQuestionIndex = 0;
    private int score = 0;

    private final String[][] questions = {
            {"Ich mag Deutsch lernen und genieße die Sprache.", "I like to learn French and enjoy the language"},
            {"Der Hund läuft schnell durch den Park mit Freude.", "The dog runs quickly through the park with joy"},
            {"Sie kaufte Blumen und Schokolade für ihre beste Freundin.", "She bought flowers and chocolate for her best friend"},
            {"Wir reisen gerne im Sommer zu verschiedenen Städten.", "We like traveling to different cities in the summer"}
    };

    private final List<List<String>> wordSets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jumbled_words);

        englishSentenceTextView = findViewById(R.id.englishSentenceTextView);
        germanSentenceTextView = findViewById(R.id.germanSentenceTextView);
        gridLayout = findViewById(R.id.gridLayout);
        checkButton = findViewById(R.id.checkButton);
        nextButton = findViewById(R.id.nextButton);
        addScoreButton = findViewById(R.id.addScoreButton);  // Find Add Score button

        selectedWords = new ArrayList<>();
        wordTextViews = new ArrayList<>();


        wordSets.add(List.of("I", "like", "to", "learn", "French", "and", "enjoy", "the", "language"));
        wordSets.add(List.of("The", "dog", "runs", "quickly", "through", "the", "park", "with", "joy"));
        wordSets.add(List.of("She", "bought", "flowers", "and", "chocolate", "for", "her", "best", "friend"));
        wordSets.add(List.of("We", "like", "traveling", "to", "different", "cities", "in", "the", "summer"));


        addScoreButton.setText("Score: " + score);


        loadQuestion();
        Button buttonNavigate = findViewById(R.id.menuButton);
        buttonNavigate.setOnClickListener(v -> {
            // Create an intent to navigate to MenuMain
            Intent intent = new Intent(Jumbled_words.this, MenuActivity.class);
            startActivity(intent);
        });


        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkTranslation();
            }
        });


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToNextQuestion();
            }
        });
    }

    private void loadQuestion() {

        selectedWords.clear();
        wordTextViews.clear();
        gridLayout.removeAllViews();
        englishSentenceTextView.setText("");


        germanSentenceTextView.setText(questions[currentQuestionIndex][0]);


        List<String> words = new ArrayList<>(wordSets.get(currentQuestionIndex));
        Collections.shuffle(words);


        for (final String word : words) {
            final TextView wordTextView = new TextView(this);
            wordTextView.setText(word);
            wordTextView.setTextSize(18);
            wordTextView.setPadding(16, 16, 16, 16);
            wordTextView.setBackgroundResource(R.drawable.word_box_background);  // Use your own box style
            wordTextView.setGravity(View.TEXT_ALIGNMENT_CENTER);

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

    private void updateEnglishSentence() {
        SpannableStringBuilder sentenceBuilder = new SpannableStringBuilder();


        int color = getResources().getColor(R.color.jump_color);

        for (String word : selectedWords) {
            int start = sentenceBuilder.length();
            sentenceBuilder.append(word).append(" ");
            int end = sentenceBuilder.length();

            sentenceBuilder.setSpan(new RelativeSizeSpan(1.5f), start, end - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            sentenceBuilder.setSpan(new ForegroundColorSpan(color), start, end - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        englishSentenceTextView.setText(sentenceBuilder, TextView.BufferType.SPANNABLE);

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

    private void reEnableWordInGrid(String removedWord) {
        for (TextView wordTextView : wordTextViews) {
            if (wordTextView.getText().toString().equals(removedWord)) {
                wordTextView.setVisibility(View.VISIBLE);  // Re-enable and show the word
                break;
            }
        }
    }

    private void checkTranslation() {
        String formedSentence = englishSentenceTextView.getText().toString().trim();
        String correctSentence = questions[currentQuestionIndex][1];
        if (formedSentence.equalsIgnoreCase(correctSentence)) {
            Toast.makeText(this, "Correct! You've translated the sentence.", Toast.LENGTH_LONG).show();
            score++;
            addScoreButton.setText("Score: " + score);
        } else {
            Toast.makeText(this, "Incorrect! Try again.", Toast.LENGTH_LONG).show();
        }
    }


    private void moveToNextQuestion() {
        if (currentQuestionIndex < questions.length - 1) {
            currentQuestionIndex++;
            loadQuestion();


            if (currentQuestionIndex == questions.length - 1) {
                nextButton.setVisibility(View.GONE);
            }
        }
    }
}
