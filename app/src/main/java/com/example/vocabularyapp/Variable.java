package com.example.vocabularyapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "variable")
public class Variable {

    @PrimaryKey(autoGenerate=true)
    private int id;
    private String word_pl;
    private String word_eng;
    private String category;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord_pl() {
        return word_pl;
    }

    public void setWord_pl(String word_pl) {
        this.word_pl = word_pl;
    }

    public String getWord_eng() {
        return word_eng;
    }

    public void setWord_eng(String word_eng) {
        this.word_eng = word_eng;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
