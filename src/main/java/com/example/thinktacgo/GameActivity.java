package com.example.thinktacgo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameActivity extends AppCompatActivity {

    private static final int QUESTION_TIME_LIMIT = 15000;  // 15 seconds in milliseconds
    private TextView timerText;
    private Handler timerHandler;
    private Runnable timerRunnable;
    private boolean player1Turn = true;
    private int roundCount = 0;
    private boolean questionAnswered = false;
    private TextView questionText;
    private RadioGroup answerOptions;
    private RadioButton answer1, answer2, answer3, answer4;
    private AudioManager audioManager;
    private MediaPlayer mediaPlayer;
    private String category;
    private int questionIndex = 0;
    private List<String[]> questionsList = new ArrayList<>();
    private Map<String, String[][]> questionsMap = new HashMap<>();
    private Map<String, String[]> answersMap = new HashMap<>();
    private Button[][] buttons = new Button[3][3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        MyApplication.initializeGamePlayer2();

        timerText = findViewById(R.id.timer_text);
        timerHandler = new Handler();

        category = getIntent().getStringExtra("CATEGORY");
        TextView titleText = findViewById(R.id.title_text);
        titleText.setText("THINK-TAC-GO!");

        questionText = findViewById(R.id.question_text);
        answerOptions = findViewById(R.id.answer_options);
        answer1 = findViewById(R.id.answer1);
        answer2 = findViewById(R.id.answer2);
        answer3 = findViewById(R.id.answer3);
        answer4 = findViewById(R.id.answer4);

        initializeQuestionsAndAnswers();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "cell_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCellClick((Button) v);
                    }
                });
            }
        }

        loadNewQuestion();

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        ImageView mm = findViewById(R.id.mm);
        mm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVolumePopup();
            }
        });

        ImageView qm = findViewById(R.id.qm);
        qm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoPopup();
            }
        });
    }

    private void initializeQuestionsAndAnswers() {
        questionsMap.put("MATH-GALING!", new String[][]{
                {"What is the product of 9x7?", "56", "63", "64", "72"},
                {"What is the sum of 28+34?", "50", "58", "62", "72"},
                {"What is 93-47?", "36", "44", "46", "56"},
                {"If 84 is divided by 7, what is the quotient?", "10", "11", "12", "13"},
                {"What is 6x11?", "56", "62", "66", "72"},
                {"What is the sum of 45+29?", "64", "70", "74", "78"},
                {"What is 128-83?", "35", "45", "55", "65"},
                {"If 96 is divided by 8, what is the quotient?", "10", "11", "12", "13"}
        });
        answersMap.put("MATH-GALING!", new String[]{"63", "62", "36", "12", "66", "78", "35", "12"});

        questionsMap.put("WORD FUN!", new String[][]{
                {"Which sentence uses commas correctly?", "She bought apples, oranges and bananas.", "She bought, apples, oranges, and bananas.", "She bought apples, oranges, and bananas.", "She bought apples oranges, and bananas."},
                {"Who wrote 'Pride and Prejudice'?", "Charlotte Brontë", "Jane Austen", "Mary Shelley", "Emily Brontë"},
                {"Choose the correct synonym for 'happy':", "Sad", "Angry", "Elated", "Tired"},
                {"What is a haiku?", "A 14-line poem", "A 5-line poem with an AABBA rhyme scheme", "A 3-line poem with a 5-7-5 syllable structure", "A poem that tells a story"},
                {"What is the main idea of a passage?", "The details that support the topic", "The author's opinion on the topic", "The most important point or message of the passage", "A secondary theme or idea"},
                {"Which sentence is written in the passive voice?", "The cat chased the mouse.", "The mouse was chased by the cat.", "The cat is chasing the mouse.", "The mouse chases the cat."},
                {"Who is the author of the play 'Hamlet'?", "William Shakespeare", "Arthur Miller", "Tennessee Williams", "George Bernard Shaw"},
                {"Which sentence correctly uses a semicolon?", "I have a big test tomorrow; I can't go out tonight.", "I have a big test tomorrow, I can't go out tonight.", "I have a big test tomorrow; and I can't go out tonight.", "I have a big test tomorrow: I can't go out tonight."},
                {"Which of the following is a work of dystopian fiction?", "'1984' by George Orwell", "'Pride and Prejudice' by Jane Austen", "'The Great Gatsby' by F. Scott Fitzgerald", "'Moby Dick' by Herman Melville"},
                {"Which of these sentences is a complex sentence?", "I went to the store, and I bought some bread.", "I went to the store; I bought some bread.", "When I went to the store, I bought some bread.", "I went to the store and bought some bread."}
        });
        answersMap.put("WORD FUN!", new String[]{"She bought apples, oranges, and bananas.", "Jane Austen", "Elated", "A 3-line poem with a 5-7-5 syllable structure", "The most important point or message of the passage", "The mouse was chased by the cat.", "William Shakespeare", "I have a big test tomorrow; I can't go out tonight.", "'1984' by George Orwell", "When I went to the store, I bought some bread."});

        questionsMap.put("HISTORY ADVENTURE!", new String[][]{
                {"Who is the national hero of the Philippines?", "Andres Bonifacio", "Jose Rizal", "Emilio Aguinaldo", "Apolinario Mabini"},
                {"Which country colonized the Philippines for over 300 years?", "United States", "Japan", "Spain", "China"},
                {"What is the official language of the Philippines, alongside English?", "Cebuano", "Ilocano", "Tagalog", "Hiligaynon"},
                {"Which city is known as the oldest city in the Philippines?", "Manila", "Davao", "Cebu", "Vigan"},
                {"Who led the Philippine Revolution against Spanish rule?", "Ferdinand Magellan", "Jose Rizal", "Emilio Aguinaldo", "Manuel L. Quezon"},
                {"What is the date of Philippine Independence Day?", "June 12", "July 4", "August 21", "December 30"},
                {"What is the name of the Filipino who invented the moon buggy used in NASA’s Apollo missions?", "Eduardo San Juan", "Juan Luna", "Apolinario Mabini", "Gregorio del Pilar"},
                {"Which Filipino president declared Martial Law in 1972?", "Corazon Aquino", "Ferdinand Marcos", "Gloria Macapagal-Arroyo", "Rodrigo Duterte"}
        });
        answersMap.put("HISTORY ADVENTURE!", new String[]{"Jose Rizal", "Spain", "Tagalog", "Cebu", "Emilio Aguinaldo", "June 12", "Eduardo San Juan", "Ferdinand Marcos"});

        questionsList = new ArrayList<>(Arrays.asList(questionsMap.get(category)));
        Collections.shuffle(questionsList);
    }

    private void onCellClick(Button button) {
        if (!questionAnswered) {
            Toast.makeText(this, "Answer the question first!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!button.getText().toString().equals("")) {
            return;
        }

        if (player1Turn) {
            button.setText("X");
        } else {
            button.setText("O");
        }

        roundCount++;

        if (checkForWin()) {
            if (player1Turn) {
                player1Wins();
            } else {
                player2Wins();
            }
        } else if (roundCount == 9) {
            draw();
        } else {
            player1Turn = !player1Turn;
            questionAnswered = false;
            loadNewQuestion();
        }
    }

    private boolean checkForWin() {
        String[][] field = new String[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = buttons[i][j].getText().toString();
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals(field[i][1])
                    && field[i][0].equals(field[i][2])
                    && !field[i][0].equals("")) {
                return true;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[0][i].equals(field[1][i])
                    && field[0][i].equals(field[2][i])
                    && !field[0][i].equals("")) {
                return true;
            }
        }

        if (field[0][0].equals(field[1][1])
                && field[0][0].equals(field[2][2])
                && !field[0][0].equals("")) {
            return true;
        }

        if (field[0][2].equals(field[1][1])
                && field[0][2].equals(field[2][0])
                && !field[0][2].equals("")) {
            return true;
        }

        return false;
    }

    private void player1Wins() {
        Toast.makeText(this, "Player 1 wins!", Toast.LENGTH_SHORT).show();
        showGameOverDialog("Player 1 wins!");
    }

    private void player2Wins() {
        Toast.makeText(this, "Player 2 wins!", Toast.LENGTH_SHORT).show();
        showGameOverDialog("Player 2 wins!");
    }

    private void draw() {
        Toast.makeText(this, "Draw!", Toast.LENGTH_SHORT).show();
        showGameOverDialog("Draw!");
        resetBoard();
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
            }
        }

        roundCount = 0;
        player1Turn = true;
        questionIndex = 0;
        questionAnswered = false;
        loadNewQuestion();
    }

    private void loadNewQuestion() {
        if (questionIndex >= questionsList.size()) {
            questionIndex = 0;
            Collections.shuffle(questionsList);  // Reshuffle when all questions have been used
        }

        String[] questionData = questionsMap.get(category)[questionIndex];
        questionText.setText(questionData[0]);
        answer1.setText(questionData[1]);
        answer2.setText(questionData[2]);
        answer3.setText(questionData[3]);
        answer4.setText(questionData[4]);

        answerOptions.clearCheck();
        questionAnswered = false;  // Reset the question answered flag

        Button submitAnswerButton = findViewById(R.id.submit_answer_button);
        submitAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionAnswered) {
                    Toast.makeText(GameActivity.this, "Time's up! You can't answer this question anymore.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int checkedId = answerOptions.getCheckedRadioButtonId();

                if (checkedId == -1) {
                    Toast.makeText(GameActivity.this, "Please select an answer.", Toast.LENGTH_SHORT).show();
                    return;
                }

                RadioButton selectedAnswer = findViewById(checkedId);
                checkAnswer(selectedAnswer.getText().toString());
            }
        });

        questionIndex++;
        startQuestionTimer();
    }

    private void startQuestionTimer() {
        timerHandler.removeCallbacks(timerRunnable);  // Remove any existing callbacks
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (!questionAnswered) {
                    Toast.makeText(GameActivity.this, "Time's Up!", Toast.LENGTH_SHORT).show();
                    questionAnswered = true;  // Disallow answering after time's up
                }
            }
        };
        timerHandler.postDelayed(timerRunnable, QUESTION_TIME_LIMIT);  // Set the timer for 15 seconds

        // Update the timer display
        timerText.setText("15s");
        final long startTime = System.currentTimeMillis();
        timerHandler.post(new Runnable() {
            @Override
            public void run() {
                long elapsedTime = System.currentTimeMillis() - startTime;
                long remainingTime = QUESTION_TIME_LIMIT - elapsedTime;
                if (remainingTime > 0 && !questionAnswered) {
                    timerText.setText((remainingTime / 1000) + "s");
                    timerHandler.postDelayed(this, 1000);
                } else {
                    timerText.setText("0s");
                }
            }
        });
    }

    private void checkAnswer(String selectedAnswer) {
        String correctAnswer = answersMap.get(category)[questionIndex - 1];

        if (selectedAnswer.equals(correctAnswer)) {
            Toast.makeText(this, "Correct Answer!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show();
        }

        questionAnswered = true;
        timerHandler.removeCallbacks(timerRunnable);  // Stop the timer if the question is answered
    }


    private void showVolumePopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_volume, null);
        builder.setView(dialogView);

        SeekBar volumeSeekBar = dialogView.findViewById(R.id.volumeSeekBar);
        volumeSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        Button okButton = dialogView.findViewById(R.id.okButton);
        AlertDialog alertDialog = builder.create();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void showInfoPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(dialogView);

        Button okButton = dialogView.findViewById(R.id.okButton);
        AlertDialog alertDialog = builder.create();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void showGameOverDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("New Game", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(GameActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        resetBoard();
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.pauseGamePlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.playGamePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.stopGamePlayer();
    }
}
