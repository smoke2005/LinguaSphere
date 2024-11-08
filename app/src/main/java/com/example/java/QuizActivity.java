package com.example.java;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class QuizActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextToSpeech textToSpeech;
    private List<Word> words = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private TextView questionTextView;
    private LinearLayout optionsContainer;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questionTextView = findViewById(R.id.questionTextView);
        optionsContainer = findViewById(R.id.optionsContainer);
        resultTextView = findViewById(R.id.resultTextView);
        Button playAudioButton = findViewById(R.id.playAudioButton);

        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(this, this);

        // Upload words to Firebase (only run this if you need to initialize the data once)
        uploadWordsToFirebase();

        // Fetch words from Firebase
        fetchWordsFromFirebase();

        playAudioButton.setOnClickListener(v -> playAudio());
    }

    private void uploadWordsToFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("words");

        // Create Word objects for each word in the JSON
        Word word1 = new Word("Bonjour", Arrays.asList("Bienvenue", "Bonjour", "Au revoir", "Bonsoir"), "Bonjour");
        Word word2 = new Word("Au revoir ", Arrays.asList("Bienvenue", "Bonjour", "Au revoir", "Bonsoir"), "Au revoir");
        Word word3 = new Word("Bonsoir", Arrays.asList("Bienvenue", "Bonjour", "Au revoir", "Bonsoir"), "Bonsoir");

        // Store them in a Map with unique keys
        Map<String, Word> wordsMap = new HashMap<>();
        wordsMap.put("word1", word1);
        wordsMap.put("word2", word2);
        wordsMap.put("word3", word3);

        // Upload the map to Firebase under "words" node
        databaseReference.setValue(wordsMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Data uploaded successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("FirebaseError", "Data upload failed: " + task.getException().getMessage());
                    }
                });
    }

    private void fetchWordsFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("words");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String text = snapshot.child("text").getValue(String.class);
                    String correctAnswer = snapshot.child("correctAnswer").getValue(String.class);

                    List<String> options = new ArrayList<>();
                    for (DataSnapshot optionSnapshot : snapshot.child("options").getChildren()) {
                        options.add(optionSnapshot.getValue(String.class));
                    }
                    words.add(new Word(text, options, correctAnswer));
                }
                Collections.shuffle(words);  // Shuffle the words for randomness
                loadNextQuestion();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", databaseError.getMessage());
            }
        });
    }

    private void loadNextQuestion() {
        if (currentQuestionIndex < words.size()) {
            Word currentWord = words.get(currentQuestionIndex);
            questionTextView.setText("What is the audio word?");
            resultTextView.setText("");  // Clear previous result
            displayOptions(currentWord);
        } else {
            showCelebratoryPage();
        }
    }

    private void displayOptions(Word word) {
        optionsContainer.removeAllViews();  // Clear previous options
        for (String option : word.getOptions()) {
            Button optionButton = new Button(this);
            optionButton.setText(option);
            optionButton.setOnClickListener(v -> checkAnswer(option, word.getCorrectAnswer()));
            optionsContainer.addView(optionButton);
        }
    }

    private void checkAnswer(String selectedOption, String correctAnswer) {
        if (selectedOption.equals(correctAnswer)) {
            resultTextView.setText("Correct!");
        } else {
            resultTextView.setText("Wrong! Correct answer: " + correctAnswer);
        }
        currentQuestionIndex++;
        optionsContainer.postDelayed(this::loadNextQuestion, 2000);
    }

    private void playAudio() {
        if (currentQuestionIndex < words.size()) {
            Word currentWord = words.get(currentQuestionIndex);
            speakWord(currentWord.getText());
        }
    }

    private void speakWord(String word) {
        if (textToSpeech != null) {
            textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }



    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language not supported");
            }
        } else {
            Log.e("TTS", "Initialization failed");
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    private void showCelebratoryPage() {
        Toast.makeText(this, "Congratulations! You've completed the quiz!", Toast.LENGTH_LONG).show();
    }
}

class Word {
    private String text;
    private List<String> options;
    private String correctAnswer;

    public Word() {
        // Default constructor required for Firebase
    }

    public Word(String text, List<String> options, String correctAnswer) {
        this.text = text;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public String getText() {
        return text;
    }

    public List<String> getOptions() {
        return options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
}
