package com.example.projectfitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import model.User;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener,
        ValueEventListener, ChildEventListener {

    TextView tvForgotPasswordPassword, tvForgotPasswordConfirm;
    EditText etForgotPasswordUserId, etForgotPasswordPassword, etForgotPasswordConfirm;
    Button btnForgotPasswordFindUserId, btnForgotPasswordChangePassword, btnForgotPasswordBack;

    DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initialize();
    }

    private void initialize(){
        tvForgotPasswordPassword = findViewById(R.id.tvForgotPasswordPassword);
        tvForgotPasswordConfirm = findViewById(R.id.tvForgotPasswordConfirm);
        etForgotPasswordPassword = findViewById(R.id.etForgotPasswordPassword);
        etForgotPasswordConfirm = findViewById(R.id.etForgotPasswordConfirm);
        etForgotPasswordUserId = findViewById(R.id.etForgotPasswordUserId);
        btnForgotPasswordBack = findViewById(R.id.btnForgotPasswordBack);
        btnForgotPasswordFindUserId = findViewById(R.id.btnForgotPasswordFindUserId);
        btnForgotPasswordChangePassword = findViewById(R.id.btnForgotPasswordChangePassword);

        btnForgotPasswordBack.setOnClickListener(this);
        btnForgotPasswordFindUserId.setOnClickListener(this);
        btnForgotPasswordChangePassword.setOnClickListener(this);

        userDatabase = FirebaseDatabase.getInstance().getReference("User");
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnForgotPasswordBack) goToLoginActivity();
        if (id == R.id.btnForgotPasswordFindUserId) findUserId(view);
        if (id == R.id.btnForgotPasswordChangePassword) changePassword(view);
    }

    private void changePassword(View view) {
        String userIdField = etForgotPasswordUserId.getText().toString().trim();
        String newPassword = etForgotPasswordPassword.getText().toString().trim();
        String confirmPassword = etForgotPasswordConfirm.getText().toString().trim();
        if (userIdField.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()){
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        } else if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Password and Confirm Password is not the same",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int userId;

        try {
            userId = Integer.parseInt(userIdField);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Intent intent = new Intent(ForgotPassword.this, LoginActivity.class);
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
    private void findUserId(View view) {
        try {
            String userId = etForgotPasswordUserId.getText().toString().trim();

            if (userId.isEmpty()) {
                Snackbar.make(view, "User Id field must not be empty.", Snackbar.LENGTH_LONG).show();
                return;
            }

            userDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Snackbar.make(view, "Input ur new Password", Snackbar.LENGTH_LONG).show();

                        tvForgotPasswordConfirm.setVisibility(View.VISIBLE);
                        tvForgotPasswordPassword.setVisibility(View.VISIBLE);
                        etForgotPasswordPassword.setVisibility(View.VISIBLE);
                        etForgotPasswordConfirm.setVisibility(View.VISIBLE);
                        btnForgotPasswordChangePassword.setVisibility(View.VISIBLE);
                    } else {
                        Snackbar.make(view, "UserId does not exist", Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Snackbar.make(view, "Error : " + error.getMessage(),Snackbar.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Snackbar.make(view, "Error : " + e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(ForgotPassword.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void clearWidgets(){
        etForgotPasswordUserId.setText(null);
        etForgotPasswordConfirm.setText(null);
        etForgotPasswordPassword.setText(null);
        etForgotPasswordConfirm.setVisibility(View.GONE);
        etForgotPasswordPassword.setVisibility(View.GONE);
        tvForgotPasswordPassword.setVisibility(View.GONE);
        tvForgotPasswordConfirm.setVisibility(View.GONE);
        btnForgotPasswordChangePassword.setVisibility(View.GONE);
        etForgotPasswordUserId.requestFocus();
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        if (snapshot.exists()) {
            String userId = snapshot.child("userId").getValue().toString();
            String password = snapshot.child("password").getValue().toString();

            etForgotPasswordUserId.setText(userId);
            etForgotPasswordPassword.setText(password);
        } else {
            Toast.makeText(this, "Document does not exist", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}