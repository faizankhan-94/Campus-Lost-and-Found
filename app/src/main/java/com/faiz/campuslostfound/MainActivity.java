package com.faiz.campuslostfound;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView registerLink;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerLink = findViewById(R.id.registerLink);

        firebaseAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(v -> loginUser());

        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    RegisterActivity.class
            );
            startActivity(intent);
        });
    }

    private void loginUser() {

        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                    this,
                    "Please enter email and password",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        loginButton.setEnabled(false);

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    loginButton.setEnabled(true);

                    if (task.isSuccessful()) {

                        Toast.makeText(
                                this,
                                "Login successful!",
                                Toast.LENGTH_SHORT
                        ).show();

                        Intent intent = new Intent(
                                MainActivity.this,
                                HomeActivity.class
                        );

                        startActivity(intent);
                        finish();

                    } else {

                        Toast.makeText(
                                this,
                                "Login failed: Check your email and password",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}