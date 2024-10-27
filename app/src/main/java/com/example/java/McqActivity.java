package com.example.java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class McqActivity extends AppCompatActivity {

    private TextView questionText;
    private RadioButton answer1, answer2, answer3, answer4;
    private FirebaseFirestore db;
    private Button nextButton, prevButton, submitButton,scoreButton;
    private RadioGroup answerGroup;

    // List to store all questions
    private List<Map<String, Object>> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;
    private String correctAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.mcq_activity);  // Ensure your layout name matches

        // Initialize UI elements
        initializeUIElements();

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Load all questions from Firestore collection "Mcq"
        loadQuestionsFromFirestore();
    }

    private void initializeUIElements() {
        questionText = findViewById(R.id.question_text);
        answer1 = findViewById(R.id.answer1);
        answer2 = findViewById(R.id.answer2);
        answer3 = findViewById(R.id.answer3);
        answer4 = findViewById(R.id.answer4);
        prevButton = findViewById(R.id.button_previous);
        nextButton = findViewById(R.id.button_next);
        submitButton = findViewById(R.id.check_button);
        answerGroup = findViewById(R.id.answers_group);
        scoreButton = findViewById(R.id.score_icon);

        // Set click listeners
        nextButton.setOnClickListener(v -> loadNextQuestion());
        prevButton.setOnClickListener(v -> loadPreviousQuestion());
        submitButton.setOnClickListener(v -> checkAnswer());

        // Set click listener for navigation to MenuMain
        Button buttonNavigate = findViewById(R.id.navigate);
        buttonNavigate.setOnClickListener(v -> {
            // Create an intent to navigate to MenuMain
            Intent intent = new Intent(McqActivity.this, MenuActivity.class);
            startActivity(intent);
        });
    }

    private void loadQuestionsFromFirestore() {
        db.collection("Mcq")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Iterate through all documents (questions)
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                // Add each question to the list
                                questionList.add(document.getData());
                            }
                            // Display the first question
                            displayQuestion(currentQuestionIndex);
                        } else {
                            Toast.makeText(McqActivity.this, "No questions found!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(McqActivity.this, "Failed to load questions.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayQuestion(int index) {
        if (index >= 0 && index < questionList.size()) {
            Map<String, Object> questionData = questionList.get(index);

            // Safely retrieve the question and options
            String question = (String) questionData.get("Question");
            String option1 = (String) questionData.get("Option 1");
            String option2 = (String) questionData.get("Option 2");
            String option3 = (String) questionData.get("Option 3");
            String option4 = (String) questionData.get("Option 4");
            correctAnswer = (String) questionData.get("Correct Answer");

            // Set the text for question and options in the UI
            questionText.setText(question);
            answer1.setText(option1);
            answer2.setText(option2);
            answer3.setText(option3);
            answer4.setText(option4);

            // Reset text color for answers
            resetAnswerColors();

            answerGroup.clearCheck(); // Clear any previously selected answer
        } else {
            Toast.makeText(this, "No more questions!", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetAnswerColors() {
        answer1.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        answer2.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        answer3.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        answer4.setTextColor(ContextCompat.getColor(this, android.R.color.white));
    }

    private void checkAnswer() {
        int selectedId = answerGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select an answer!", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadioButton = findViewById(selectedId);
        String selectedAnswer = selectedRadioButton.getText().toString();

        // Highlight the selected answer and correct answer
        if (selectedAnswer.equals(correctAnswer)) {
            selectedRadioButton.setTextColor(Color.GREEN);  // Correct answer
            score++;  // Increase score
            scoreButton.setText(String.format("Score: %d", score));

            //Toast.makeText(this, "Correct! Your score is: " + score, Toast.LENGTH_SHORT).show();
        } else {
            selectedRadioButton.setTextColor(Color.RED);  // Incorrect answer
            Toast.makeText(this, "Wrong answer!", Toast.LENGTH_SHORT).show();
        }

        // Highlight the correct answer
        highlightCorrectAnswer();
    }

    private void highlightCorrectAnswer() {
        if (answer1.getText().toString().equals(correctAnswer)) {
            answer1.setTextColor(Color.GREEN);
        } else if (answer2.getText().toString().equals(correctAnswer)) {
            answer2.setTextColor(Color.GREEN);
        } else if (answer3.getText().toString().equals(correctAnswer)) {
            answer3.setTextColor(Color.GREEN);
        } else if (answer4.getText().toString().equals(correctAnswer)) {
            answer4.setTextColor(Color.GREEN);
        }
    }

    private void loadNextQuestion() {
        if (currentQuestionIndex < questionList.size() - 1) {
            currentQuestionIndex++;
            displayQuestion(currentQuestionIndex);
        } else {
            Toast.makeText(this, "You have reached the end of the questions!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPreviousQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            displayQuestion(currentQuestionIndex);
        } else {
            Toast.makeText(this, "You are at the first question!", Toast.LENGTH_SHORT).show();
        }
    }
}
