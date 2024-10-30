package com.example.projectfitnessapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import model.User;

public class MainMenu extends AppCompatActivity implements View.OnClickListener {

    private int userId;

    Button btnProfile, btnBMI, btnHistory, btnLogout;
    DatabaseReference personDatabase;

    SharedPreferences sharedPreferences;
    private int personId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainMenu), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initialize();
    }

    private void initialize(){
        btnProfile = findViewById(R.id.btnProfile);
        btnBMI = findViewById(R.id.btnBMI);
        btnHistory = findViewById(R.id.btnHistory);
        btnLogout = findViewById(R.id.btnLogout);

        btnProfile.setOnClickListener(this);
        btnBMI.setOnClickListener(this);
        btnHistory.setOnClickListener(this);
        btnLogout.setOnClickListener(this);

        personDatabase = FirebaseDatabase.getInstance().getReference("Person");

        // Keep the user Id throughout the app
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId",-1);
        personId = getIntent().getIntExtra("personId",-1);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnProfile) goToProfile(view);
        if (id == R.id.btnBMI) goToBMI(view);
        if (id == R.id.btnHistory) goToHistory(view);
        if (id == R.id.btnLogout) logout();
    }

    private void goToHistory(View view) {
    }

    private void goToBMI(View view) {
    }

    private void goToProfile(View view) {
        Intent intent = new Intent(MainMenu.this, DisplayPerson.class);
        intent.putExtra("personId",personId);
        startActivity(intent);
        finish();
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppsPrefs", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(MainMenu.this, LoginActivity.class);
        Toast.makeText(this, "User Logout" , Toast.LENGTH_SHORT).show();
        startActivity(intent);
        finish();
    }
}
