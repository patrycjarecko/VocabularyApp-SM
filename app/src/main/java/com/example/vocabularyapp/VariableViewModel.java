package com.example.vocabularyapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class VariableViewModel extends AndroidViewModel {

    private VariableRepository variableRepository;
    private String word;
    private LiveData<List<Variable>> variables_eng;
    private LiveData<List<Variable>> variables_pl;

    public VariableViewModel(@NonNull Application application) {
        super(application);
        variableRepository = new VariableRepository(application);
        variables_eng = variableRepository.findAllEngVariables();
        variables_pl = variableRepository.findAllPlVariables();
    }


    LiveData<List<Variable>> findAllEngVariables(){ return variables_eng;}
    LiveData<List<Variable>> findAllPlVariables(){ return variables_pl; }


    public void insert(Variable variable) { variableRepository.insert(variable); }
    public void update(Variable variable) { variableRepository.update(variable); }
    public void delete(Variable variable) { variableRepository.delete(variable); }
    public void deleteAll() { variableRepository.deleteAll(); }


    LiveData<List<String>> findDisctinctCategories() {
        return variableRepository.findALlDistinctCategories();
    }

    LiveData<List<Variable>> findVariablesByCategory(String category) {
        return variableRepository.findVariablesByCategory(category);
    }
}
