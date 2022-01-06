package com.example.vocabularyapp;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class VariableRepository {

    private VariableDao variableDao;
    private String word;
    private LiveData<List<Variable>> variables;
    private LiveData<List<Variable>> variables_pl;
    private LiveData<List<Variable>> variables_eng;

    VariableRepository(Application application) {
        VariableDatabase database = VariableDatabase.getDatabase(application);
        variableDao = database.variableDao();
        //uwzglednic potem w druga strone
        variables_eng = variableDao.findAllEng();
        variables_pl = variableDao.findAllPl();
    }


    LiveData<List<Variable>> findAllEngVariables(){ return variables_eng; }
    LiveData<List<Variable>> findAllPlVariables(){ return variables_pl; }


    void insert(Variable variable){
        VariableDatabase.databaseWriteExecutor.execute(() ->{
            variableDao.insert(variable);
        });
    }

    void update(Variable variable){
        VariableDatabase.databaseWriteExecutor.execute(() ->{
            variableDao.update(variable);
        });
    }

    void delete(Variable variable){
        VariableDatabase.databaseWriteExecutor.execute(() ->{
            variableDao.delete(variable);
        });
    }

    void deleteAll(){
        VariableDatabase.databaseWriteExecutor.execute(() ->{
            variableDao.deleteAll();
        });
    }

}