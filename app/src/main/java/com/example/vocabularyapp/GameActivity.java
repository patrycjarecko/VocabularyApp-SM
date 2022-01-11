package com.example.vocabularyapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lombok.SneakyThrows;

public class GameActivity extends AppCompatActivity {

    VariableViewModel variableViewModel;
    List<String> categories = new ArrayList<>();
    LinearLayout gameListView;
    private String chosenCategory;

    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_list);

        variableViewModel = ViewModelProviders.of(this).get(VariableViewModel.class);
        prepareCategories();

       }

    private void prepareCategories() throws IOException {
        BufferedReader bufer = new BufferedReader(new InputStreamReader(getAssets().open("categories.txt")));
        String word;

        while ((word = bufer.readLine()) != null){
            this.categories.add(word);
        }
        setCategories();

    }

    private void setCategories() {
        gameListView = findViewById(R.id.game_list_view);
        gameListView.removeAllViews();
        for (int i = 0; i < this.categories.size(); i++) {
            String tempCategory = this.categories.get(i);
            Button button = new Button(this);
            button.setId(i + 1);
            button.setText(tempCategory);
            button.setOnClickListener(view -> {

                this.chosenCategory = tempCategory;
                //if dt trybow
                setContentView(R.layout.translate_word_game);
                startTranslateWordGame();

            });
            gameListView.addView(button);
        }
    }

    TextView wordToTranslate;
    EditText translation;
    Button submitButton;
    Button backToCategoryButton;
    String requiredResult;

    private void startTranslateWordGame(){
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

    private void setVariables(List<Variable> variables) {
        Variable variable = variables.get(new Random().nextInt(variables.size()));
        wordToTranslate.setText(variable.getWord_eng());
        requiredResult = variable.getWord_pl();
    }
}