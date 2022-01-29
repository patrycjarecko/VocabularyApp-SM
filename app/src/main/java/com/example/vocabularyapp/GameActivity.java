package com.example.vocabularyapp;

import static java.lang.Integer.parseInt;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import lombok.SneakyThrows;

public class GameActivity extends AppCompatActivity {

    VariableViewModel variableViewModel;
    List<String> categories = new ArrayList<>();
    List<Variable> variables = new ArrayList<>();
    List<String> possible_answers = new ArrayList<>();
    LinearLayout answerButtonsListView;
    private String chosenCategory;
    private Integer chosenMode = 1;
    private Integer chosenDifficlty = 1; //1-easy, 2-medium, 3-hard

    //translate mode
    TextView wordToTranslate;
    EditText translation;
    Button submitButton;
    Button backToCategoryButton;
    String requiredResult;

    //blankspaces mode
    List<Character> randomLetters = new ArrayList<>();
    String blank_spaces_word;

    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_list);
        chosenMode = getIntent().getExtras().getInt("chosenMode");

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        chosenDifficlty = parseInt(sharedPreferences.getString("difficulty", "1"));

        variableViewModel = ViewModelProviders.of(this).get(VariableViewModel.class);
        try {
            prepareCategories();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void prepareCategories() throws IOException {
        BufferedReader bufer = new BufferedReader(new InputStreamReader(getAssets().open("categories.txt")));
        String word;

        while ((word = bufer.readLine()) != null) {
            this.categories.add(word);
        }
        setCategories();
    }


    private void setCategories() {
        answerButtonsListView = findViewById(R.id.game_list_view);
        answerButtonsListView.removeAllViews();
        for (int i = 0; i < this.categories.size(); i++) {
            String tempCategory = this.categories.get(i);
            Button button = new Button(this);
            button.setId(i + 1);
            button.setText(tempCategory);
            button.setOnClickListener(view -> {
                this.chosenCategory = tempCategory;
                variableViewModel.findVariablesByCategory(this.chosenCategory).observe(this, this::setVariables);

                //if dt trybow
                if (this.chosenMode == 1) {
                    setContentView(R.layout.choose_answer_game);
                    startChooseAnswerGame();
                } else if (this.chosenMode == 2) {
                    setContentView(R.layout.translate_word_game);
                    startTranslateWordGame();
                } else {
                    //tutaj trzecia
                    setContentView(R.layout.blank_spaces_game);
                    startBlankSpacesGame();
                }
            });
            answerButtonsListView.addView(button);
        }
    }

    private void startTranslateWordGame() {
        wordToTranslate = findViewById(R.id.word_to_translate_text_view);
        translation = findViewById(R.id.put_translate_text_edit);
        submitButton = findViewById(R.id.game_submit_button);
        backToCategoryButton = findViewById(R.id.back_to_category_button);

        submitButton.setOnClickListener(view -> {
            String translate = translation.getText().toString();

            if (translate.equals(requiredResult)) {
                System.out.println("BRAWO");
                translation.setText("");
                startTranslateWordGame(); //potem to zmienic
            } else
                System.out.println("ŹLE");

        });

        backToCategoryButton.setOnClickListener(view -> {
            setContentView(R.layout.category_list);
            setCategories();
        });

        variableViewModel.findVariablesByCategory(this.chosenCategory).observe(this, this::setVariables);
    }


    private void startChooseAnswerGame() {
        wordToTranslate = findViewById(R.id.word_to_translate_text_view);
        variableViewModel.findVariablesByCategory(this.chosenCategory).observe(this, this::setVariables);
        variableViewModel.findVariablesByCategory(this.chosenCategory).observe(this, this::setPossibleAnswers);
    }


    private void setAnswerButtons() {
        answerButtonsListView = findViewById(R.id.answer_buttons);
        answerButtonsListView.removeAllViews();
        for (int i = 0; i <= chosenDifficlty; i++) {
            Button button = new Button(this);
            button.setId(i + 1);
            button.setText(this.possible_answers.get(i));
            button.setOnClickListener(view -> {
                if (button.getText().equals(requiredResult)) {
                    System.out.println("BRAWO");
                    startChooseAnswerGame(); //potem to zmienic
                } else
                    System.out.println("ŹLE");
            });
            answerButtonsListView.addView(button);
        }

        backToCategoryButton = findViewById(R.id.back_to_category_button);
        backToCategoryButton.setOnClickListener(view -> {
            setContentView(R.layout.category_list);
            setCategories();
        });

    }


    private synchronized void setVariables(List<Variable> variables) {
        if (variables.size() == 0) {
            setContentView(R.layout.category_list);
            setCategories();
            Toast.makeText(getApplicationContext(), "You don't have enough words in this category to start the game.", Toast.LENGTH_SHORT).show();
            return;
        }

        Integer pom = new Random().nextInt(variables.size());
        Variable variable = variables.get(pom);
        wordToTranslate.setText(variable.getWord_eng());
        requiredResult = variable.getWord_pl();
    }

    private void startBlankSpacesGame() {
        wordToTranslate = findViewById(R.id.word_to_translate_text_view);
        setBlankSpaces();

        variableViewModel.findVariablesByCategory(this.chosenCategory).observe(this, this::setVariables);
        variableViewModel.findVariablesByCategory(this.chosenCategory).observe(this, this::setBlankSpacesPossibleAnswers);

//        randomLetters;
//        blank_spaces_word;
    }

    private void setBlankSpacesPossibleAnswers(List<Variable> variables) {
        StringBuilder givenAnswer = new StringBuilder();
        List<Character> letters = getShuffledLetters();
        TextView blankSpacesWord = findViewById(R.id.blank_spaces_word);

        answerButtonsListView = findViewById(R.id.answer_buttons);
        answerButtonsListView.removeAllViews();

        for (int i = 0; i <= requiredResult.length() + (chosenDifficlty * 2); i++) {
            Button button = new Button(this);
            button.setId(i + 1);
            button.setText(letters.get(i) + "");
            button.setOnClickListener(view -> {
                givenAnswer.append(button.getText());
                blankSpacesWord.setText(givenAnswer.toString());
                if (givenAnswer.toString().equals(requiredResult)) {
                    System.out.println("BRAWO");
                    startBlankSpacesGame(); //potem to zmienic
                } else
                    System.out.println("ŹLE");
            });
            answerButtonsListView.addView(button);
        }

        backToCategoryButton = findViewById(R.id.back_to_category_button);
        backToCategoryButton.setOnClickListener(view -> {
            setContentView(R.layout.category_list);
            setCategories();
        });
    }

    private List<Character> getShuffledLetters() {
        Random randNum = new Random();
        char[] lettersArray = requiredResult.toCharArray();

        List<char[]> asList = Arrays.asList(lettersArray);

        List<Character> letters = new ArrayList<>();
        for (char letter : lettersArray) {
            letters.add(letter);
        }

        for (int i = 0; i < chosenDifficlty * 2; i++) {
            letters.add("abcdefghijklmnopqrstuvwxyz".toCharArray()[randNum.nextInt("abcdefghijklmnopqrstuvwxyz".toCharArray().length)]);
        }

        return letters;
    }

    private void setBlankSpaces() {


    }

    private void setPossibleAnswers(List<Variable> variables) {
        possible_answers.clear();

        if (variables.size() <= this.chosenDifficlty) {
            setContentView(R.layout.category_list);
            setCategories();
            Toast.makeText(getApplicationContext(), "You don't have enough words in this category to start the game.", Toast.LENGTH_SHORT).show();
            return;
        }

        //musi byc o jeden mniejsza zeby zmiescic poprawną
        while (this.possible_answers.size() < this.chosenDifficlty) {
            Variable variable = variables.get(new Random().nextInt(variables.size()));

            if (!Objects.equals(variable.getWord_pl(), this.requiredResult) && !ifContains(this.possible_answers, variable.getWord_pl())) {
                this.possible_answers.add(variable.getWord_pl());
            }
        }

        this.possible_answers.add(requiredResult);
        Collections.shuffle(this.possible_answers);
        setAnswerButtons();
    }


    private boolean ifContains(List<String> list, String new_value) {
        for (Integer i = 0; i < list.size(); i++) {
            if (list.get(i) == new_value) {
                return true;
            }
        }
        return false;
    }

    public void setChosenMode(int chosenMode) {
        this.chosenMode = chosenMode;
    }

}
