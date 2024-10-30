package com.example.projectfitnessapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import model.Person;

public class EditPerson extends AppCompatActivity implements View

        .OnClickListener, ValueEventListener {
    DatabaseReference personDatabase;

    SharedPreferences sharedPreferences;
    EditText etEditPersonName,etEditPersonAge,etEditPersonWeight,etEditPersonHeight;
    Button btnEditPersonSave, btnEditPersonReturn;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_person);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initialize();
    }

    private void initialize() {
        personDatabase = FirebaseDatabase.getInstance().getReference("Person");

        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId",-1);
        etEditPersonAge = findViewById(R.id.etEditPersonAge);
        etEditPersonHeight = findViewById(R.id.etEditPersonHeight);
        etEditPersonName = findViewById(R.id.etEditPersonName);
        etEditPersonWeight = findViewById(R.id.etEditPersonWeight);
        btnEditPersonReturn = findViewById(R.id.btnEditPersonReturn);
        btnEditPersonSave = findViewById(R.id.btnEditPersonSave);

        btnEditPersonSave.setOnClickListener(this);
        btnEditPersonReturn.setOnClickListener(this);

        getPersonFromDatabase(userId);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id == R.id.btnEditPersonReturn) goToDisplayPersonActivity();

        if(id == R.id.btnEditPersonSave) Update();
    }

    private void Update() {
        try{
            String name = etEditPersonName.getText().toString();
            String age = etEditPersonAge.getText().toString();
            String weight = etEditPersonWeight.getText().toString();
            String height = etEditPersonHeight.getText().toString();

            Person person = new Person(userId,name,Integer.parseInt(age),Float.parseFloat(weight),Float.parseFloat(height));
            personDatabase.child(String.valueOf(userId)).setValue(person);
            Toast.makeText(this, "The person with id "+userId+"has been updated", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void goToDisplayPersonActivity() {
        finish();
    }

    private void getPersonFromDatabase(int userId){
        personDatabase.child(String.valueOf(userId)).addValueEventListener(this);
    }
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        if(snapshot.exists()){
            String name = snapshot.child("name").getValue().toString();
            String age = snapshot.child("age").getValue().toString();
            String weight = snapshot.child("weight").getValue().toString();
            String height = snapshot.child("height").getValue().toString();
            etEditPersonName.setText(name);
            etEditPersonAge.setText(age);
            etEditPersonWeight.setText(weight);
            etEditPersonHeight.setText(height);
            Toast.makeText(this, userId, Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "No document", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}