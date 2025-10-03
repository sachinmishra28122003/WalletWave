package com.example.walletwave;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class SignUp extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    DatabaseReference usersRef = database.getReference("Users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TextView LoginText = findViewById(R.id.LoginText);
        LoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUp.this, MainActivity.class);
                startActivity(i);
            }
        });

        // signUp
        EditText email = findViewById(R.id.emailEditText) ;
        EditText password = findViewById(R.id.passwordEditText);
        EditText cnfmPassword = findViewById(R.id.confirmPasswordEditText);
        Button signUp = findViewById(R.id.SignUpButton);
        EditText name = findViewById(R.id.nameEditText);
        EditText income = findViewById(R.id.incomeEditText);
        String n = name.getText().toString().trim();
        String pass = income.getText().toString().trim();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailStr = email.getText().toString().trim();
                String passwordStr = password.getText().toString().trim();
                String confirmPasswordStr = cnfmPassword.getText().toString().trim();
                String nameStr = name.getText().toString().trim();
                String incomeStr = income.getText().toString().trim();
                if (emailStr.isEmpty() || passwordStr.isEmpty() || confirmPasswordStr.isEmpty()) {
                    Toast.makeText(SignUp.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (passwordStr.length() < 6) {
                    Toast.makeText(SignUp.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!passwordStr.equals(confirmPasswordStr)) {
                    Toast.makeText(SignUp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.createUserWithEmailAndPassword(emailStr, passwordStr)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String uid = auth.getCurrentUser().getUid();

                                    HashMap<String, Object> userMap = new HashMap<>();
                                    userMap.put("name", nameStr);
                                    userMap.put("income", incomeStr);
                                    userMap.put("email", emailStr);

                                    // save data to database
                                    usersRef.child(uid).setValue(userMap)
                                                    .addOnSuccessListener(unused -> {Toast.makeText(SignUp.this,"Added data",Toast.LENGTH_SHORT).show();});
                                    Toast.makeText(SignUp.this, "Successful SignUp", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignUp.this, HomePage.class);
                                    intent.putExtra("user_name", nameStr);
                                    intent.putExtra("Password", incomeStr);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(SignUp.this, "Sign Up Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

    }
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // User is already signed in
            Intent intent = new Intent(SignUp.this, HomePage.class);
            startActivity(intent);
            finish(); // Optional: close current activity so user can't go back
        }
    }
}