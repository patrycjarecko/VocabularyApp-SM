package com.example.vocabularyapp;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Variable.class}, version=1, exportSchema = false)
public abstract class VariableDatabase extends RoomDatabase {


    public abstract VariableDao variableDao();
    private static volatile VariableDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 8;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static VariableDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (VariableDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), VariableDatabase.class, "variable_db").addCallback(sRoomDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;

    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            databaseWriteExecutor.execute(() -> {
                VariableDao dao = INSTANCE.variableDao();
                dao.deleteAll();

                Variable variable = new Variable();
                variable.setWord_pl("jabłko");
                variable.setWord_eng("apple");
                variable.setCategory("owoc");
                dao.insert(variable);
                Variable variable1 = new Variable();
                variable1.setWord_pl("pomidor");
                variable1.setWord_eng("tomato");
                variable1.setCategory("warzywo");
                dao.insert(variable1);
            });
        }
    };
}
