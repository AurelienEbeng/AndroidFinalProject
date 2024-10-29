package com.example.projectfitnessapp;

import android.content.Intent;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import model.Person;
import model.User;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnCreateAccountReturn, btnCreateAccount;
    TextView tvCreateAccountUserIdData;
    EditText etPassword, etConfirmPassword, etName, etAge, etHeight, etWeight;

    DatabaseReference userDatabase, personDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_acount);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.createAccount), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initialize();
    }

    private void initialize(){
        tvCreateAccountUserIdData = findViewById(R.id.tvCreateAccountUserIdData);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        btnCreateAccountReturn = findViewById(R.id.btnCreateAccountReturn);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);

        btnCreateAccount.setOnClickListener(this);
        btnCreateAccountReturn.setOnClickListener(this);
        etWeight.setOnClickListener(this);
        etHeight.setOnClickListener(this);
        etAge.setOnClickListener(this);
        etName.setOnClickListener(this);
        etConfirmPassword.setOnClickListener(this);
        etPassword.setOnClickListener(this);

        userDatabase = FirebaseDatabase.getInstance().getReference("User");
        personDatabase = FirebaseDatabase.getInstance().getReference("Person");

        displayId();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnCreateAccount) createAccount();
        if (id == R.id.btnCreateAccountReturn) goToLoingActivity();
    }

    private void goToLoingActivity() {
        Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void createAccount() {
        // Gets all the input needed from EditText
        String password = etPassword.getText().toString().trim();
        int userId = Integer.parseInt(tvCreateAccountUserIdData.getText().toString().trim());
        int personId = userId;
        String name = etName.getText().toString().trim();
        int age = Integer.parseInt(etAge.getText().toString().trim());
        float weight = Float.parseFloat(etWeight.getText().toString().trim());
        float height = Float.parseFloat(etHeight.getText().toString().trim());
        int BMI = 0;

        Person person = new Person(personId, name, age, weight, height, BMI);
        User user = new User(userId, password);

        // Creates User in firebase
        userDatabase.child(String.valueOf(userId)).setValue(user).addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful()) {
                //Creates Person in firebase
                personDatabase.child(String.valueOf(userId)).setValue(person).addOnCompleteListener(personTask -> {
                    if (personTask.isSuccessful()) {
                        Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();

                        // Goes to LoginActivity page if person and user is created
                        Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to create Person", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Failed to create User", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayId(){
         userDatabase = FirebaseDatabase.getInstance().getReference("User"); //

        // Gets last userId
        userDatabase.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                        int userId = Integer.parseInt(userSnapshot.getKey()) + 100;
                        // Update the TextView with the retrieved user ID
                        tvCreateAccountUserIdData.setText(String.valueOf(userId));

                    }
                } else {
                    tvCreateAccountUserIdData.setText("No users found.");
                }
            }

            // Method in order to handle non existence Database.
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
                Log.e("DisplayId", "Error retrieving user ID: " + error.getMessage());
                tvCreateAccountUserIdData.setText("Error fetching user ID."); // Update UI on error
            }
        });
    }
}
