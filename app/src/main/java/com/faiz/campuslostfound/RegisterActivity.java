package com.faiz.campuslostfound;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameInput, emailInput, passwordInput;
    private Button registerButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        registerButton = findViewById(R.id.registerButton);

        firebaseAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this,
                    "Password must contain at least 6 characters",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        registerButton.setEnabled(false);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    registerButton.setEnabled(true);

                    if (task.isSuccessful()) {

                        // Account created successfully
                        Toast.makeText(this,
                                "Account created! Please login.",
                                Toast.LENGTH_SHORT).show();

                        // Sign out the newly created user
                        firebaseAuth.signOut();

                        // Open Login screen
                        Intent intent = new Intent(
                                RegisterActivity.this,
                                MainActivity.class
                        );

                        startActivity(intent);
                        finish();

                    } else {

                        String error = task.getException() != null
                                ? task.getException().getMessage()
                                : "Registration failed";

                        Toast.makeText(this,
                                error,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}