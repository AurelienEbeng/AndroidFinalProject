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

import java.util.EventListener;

import model.User;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener,
        ValueEventListener, ChildEventListener {

    TextView tvCreateAccount, tvForgotPassword;
    EditText etUserId, etPassword;
    Button btnLogin;

    DatabaseReference PersonDatabase, UserDatabase;

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

    private void initialize() {
        tvCreateAccount = findViewById(R.id.tvCreateAccount);
        tvForgotPassword = findViewById(R.id.tvCreateAccount);

        etUserId = findViewById(R.id.etUserId);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogIn);

        btnLogin.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
        tvCreateAccount.setOnClickListener(this);

        PersonDatabase = FirebaseDatabase.getInstance().getReference("Person");
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnLogIn) login(view);
        if (id == R.id.tvCreateAccount) goToCreatePage(view);
        if (id == R.id.tvForgotPassword) goToForgotPage(view);
    }

    private void goToForgotPage(View view) {
    }

    private void goToCreatePage(View view) {
    }

    private void login(View view) {
        try {
            int userId = Integer.parseInt(etUserId.getText().toString());
            String password = etPassword.getText().toString();

            DatabaseReference userRef = PersonDatabase.child(String.valueOf(userId));
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String storedPassword = snapshot.child("password").getValue(String.class);

                        if (storedPassword != null && storedPassword.equals(password)) {
                            User user = new User(userId, password);

                            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                            intent.putExtra("person",userId);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LogInActivity.this, "Error. Password Invalid",Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(LogInActivity.this,
                                "Error. User Id not found.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Snackbar.make(view, "Database Error: "+error.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            });
        } catch (NumberFormatException e)  {
            Snackbar.make(view, "Invalid User ID",Snackbar.LENGTH_LONG).show();
        } catch (Exception e) {
            Snackbar.make(view,e.getMessage(),Snackbar.LENGTH_LONG).show();
        }
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

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}
