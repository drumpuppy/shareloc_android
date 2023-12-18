package com.example.shareloc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shareloc.R;
import com.example.shareloc.Class.User;
import com.example.shareloc.managers.ApiManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private FirebaseAuth mAuth;
    private ApiManager apiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        apiManager = new ApiManager();
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button registerButton = findViewById(R.id.registerButton);
        TextView registerLink = findViewById(R.id.registerLink);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performRegistration();
            }
        });

        registerLink.setOnClickListener(view -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
    }

    private void performRegistration() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            if (email.isEmpty()) {
                emailEditText.setError("Email est nécéssaire");
            }
            if (password.isEmpty()) {
                passwordEditText.setError("Le mot de passe est requis");
            }
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("le mot de passe doit faire au moins 6 charactères");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            initializeUserData(firebaseUser.getUid(), email);
                        }

                        Toast.makeText(this, "Enregistrement réussi", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "la connexion à échoué.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initializeUserData(String userId, String email) {
        User newUser = new User(email, "");
        apiManager.createUser(userId, newUser);
    }
}
