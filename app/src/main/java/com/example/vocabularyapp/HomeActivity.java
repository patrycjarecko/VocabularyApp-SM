package com.example.vocabularyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Button quizModeButton;
    Button writeWordModeButton;
    Button combineLettersModeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.home_view);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.home_toolbar);

        quizModeButton = findViewById(R.id.button_quiz);
        writeWordModeButton = findViewById(R.id.button_write);
        combineLettersModeButton = findViewById(R.id.button_combine);

        quizModeButton.setText("Quiz");
        writeWordModeButton.setText("Write");
        combineLettersModeButton.setText("Combine");

        quizModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, GameActivity.class);
                intent.putExtra("chosenMode", 1);
                startActivity(intent);
            }
        });

        writeWordModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, GameActivity.class);
                intent.putExtra("chosenMode", 2);
                startActivity(intent);
            }
        });

        combineLettersModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, GameActivity.class);
                intent.putExtra("chosenMode", 3);
                startActivity(intent);
            }
        });

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.nav_home:
                break;

            case R.id.nav_favorite:
                break;

            case R.id.nav_word_list:
                intent = new Intent(HomeActivity.this, VariableActivity.class);
                startActivity(intent);
                break;

            case R.id.statistic:
                break;

            case R.id.settings:
                intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_tutorial:
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}