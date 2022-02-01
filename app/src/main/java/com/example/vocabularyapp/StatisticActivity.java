package com.example.vocabularyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class StatisticActivity extends AppCompatActivity {
    private int amountOfWordsToLearn = 0;
    private int amountOfLearningWords = 0;
    private int amountOfLearntWords = 0;


    TextView amountOfWordsToLearnView;
    TextView amountOfLearningWordsView;
    TextView amountOfLearntWordsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);


        amountOfWordsToLearnView = findViewById(R.id.to_learn_value);
        amountOfLearningWordsView =  findViewById(R.id.to_learning_value);
        amountOfLearntWordsView = findViewById(R.id.learnt_value);

        countStatistics();
    }

    public void countStatistics() {
        List<String> statuses = HomeActivity.statuses;
        amountOfWordsToLearn = 0;
        amountOfLearningWords = 0;
        amountOfLearntWords = 0;

        for (String status : HomeActivity.statusForEachVariable) {
            if (status.equals("Do nauczenia")) amountOfWordsToLearn++;
            else if (status.equals("W trakcie nauki")) amountOfLearningWords++;
            else if (status.equals("Nauczone")) amountOfLearntWords++;
        }

        amountOfWordsToLearnView.setText(amountOfWordsToLearn + "");
        amountOfLearningWordsView.setText(amountOfLearningWords + "");
        amountOfLearntWordsView.setText(amountOfLearntWords + "");
    }
}