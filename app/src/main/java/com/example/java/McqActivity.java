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

// Question class to represent a single question
class Question {
    private String questionText;
    private String[] options;
    private String correctAnswer;

    public Question(String questionText, String option1, String option2, String option3, String option4, String correctAnswer) {
        this.questionText = questionText;
        this.options = new String[]{option1, option2, option3, option4};
        this.correctAnswer = correctAnswer;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String[] getOptions() {
        return options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
}

public class McqActivity extends AppCompatActivity {

    private TextView questionText;
    private RadioButton answer1, answer2, answer3, answer4;
    private FirebaseFirestore db;
    private Button nextButton, prevButton, submitButton, scoreButton;
    private RadioGroup answerGroup;

    private List<Question> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.mcq_activity);

        initializeUIElements();
        db = FirebaseFirestore.getInstance();
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

        Button buttonNavigate = findViewById(R.id.navigate);
        buttonNavigate.setOnClickListener(v -> {
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
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                String question = (String) document.getData().get("Question");
                                String option1 = (String) document.getData().get("Option 1");
                                String option2 = (String) document.getData().get("Option 2");
                                String option3 = (String) document.getData().get("Option 3");
                                String option4 = (String) document.getData().get("Option 4");
                                String correctAnswer = (String) document.getData().get("Correct Answer");
                                questionList.add(new Question(question, option1, option2, option3, option4, correctAnswer));
                            }
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
            Question currentQuestion = questionList.get(index);
            questionText.setText(currentQuestion.getQuestionText());
            String[] options = currentQuestion.getOptions();
            answer1.setText(options[0]);
            answer2.setText(options[1]);
            answer3.setText(options[2]);
            answer4.setText(options[3]);

            resetAnswerColors();
            answerGroup.clearCheck();
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
        String correctAnswer = questionList.get(currentQuestionIndex).getCorrectAnswer();

        if (selectedAnswer.equals(correctAnswer)) {
            selectedRadioButton.setTextColor(Color.GREEN);
            score++;
            scoreButton.setText(String.format("Score: %d", score));
        } else {
            selectedRadioButton.setTextColor(Color.RED);  // Incorrect answer
            Toast.makeText(this, "Wrong answer!", Toast.LENGTH_SHORT).show();
        }

        highlightCorrectAnswer(correctAnswer);
    }

    private void highlightCorrectAnswer(String correctAnswer) {
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
