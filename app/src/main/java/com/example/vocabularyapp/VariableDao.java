package com.example.vocabularyapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface VariableDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Variable variable);


    @Update
    public void update(Variable variable);

    @Delete
    public void delete(Variable variable);

    @Query("DELETE FROM variable")
    public void deleteAll();

    @Query("SELECT * FROM variable ORDER BY word_eng")
    public LiveData<List<Variable>> findAllEng();

    @Query("SELECT * FROM variable WHERE word_eng LIKE:word_eng")
    public List<Variable> findVariableByWordEng(String word_eng);

    @Query("SELECT * FROM variable ORDER BY word_pl")
    public LiveData<List<Variable>> findAllPl();

    @Query("SELECT * FROM variable WHERE word_pl LIKE:word_pl")
    public List<Variable> findVariableByWordPl(String word_pl);

    @Query("SELECT * FROM variable WHERE category LIKE:category")
    public LiveData<List<Variable>> findVariableByCategory(String category);

    @Query("SELECT category from variable")
    LiveData<List<String>> findAllDistinctCategories();

    //by status

}
