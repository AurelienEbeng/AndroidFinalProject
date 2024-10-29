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

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import model.User;

public class EditLogin extends AppCompatActivity implements View.OnClickListener{

    TextView tvEditUserUserId;
    EditText etUserPassword, etEditUserPasswordConfirmation;
    Button btnEditUserBack, btnEditUserSave;

    DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initialize();
    }

    private void initialize(){
        tvEditUserUserId = findViewById(R.id.tvEditUserUserId);
        etUserPassword = findViewById(R.id.etUserPassword);
        etEditUserPasswordConfirmation = findViewById(R.id.etEditUserPasswordConfirmation);
        btnEditUserBack = findViewById(R.id.btnEditUserBack);
        btnEditUserSave = findViewById(R.id.btnEditUserSave);

        btnEditUserSave.setOnClickListener(this);
        btnEditUserBack.setOnClickListener(this);

        userDatabase = FirebaseDatabase.getInstance().getReference("User");

        displayId();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnEditUserSave) saveUserInfo(view);
        if (id == R.id.btnEditUserBack) goToMainMenuActivity();
    }

    private void goToMainMenuActivity() {
        Intent intent = new Intent(EditLogin.this, MainMenu.class);
        startActivity(intent);
        finish();
    }

    private void saveUserInfo(View view) {
        //Gets User data from EditText
        int userId = Integer.parseInt(tvEditUserUserId.getText().toString().trim());
        String newPassword = etUserPassword.getText().toString().trim();
        String confirmPassword = etEditUserPasswordConfirmation.getText().toString().trim();

        // Creates a message if condition is met which could lead to a Database error
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Password and Confirm Password is not the same",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if user exists in the database before changing the password
        userDatabase.child(String.valueOf(userId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Snackbar.make(view, "User with ID " + userId + " does not exist.", Snackbar.LENGTH_LONG).show();
                    return; // Exit if the user does not exist
                }

                // User exists, proceed to update the password
                User user = new User(userId, newPassword);
                userDatabase.child(String.valueOf(userId)).setValue(user).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Snackbar.make(view, "The User with ID " + userId + " has successfully changed password.", Snackbar.LENGTH_LONG).show();
                        clearWidgets();
                        Intent intent = new Intent(EditLogin.this, MainMenu.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Snackbar.make(view, "Failed to change password. Please try again.", Snackbar.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(view, "Error: " + error.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void displayId(){
        userDatabase = FirebaseDatabase.getInstance().getReference("User"); //

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        int currentUserId = sharedPreferences.getInt("userId",-1);

        if (currentUserId != -1){
            tvEditUserUserId.setText(String.valueOf(currentUserId));
        } else {
            // Gets last userId via firebase if the sharedPreferences does not work
            userDatabase.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                            int userId = Integer.parseInt(userSnapshot.getKey());
                            // Update the TextView with the retrieved user ID
                            tvEditUserUserId.setText(String.valueOf(userId));

                        }
                    } else {
                        tvEditUserUserId.setText("No users found.");
                    }
                }

                // Method in order to handle non existence Database.
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle possible errors
                    Log.e("DisplayId", "Error retrieving user ID: " + error.getMessage());
                    tvEditUserUserId.setText("Error fetching user ID."); // Update UI on error
                }
            });
        }
    }

    private void clearWidgets(){
        etUserPassword.setText(null);
        etEditUserPasswordConfirmation.setText(null);
        etUserPassword.requestFocus();
    }
}