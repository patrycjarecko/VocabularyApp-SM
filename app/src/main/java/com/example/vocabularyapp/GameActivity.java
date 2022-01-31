package com.example.vocabularyapp;

import static java.lang.Integer.parseInt;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

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
    List<Character> letters = new ArrayList<>();
    LinearLayout answerButtonsListView;
    private String chosenCategory;
    private Integer chosenMode = 1;
    private Integer chosenDifficlty = 1; //1-easy, 2-medium, 3-hard

    static String WORD_ID_KEY = "word_id_key";
    static String WORD_CATEGORY_KEY = "word_category_key";
    static String CHOSEN_MODE_KEY = "chosen_mode_key";

    //translate mode
    TextView wordToTranslate;
    EditText translation;
    Button submitButton;
    Button backToCategoryButton;
    String requiredResult;
    private boolean isSavedInstanceState = false;

    //blankspaces mode
    List<Character> randomLetters = new ArrayList<>();
    String blank_spaces_word;
    Button resetButton;

    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_list);
        if (!isSavedInstanceState) chosenMode = getIntent().getExtras().getInt("chosenMode");

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
        if (!isSavedInstanceState) {
            for (int i = 0; i < this.categories.size(); i++) {
                String tempCategory = this.categories.get(i);
                Button button = new Button(this);
                button.setId(i + 1);
                button.setText(tempCategory);
                button.setOnClickListener(view -> {
                    this.chosenCategory = tempCategory;
                    variableViewModel.findVariablesByCategory(this.chosenCategory).observe(this, this::setVariables);

                    if (this.chosenMode == 1) {
                        setContentView(R.layout.choose_answer_game);
                        startChooseAnswerGame();
                    } else if (this.chosenMode == 2) {
                        setContentView(R.layout.translate_word_game);
                        startTranslateWordGame();
                    } else {
                        setContentView(R.layout.blank_spaces_game);
                        startBlankSpacesGame();
                    }
                });
                answerButtonsListView.addView(button);
            }
        } else {
            if (this.chosenMode == 1) {
                setContentView(R.layout.choose_answer_game);
                startChooseAnswerGame();
            } else if (this.chosenMode == 2) {
                setContentView(R.layout.translate_word_game);
                startTranslateWordGame();
            } else {
                setContentView(R.layout.blank_spaces_game);
                startBlankSpacesGame();
            }
        }

    }

    private void startTranslateWordGame() {
        if (!isSavedInstanceState) wordToTranslate = findViewById(R.id.word_to_translate_text_view);
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
        if (!isSavedInstanceState) wordToTranslate = findViewById(R.id.word_to_translate_text_view);
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
        if (!isSavedInstanceState) wordToTranslate = findViewById(R.id.word_to_translate_text_view);
        variableViewModel.findVariablesByCategory(this.chosenCategory).observe(this, this::setVariables);
        variableViewModel.findVariablesByCategory(this.chosenCategory).observe(this, this::getShuffledLetters);

        //getShuffledLetters();
//        randomLetters;
//        blank_spaces_word;
    }


    private void setBlankSpacesPossibleAnswers() {

        StringBuilder givenAnswer = new StringBuilder();
        TextView blankSpacesWord = findViewById(R.id.blank_spaces_word);

        TableLayout tabLayout = findViewById(R.id.tab_buttons);
        tabLayout.removeAllViews();
        int maxLen = requiredResult.length() + (chosenDifficlty * 2);
        for (int i = 1; i <= maxLen; ) {
            TableRow a = new TableRow(this);
            a.setId(i * 20 + 1);
            for (int y = 0; (y < 4) && (i <= maxLen); y++) {
                Button button = new Button(this);
                button.setId(i);
                button.setText(this.letters.get(i - 1) + "");
                button.setOnClickListener(view -> {
                    button.setEnabled(false);
                    givenAnswer.append(button.getText());
                    blankSpacesWord.setText(givenAnswer.toString());

                    if (givenAnswer.toString().equals(requiredResult)) {
                        System.out.println("BRAWO");

                        givenAnswer.setLength(0);
                        blankSpacesWord.setText("");
                        startBlankSpacesGame(); //potem to zmienic
                    } else
                        System.out.println("ŹLE");
                });
                a.addView(button);
                i++;
            }
            tabLayout.addView(a);
        }

        resetButton = findViewById(R.id.reset_button);
        resetButton.setOnClickListener(view -> {
            blankSpacesWord.setText("");
            givenAnswer.setLength(0);

            for (int i = 0; i < maxLen; i++) {
                Button button = findViewById(i + 1);
                button.setEnabled(true);
            }
        });

        backToCategoryButton = findViewById(R.id.back_to_category_button);
        backToCategoryButton.setOnClickListener(view -> {
            setContentView(R.layout.category_list);
            setCategories();
        });
    }

    private synchronized void getShuffledLetters(List<Variable> variables) {
        Random randNum = new Random();
        System.out.println("tu jest wymaganyt result" + requiredResult);
        char[] lettersArray = requiredResult.toCharArray();

        List<char[]> asList = Arrays.asList(lettersArray);

        this.letters.clear();
        for (char letter : lettersArray) {
            this.letters.add(letter);
        }

        for (int i = 0; i < chosenDifficlty * 2; i++) {
            this.letters.add("abcdefghijklmnopqrstuvwxyz".toCharArray()[randNum.nextInt("abcdefghijklmnopqrstuvwxyz".toCharArray().length)]);
        }

        Collections.shuffle(this.letters);
        setBlankSpacesPossibleAnswers();
    }


    private void setPossibleAnswers(List<Variable> variables) {
        this.possible_answers.clear();

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

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(WORD_ID_KEY, wordToTranslate.getId());
        bundle.putString(WORD_CATEGORY_KEY, chosenCategory);
        bundle.putInt(CHOSEN_MODE_KEY, chosenMode);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            wordToTranslate = findViewById(savedInstanceState.getInt(WORD_ID_KEY));
            chosenCategory = savedInstanceState.getString(WORD_CATEGORY_KEY);
            chosenMode = savedInstanceState.getInt(CHOSEN_MODE_KEY);
            isSavedInstanceState = true;
        }
    }
}